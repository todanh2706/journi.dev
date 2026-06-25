package journi.dev.backend.entities;

public enum SubmissionStatus {
    SUBMITTED,
    EVALUATING,
    PASSED,
    NEEDS_CHANGES,
    FAILED;

    public boolean isTerminal() {
        return this == PASSED || this == NEEDS_CHANGES || this == FAILED;
    }

    public boolean isActive() {
        return this == SUBMITTED || this == EVALUATING;
    }
}
