package journi.dev.backend.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import journi.dev.backend.entities.RefreshSession;
import journi.dev.backend.entities.RefreshSessionRevocationReason;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;

@ActiveProfiles("test")
@DataJpaTest
class RefreshSessionRepositoryTest {
    @Autowired
    private RefreshSessionRepository refreshSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("[TEST] Refresh session token digests are unique")
    @Test
    void tokenDigestMustBeUnique() {
        User user = userRepository.saveAndFlush(user("digest-user", "digest@example.com"));
        String tokenHash = "a".repeat(64);
        refreshSessionRepository.saveAndFlush(session(user, UUID.randomUUID(), tokenHash));

        assertThatThrownBy(() -> refreshSessionRepository.saveAndFlush(
                session(user, UUID.randomUUID(), tokenHash)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("[TEST] Independent refresh families coexist for one user")
    @Test
    void independentFamiliesCoexist() {
        User user = userRepository.saveAndFlush(user("family-user", "family@example.com"));
        UUID firstFamily = UUID.randomUUID();
        UUID secondFamily = UUID.randomUUID();

        refreshSessionRepository.saveAndFlush(session(user, firstFamily, "b".repeat(64)));
        refreshSessionRepository.saveAndFlush(session(user, secondFamily, "c".repeat(64)));

        assertThat(refreshSessionRepository.findAllByFamilyId(firstFamily)).hasSize(1);
        assertThat(refreshSessionRepository.findAllByFamilyId(secondFamily)).hasSize(1);
    }

    @DisplayName("[TEST] Active records in one family are revoked together")
    @Test
    void revokeActiveFamilyUpdatesOnlyMatchingActiveRows() {
        User user = userRepository.saveAndFlush(user("revoke-user", "revoke@example.com"));
        UUID familyId = UUID.randomUUID();
        UUID otherFamilyId = UUID.randomUUID();
        refreshSessionRepository.saveAndFlush(session(user, familyId, "d".repeat(64)));
        refreshSessionRepository.saveAndFlush(session(user, familyId, "e".repeat(64)));
        refreshSessionRepository.saveAndFlush(session(user, otherFamilyId, "f".repeat(64)));
        Instant revokedAt = Instant.now();

        int updated = refreshSessionRepository.revokeActiveFamily(
                familyId, revokedAt, RefreshSessionRevocationReason.REPLAY);

        assertThat(updated).isEqualTo(2);
        assertThat(refreshSessionRepository.findAllByFamilyId(familyId))
                .allSatisfy(session -> {
                    assertThat(session.getRevokedAt()).isNotNull();
                    assertThat(session.getRevocationReason()).isEqualTo(RefreshSessionRevocationReason.REPLAY);
                });
        assertThat(refreshSessionRepository.findAllByFamilyId(otherFamilyId))
                .allSatisfy(session -> assertThat(session.getRevokedAt()).isNull());
    }

    @DisplayName("[TEST] Digest lookup is available inside a pessimistic write transaction")
    @Test
    void findByTokenHashForUpdateReturnsMatchingSession() {
        User user = userRepository.saveAndFlush(user("lock-user", "lock@example.com"));
        String tokenHash = "1".repeat(64);
        RefreshSession saved = refreshSessionRepository.saveAndFlush(
                session(user, UUID.randomUUID(), tokenHash));

        assertThat(refreshSessionRepository.findByTokenHashForUpdate(tokenHash))
                .isPresent()
                .get()
                .extracting(RefreshSession::getRefreshSessionId)
                .isEqualTo(saved.getRefreshSessionId());

        // H2 confirms the JPA lock wiring only. PostgreSQL lock contention must be
        // verified before production rollout because H2 does not perfectly model it.
    }

    private RefreshSession session(User user, UUID familyId, String tokenHash) {
        RefreshSession session = new RefreshSession();
        session.setUser(user);
        session.setFamilyId(familyId);
        session.setTokenHash(tokenHash);
        session.setExpiresAt(Instant.now().plusSeconds(3600));
        return session;
    }

    private User user(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("encoded-password");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        return user;
    }
}
