package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;

public record EvaluationJob(
        UUID submissionId,
        String repositoryUrl,
        String branch,
        String commitSha,
        int passingScore,
        int maxScore,
        int timeoutSeconds,
        String graderImage,
        List<String> graderCommand) {
}
