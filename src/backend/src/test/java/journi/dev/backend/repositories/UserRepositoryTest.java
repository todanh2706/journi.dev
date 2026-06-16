package journi.dev.backend.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    private static final String EXISTING_USERNAME = "test_user";
    private static final String EXISTING_EMAIL = "testuser@gmail.com";
    private static final String EXISTING_VERIFICATION_CODE = "verify-123";
    private static final String MISSING_USERNAME = "missing_user";
    private static final String MISSING_EMAIL = "missing@gmail.com";
    private static final String MISSING_VERIFICATION_CODE = "missing-code";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Autowired
    private UserRepository userRepository;

    @DisplayName("[TEST] findByUsername returns the matching user")
    @Test
    void findByUsernameReturnsMatchingUser() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Persist mock user into isolated H2 test database
        User savedUser = userRepository.saveAndFlush(TestUsers.validUser());

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThat(userRepository.findByUsername(EXISTING_USERNAME))
                .isPresent()
                .get()
                .extracting(User::getUserId)
                .isEqualTo(savedUser.getUserId());
    }

    @DisplayName("[TEST] findByUsername returns empty when no user matches")
    @Test
    void findByUsernameReturnsEmptyWhenUserDoesNotExist() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Keep the isolated H2 test database empty for this negative scenario

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThat(userRepository.findByUsername(MISSING_USERNAME)).isEmpty();
    }

    @DisplayName("[TEST] existsByUsername and existsByEmail report persisted users")
    @Test
    void existsQueriesReturnTrueForPersistedUser() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Persist mock user into isolated H2 test database
        userRepository.saveAndFlush(TestUsers.validUser());

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertAll(
                // assertAll evaluates every repository query, so one failure does not hide the others
                () -> assertThat(userRepository.existsByUsername(EXISTING_USERNAME)).isTrue(),
                () -> assertThat(userRepository.existsByUsername(MISSING_USERNAME)).isFalse(),
                () -> assertThat(userRepository.existsByEmail(EXISTING_EMAIL)).isTrue(),
                () -> assertThat(userRepository.existsByEmail(MISSING_EMAIL)).isFalse());
    }

    @DisplayName("[TEST] findByVerificationCode returns the matching user")
    @Test
    void findByVerificationCodeReturnsMatchingUser() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Persist mock user with verification code into isolated H2 test database
        User savedUser = userRepository.saveAndFlush(TestUsers.validUser());

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThat(userRepository.findByVerificationCode(EXISTING_VERIFICATION_CODE))
                .isPresent()
                .get()
                .extracting(User::getUserId)
                .isEqualTo(savedUser.getUserId());
    }

    @DisplayName("[TEST] findByVerificationCode returns empty when no code matches")
    @Test
    void findByVerificationCodeReturnsEmptyWhenCodeDoesNotExist() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Keep the isolated H2 test database empty for this negative scenario

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThat(userRepository.findByVerificationCode(MISSING_VERIFICATION_CODE)).isEmpty();
    }

    private static final class TestUsers {

        private TestUsers() {
        }

        static User validUser() {
            return builder().build();
        }

        static Builder builder() {
            return new Builder();
        }

        private static final class Builder {
            private String username = EXISTING_USERNAME;
            private String email = EXISTING_EMAIL;
            private String verificationCode = EXISTING_VERIFICATION_CODE;

            private Builder() {
            }

            private User build() {
                // Test Data Builder: keep valid User defaults in one place for repository tests
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPasswordHash(ENCODED_PASSWORD);
                user.setRole(UserRole.USER);
                user.setStatus(UserStatus.ACTIVE);
                user.setEnabled(true);
                user.setVerificationCode(verificationCode);
                user.setVerificationExpiration(LocalDateTime.now().plusHours(1));
                return user;
            }
        }
    }
}
