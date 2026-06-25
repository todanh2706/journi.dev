package journi.dev.backend.services;

import java.util.List;

import journi.dev.backend.entities.SubmissionStatus;

public record EvaluationResult(
        SubmissionStatus status,
        int score,
        String summary,
        List<CriterionResult> criteria,
        String outputExcerpt) {

    public record CriterionResult(String criterion, boolean passed, String message) {
    }
}
