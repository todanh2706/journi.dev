package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import journi.dev.backend.entities.SubmissionStatus;

public record SubmissionResponse(
        UUID submissionId,
        UUID challengeId,
        String repositoryUrl,
        String branch,
        String commitSha,
        int attemptNumber,
        SubmissionStatus status,
        Integer score,
        String resultSummary,
        List<CriterionFeedbackResponse> feedback,
        String outputExcerpt,
        boolean retryable,
        LocalDateTime submittedAt,
        LocalDateTime evaluationStartedAt,
        LocalDateTime completedAt) {
}
