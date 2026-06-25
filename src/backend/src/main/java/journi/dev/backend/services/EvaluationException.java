package journi.dev.backend.services;

import journi.dev.backend.entities.SubmissionFailureCategory;

public class EvaluationException extends Exception {
    private final SubmissionFailureCategory category;

    public EvaluationException(SubmissionFailureCategory category, String message) {
        super(message);
        this.category = category;
    }

    public EvaluationException(SubmissionFailureCategory category, String message, Throwable cause) {
        super(message, cause);
        this.category = category;
    }

    public SubmissionFailureCategory getCategory() {
        return category;
    }
}
