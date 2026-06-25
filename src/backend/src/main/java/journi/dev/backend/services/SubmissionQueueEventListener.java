package journi.dev.backend.services;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SubmissionQueueEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionQueueEventListener.class);
    private final SubmissionJobPublisher submissionJobPublisher;

    public SubmissionQueueEventListener(SubmissionJobPublisher submissionJobPublisher) {
        this.submissionJobPublisher = submissionJobPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void queueSubmission(SubmissionQueuedEvent event) {
        try {
            submissionJobPublisher.publish(event.submissionId());
        } catch (RuntimeException exception) {
            LOGGER.error("Submission {} was committed but could not be queued; worker recovery will republish it",
                    event.submissionId(), exception);
        }
    }
}
