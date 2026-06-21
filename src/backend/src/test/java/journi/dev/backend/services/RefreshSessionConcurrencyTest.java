package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import journi.dev.backend.entities.RefreshSession;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.exceptions.RefreshSessionException;
import journi.dev.backend.repositories.RefreshSessionRepository;
import journi.dev.backend.repositories.UserRepository;

@ActiveProfiles("test")
@SpringBootTest
class RefreshSessionConcurrencyTest {
    @Autowired
    private RefreshSessionService refreshSessionService;

    @Autowired
    private RefreshSessionRepository refreshSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private ExecutorService executor;

    @AfterEach
    void tearDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    void concurrentRotationAllowsOnlyOneSuccessAndTreatsSecondUseAsReplay() throws Exception {
        User user = userRepository.saveAndFlush(activeUser());
        RefreshSessionToken issued = refreshSessionService.issue(user);
        RefreshSession initial = refreshSessionRepository
                .findByTokenHash(refreshSessionService.hashToken(issued.value()))
                .orElseThrow();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        executor = Executors.newFixedThreadPool(2);

        Future<Boolean> first = executor.submit(() -> rotateAfterBarrier(issued.value(), ready, start));
        Future<Boolean> second = executor.submit(() -> rotateAfterBarrier(issued.value(), ready, start));
        ready.await();
        start.countDown();

        assertThat(List.of(first.get(), second.get())).containsExactlyInAnyOrder(true, false);
        assertThat(refreshSessionRepository.findAllByFamilyId(initial.getFamilyId()))
                .allSatisfy(session -> assertThat(session.getRevokedAt()).isNotNull());

        // H2 exercises the transactional race best-effort. Verify SELECT ... FOR UPDATE
        // contention against PostgreSQL before production rollout.
    }

    private boolean rotateAfterBarrier(String token, CountDownLatch ready, CountDownLatch start)
            throws InterruptedException {
        ready.countDown();
        start.await();
        try {
            refreshSessionService.rotate(token);
            return true;
        } catch (RefreshSessionException exception) {
            return false;
        }
    }

    private User activeUser() {
        User user = new User();
        user.setUsername("concurrency-user");
        user.setEmail("concurrency@example.com");
        user.setPasswordHash("encoded-password");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        return user;
    }
}
