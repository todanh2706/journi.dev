package journi.dev.backend.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import journi.dev.backend.configurations.AuthSessionProperties;
import journi.dev.backend.entities.RefreshSession;
import journi.dev.backend.entities.RefreshSessionRevocationReason;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.exceptions.RefreshSessionException;
import journi.dev.backend.repositories.RefreshSessionRepository;

@Service
public class RefreshSessionService {
    private static final int TOKEN_BYTES = 32;
    private static final int CLEANUP_FAMILY_LIMIT = 100;

    private final RefreshSessionRepository refreshSessionRepository;
    private final AuthSessionProperties properties;
    private final Clock clock;
    private final SecureRandom secureRandom;

    public RefreshSessionService(
            RefreshSessionRepository refreshSessionRepository,
            AuthSessionProperties properties,
            Clock clock,
            SecureRandom secureRandom) {
        this.refreshSessionRepository = refreshSessionRepository;
        this.properties = properties;
        this.clock = clock;
        this.secureRandom = secureRandom;
    }

    @Transactional
    public RefreshSessionToken issue(User user) {
        Instant now = clock.instant();
        cleanupExpiredFamilies(now);

        String rawToken = generateToken();
        Instant expiresAt = now.plus(properties.getRefreshTokenLifetime());
        RefreshSession session = newSession(user, UUID.randomUUID(), rawToken, expiresAt);
        refreshSessionRepository.save(session);

        return new RefreshSessionToken(rawToken, expiresAt, user);
    }

    @Transactional(noRollbackFor = RefreshSessionException.class)
    public RefreshSessionToken rotate(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            throw new RefreshSessionException();
        }

        RefreshSession current = refreshSessionRepository.findByTokenHashForUpdate(hashToken(rawToken))
                .orElseThrow(RefreshSessionException::new);
        Instant now = clock.instant();

        if (current.getRevokedAt() != null) {
            if (current.getRevocationReason() == RefreshSessionRevocationReason.ROTATED) {
                refreshSessionRepository.revokeActiveFamily(
                        current.getFamilyId(), now, RefreshSessionRevocationReason.REPLAY);
            }
            throw new RefreshSessionException();
        }

        if (!current.getExpiresAt().isAfter(now)) {
            current.setRevokedAt(now);
            current.setRevocationReason(RefreshSessionRevocationReason.EXPIRED);
            refreshSessionRepository.save(current);
            throw new RefreshSessionException();
        }

        User user = current.getUser();
        if (!user.isEnabled() || user.getStatus() != UserStatus.ACTIVE) {
            refreshSessionRepository.revokeActiveFamily(
                    current.getFamilyId(), now, RefreshSessionRevocationReason.ACCOUNT_DISABLED);
            throw new RefreshSessionException();
        }

        String successorToken = generateToken();
        RefreshSession successor = newSession(
                user, current.getFamilyId(), successorToken, current.getExpiresAt());
        successor = refreshSessionRepository.saveAndFlush(successor);

        current.setLastUsedAt(now);
        current.setRevokedAt(now);
        current.setRevocationReason(RefreshSessionRevocationReason.ROTATED);
        current.setReplacedBySession(successor);
        refreshSessionRepository.save(current);

        return new RefreshSessionToken(successorToken, current.getExpiresAt(), user);
    }

    @Transactional
    public void revokeForLogout(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            return;
        }

        refreshSessionRepository.findByTokenHashForUpdate(hashToken(rawToken))
                .ifPresent(session -> revokeKnownSessionForLogout(session, clock.instant()));
    }

    String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private RefreshSession newSession(User user, UUID familyId, String rawToken, Instant expiresAt) {
        RefreshSession session = new RefreshSession();
        session.setUser(user);
        session.setFamilyId(familyId);
        session.setTokenHash(hashToken(rawToken));
        session.setExpiresAt(expiresAt);
        return session;
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void revokeKnownSessionForLogout(RefreshSession session, Instant now) {
        if (session.getRevokedAt() != null) {
            return;
        }
        if (!session.getExpiresAt().isAfter(now)) {
            session.setRevokedAt(now);
            session.setRevocationReason(RefreshSessionRevocationReason.EXPIRED);
            refreshSessionRepository.save(session);
            return;
        }
        refreshSessionRepository.revokeActiveFamily(
                session.getFamilyId(), now, RefreshSessionRevocationReason.LOGOUT);
    }

    private void cleanupExpiredFamilies(Instant now) {
        List<UUID> familyIds = refreshSessionRepository.findExpiredFamilyIds(
                now, PageRequest.of(0, CLEANUP_FAMILY_LIMIT));
        if (!familyIds.isEmpty()) {
            refreshSessionRepository.deleteFamilies(familyIds);
        }
    }
}
