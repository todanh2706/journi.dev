package journi.dev.backend.services;

import java.util.UUID;

public interface SubmissionJobPublisher {
    void publish(UUID submissionId);
}
