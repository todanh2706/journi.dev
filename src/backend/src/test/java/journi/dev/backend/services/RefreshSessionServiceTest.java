package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import journi.dev.backend.configurations.AuthSessionProperties;
import journi.dev.backend.entities.RefreshSession;
import journi.dev.backend.entities.RefreshSessionRevocationReason;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.exceptions.RefreshSessionException;
import journi.dev.backend.repositories.RefreshSessionRepository;

@ExtendWith(MockitoExtension.class)
class RefreshSessionServiceTest {
    private static final Instant NOW = Instant.parse("2026-06-21T00:00:00Z");

    @Mock
    private RefreshSessionRepository refreshSessionRepository;

    private RefreshSessionService refreshSessionService;

    @BeforeEach
    void setUp() {
        AuthSessionProperties properties = new AuthSessionProperties();
        properties.setRefreshTokenLifetime(Duration.ofDays(30));
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        refreshSessionService = new RefreshSessionService(
                refreshSessionRepository, properties, clock, new SecureRandom());
    }

    @Test
    void issuePersistsOnlyDigestWithFixedAbsoluteExpiry() {
        User user = activeUser();
        ArgumentCaptor<RefreshSession> captor = ArgumentCaptor.forClass(RefreshSession.class);

        RefreshSessionToken issued = refreshSessionService.issue(user);

        verify(refreshSessionRepository).save(captor.capture());
        RefreshSession persisted = captor.getValue();
        assertThat(issued.value()).isNotBlank();
        assertThat(persisted.getTokenHash()).hasSize(64).isNotEqualTo(issued.value());
        assertThat(persisted.getExpiresAt()).isEqualTo(NOW.plus(Duration.ofDays(30)));
        assertThat(issued.expiresAt()).isEqualTo(persisted.getExpiresAt());
        assertThat(persisted.getFamilyId()).isNotNull();
    }

    @Test
    void separateIssuesCreateIndependentFamilies() {
        User user = activeUser();
        ArgumentCaptor<RefreshSession> captor = ArgumentCaptor.forClass(RefreshSession.class);

        refreshSessionService.issue(user);
        refreshSessionService.issue(user);

        verify(refreshSessionRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(RefreshSession::getFamilyId)
                .doesNotHaveDuplicates();
    }

    @Test
    void rotateConsumesCurrentSessionAndPreservesFamilyExpiry() {
        User user = activeUser();
        UUID familyId = UUID.randomUUID();
        Instant familyExpiry = NOW.plus(Duration.ofDays(12));
        RefreshSession current = session(user, familyId, familyExpiry);
        when(refreshSessionRepository.findByTokenHashForUpdate(any(String.class)))
                .thenReturn(Optional.of(current));
        when(refreshSessionRepository.saveAndFlush(any(RefreshSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshSessionToken rotated = refreshSessionService.rotate("current-refresh-token");

        assertThat(rotated.expiresAt()).isEqualTo(familyExpiry);
        assertThat(current.getRevokedAt()).isEqualTo(NOW);
        assertThat(current.getRevocationReason()).isEqualTo(RefreshSessionRevocationReason.ROTATED);
        assertThat(current.getReplacedBySession()).isNotNull();
        assertThat(current.getReplacedBySession().getFamilyId()).isEqualTo(familyId);
        assertThat(current.getReplacedBySession().getExpiresAt()).isEqualTo(familyExpiry);
    }

    @Test
    void rotatedCredentialReplayRevokesActiveFamily() {
        RefreshSession rotated = session(activeUser(), UUID.randomUUID(), NOW.plusSeconds(3600));
        rotated.setRevokedAt(NOW.minusSeconds(30));
        rotated.setRevocationReason(RefreshSessionRevocationReason.ROTATED);
        when(refreshSessionRepository.findByTokenHashForUpdate(any(String.class)))
                .thenReturn(Optional.of(rotated));

        assertThatThrownBy(() -> refreshSessionService.rotate("replayed-token"))
                .isInstanceOf(RefreshSessionException.class);

        verify(refreshSessionRepository).revokeActiveFamily(
                rotated.getFamilyId(), NOW, RefreshSessionRevocationReason.REPLAY);
    }

    @Test
    void expiredCredentialIsMarkedAndRejectedWithoutSuccessor() {
        RefreshSession expired = session(activeUser(), UUID.randomUUID(), NOW);
        when(refreshSessionRepository.findByTokenHashForUpdate(any(String.class)))
                .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> refreshSessionService.rotate("expired-token"))
                .isInstanceOf(RefreshSessionException.class);

        assertThat(expired.getRevokedAt()).isEqualTo(NOW);
        assertThat(expired.getRevocationReason()).isEqualTo(RefreshSessionRevocationReason.EXPIRED);
        verify(refreshSessionRepository, never()).saveAndFlush(any(RefreshSession.class));
    }

    @Test
    void logoutIsIdempotentForMissingUnknownAndRevokedCredentials() {
        refreshSessionService.revokeForLogout(null);
        refreshSessionService.revokeForLogout("");
        when(refreshSessionRepository.findByTokenHashForUpdate(any(String.class)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(revokedSession()));

        refreshSessionService.revokeForLogout("unknown-token");
        refreshSessionService.revokeForLogout("already-revoked-token");

        verify(refreshSessionRepository, never()).revokeActiveFamily(
                any(UUID.class), any(Instant.class), any(RefreshSessionRevocationReason.class));
    }

    @Test
    void logoutRevokesTheCurrentActiveFamily() {
        RefreshSession current = session(activeUser(), UUID.randomUUID(), NOW.plusSeconds(3600));
        when(refreshSessionRepository.findByTokenHashForUpdate(any(String.class)))
                .thenReturn(Optional.of(current));

        refreshSessionService.revokeForLogout("active-token");

        verify(refreshSessionRepository).revokeActiveFamily(
                current.getFamilyId(), NOW, RefreshSessionRevocationReason.LOGOUT);
    }

    private RefreshSession session(User user, UUID familyId, Instant expiresAt) {
        RefreshSession session = new RefreshSession();
        session.setUser(user);
        session.setFamilyId(familyId);
        session.setTokenHash("f".repeat(64));
        session.setExpiresAt(expiresAt);
        return session;
    }

    private RefreshSession revokedSession() {
        RefreshSession session = session(activeUser(), UUID.randomUUID(), NOW.plusSeconds(3600));
        session.setRevokedAt(NOW.minusSeconds(10));
        session.setRevocationReason(RefreshSessionRevocationReason.LOGOUT);
        return session;
    }

    private User activeUser() {
        User user = new User();
        user.setUsername("journi-user");
        user.setEnabled(true);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
