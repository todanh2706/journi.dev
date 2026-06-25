package journi.dev.backend.entities;

public enum SubmissionFailureCategory {
    GITHUB_UNAVAILABLE,
    CLONE_FAILED,
    COMMIT_NOT_FOUND,
    RUNNER_START_FAILED,
    TIMEOUT,
    RESULT_INVALID,
    WORKER_LOST,
    INTERNAL_ERROR
}
