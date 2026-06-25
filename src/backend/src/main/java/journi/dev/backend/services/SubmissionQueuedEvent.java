package journi.dev.backend.services;

import java.util.UUID;

public record SubmissionQueuedEvent(UUID submissionId) {
}
