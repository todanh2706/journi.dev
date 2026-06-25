package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import journi.dev.backend.entities.SubmissionStatus;

public record SubmissionSummaryResponse(
        UUID submissionId,
        int attemptNumber,
        SubmissionStatus status,
        Integer score,
        LocalDateTime submittedAt,
        LocalDateTime completedAt) {
}
