package journi.dev.backend.dtos.responses;

import java.util.List;
import java.util.UUID;

import journi.dev.backend.entities.ProgressStatus;

public record ChallengeResponse(
        UUID challengeId,
        UUID nodeId,
        String title,
        String description,
        String difficulty,
        String instructions,
        List<String> acceptanceCriteria,
        List<String> hints,
        List<String> expectedArtifacts,
        String starterRepositoryUrl,
        int maxScore,
        int passingScore,
        int timeoutSeconds,
        boolean required,
        boolean submissionEnabled,
        ProgressStatus progressStatus,
        SubmissionSummaryResponse currentSubmission) {
}
