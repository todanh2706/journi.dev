package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

class SubmissionQueueEventListenerTest {
    @Test
    void listenerPublishesOnlyInAfterCommitPhase() throws Exception {
        SubmissionJobPublisher publisher = org.mockito.Mockito.mock(SubmissionJobPublisher.class);
        SubmissionQueueEventListener listener = new SubmissionQueueEventListener(publisher);
        UUID submissionId = UUID.randomUUID();

        listener.queueSubmission(new SubmissionQueuedEvent(submissionId));

        verify(publisher).publish(submissionId);
        Method method = SubmissionQueueEventListener.class
                .getMethod("queueSubmission", SubmissionQueuedEvent.class);
        TransactionalEventListener annotation = method.getAnnotation(TransactionalEventListener.class);
        assertThat(annotation.phase()).isEqualTo(TransactionPhase.AFTER_COMMIT);
    }
}
