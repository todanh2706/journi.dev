## ADDED Requirements

### Requirement: Progress-Gated Challenge Delivery
The backend SHALL expose the required challenge for an authenticated learner only when its `PRACTICE` or `PROJECT` node is not `LOCKED`, regardless of whether automated submission is enabled. The learner-facing response SHALL include instructions, acceptance criteria, hints, expected artifacts, starter repository URL, estimated limits, passing score, and a derived submission-enabled value, but SHALL NOT expose hidden tests, grader images, commands, secrets, raw activation flags, or another learner's submission data.

#### Scenario: Retrieve an unlocked practice challenge
- **WHEN** an authenticated learner requests `GET /api/v1/skill-nodes/{nodeId}/challenge` for an `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED` `PRACTICE` or `PROJECT` node with a required challenge
- **THEN** the backend returns the learner-facing challenge brief and current learner submission summary

#### Scenario: Reject challenge access for a locked node
- **WHEN** a learner requests a challenge whose node has status `LOCKED`
- **THEN** the backend rejects the request without returning instructions, acceptance criteria, starter-repository metadata, or evaluator details

#### Scenario: Reject unsupported node types
- **WHEN** a learner requests the challenge endpoint for a `LESSON`, `QUIZ`, or `CHALLENGE` node
- **THEN** the backend rejects the request as unsupported by this practice workflow

#### Scenario: Retrieve a brief while automated submission is disabled
- **WHEN** an authenticated learner requests an unlocked required challenge whose global practice submission flag or challenge evaluation flag is disabled
- **THEN** the backend returns the learner-facing brief with submission reported as disabled
- **THEN** no evaluator configuration is disclosed

### Requirement: Immutable GitHub Revision Submission
The system SHALL allow an authenticated learner to submit a readable public GitHub repository, branch, and full commit SHA for an unlocked required challenge only when both the global practice submission flag and challenge evaluation flag are enabled. The backend SHALL normalize and allowlist the repository URL, verify that the revision belongs to the repository, persist the exact revision as a learner-owned attempt, and prevent arbitrary clone hosts or user-selected execution commands.

#### Scenario: Submit a valid GitHub revision
- **WHEN** a learner posts a valid public GitHub repository URL, branch, and commit SHA to `POST /api/v1/users/me/challenges/{challengeId}/submissions`
- **THEN** the backend creates a `SUBMITTED` attempt owned by that learner and returns `202 Accepted`
- **THEN** the associated node progress becomes `IN_PROGRESS` without completing the node

#### Scenario: Submit the same revision again
- **WHEN** the same learner submits the same challenge and commit SHA more than once
- **THEN** the backend returns the existing submission without creating or enqueueing a duplicate evaluation

#### Scenario: Reject an invalid repository reference
- **WHEN** a submission uses a non-HTTPS URL, a host other than `github.com`, a missing repository or commit, a non-full commit SHA, or a repository that is not publicly readable
- **THEN** the backend rejects the request without creating progress, submission, or evaluation-job state

#### Scenario: Reject submission for an unavailable challenge
- **WHEN** a learner submits work for a locked node, an unsupported node type, or a challenge unrelated to the target learner-visible node
- **THEN** the backend rejects the request without changing roadmap progress

#### Scenario: Reject submission while evaluation is disabled
- **WHEN** a learner submits work while the global practice submission flag or challenge evaluation flag is disabled
- **THEN** the backend rejects the request without verifying GitHub, creating an attempt, changing progress, or publishing a queue message

### Requirement: Asynchronous Submission Lifecycle
The system SHALL represent evaluation with the persisted states `SUBMITTED`, `EVALUATING`, `PASSED`, `NEEDS_CHANGES`, and `FAILED`. Submission creation, worker claims, terminal result handling, and queue redelivery SHALL be idempotent. A learner SHALL be able to observe state and timestamps without directly setting score, feedback, terminal status, or node completion.

#### Scenario: Evaluate an accepted submission
- **WHEN** a valid submission is persisted and queued
- **THEN** one grader claim transitions it from `SUBMITTED` to `EVALUATING`
- **THEN** the accepted deterministic result transitions it exactly once to `PASSED`, `NEEDS_CHANGES`, or `FAILED`

#### Scenario: Redeliver an evaluation message
- **WHEN** Redis redelivers a job whose submission is already being evaluated or is terminal
- **THEN** the worker does not create a second attempt, overwrite the first terminal result, or repeat node completion

#### Scenario: Recover a stale evaluation
- **WHEN** a worker stops after claiming a submission and its evaluation lease expires
- **THEN** the system returns the submission to a retryable infrastructure state and permits safe re-evaluation

### Requirement: Isolated Deterministic Evaluation
The grader SHALL evaluate the exact submitted commit with challenge-owned tests and configuration inside an ephemeral isolated runtime. User code SHALL NOT execute inside the public Spring Boot API process. The runtime SHALL use a pinned grader image, non-root identity, disabled outbound network, explicit CPU/memory/process/time limits, a fresh per-submission workspace bind mount, read-only grader assets, no arbitrary host or application-secret mounts, and capped result/output sizes.

#### Scenario: Grade a passing revision
- **WHEN** the exact commit satisfies the hidden deterministic test suite and configured passing score
- **THEN** the grader records a structured `PASSED` result with bounded learner-safe feedback

#### Scenario: Grade a revision that needs changes
- **WHEN** the grader completes normally but the revision does not satisfy the passing criteria
- **THEN** the grader records `NEEDS_CHANGES`, the score, failed acceptance-criterion summaries, and bounded output without completing the node

#### Scenario: Evaluation infrastructure fails
- **WHEN** cloning, container startup, timeout enforcement, or result extraction prevents a trustworthy grading decision
- **THEN** the system records `FAILED` with a safe failure category and does not treat the learner revision as an assessment failure or complete the node

#### Scenario: Attempt to influence evaluator configuration
- **WHEN** submitted repository content requests a different image, command, environment variable, mount, network policy, or resource limit
- **THEN** the grader ignores the request and uses only the server-owned challenge configuration

### Requirement: Learner-Owned Feedback and Retry
The system SHALL expose only the authenticated learner's submission history and status. Attempts SHALL be ordered newest first and include repository revision, status, score when available, bounded feedback, and timestamps. A corrected commit after `NEEDS_CHANGES` SHALL create a new attempt, while `FAILED` infrastructure evaluation SHALL be retryable on the same attempt without consuming another learner attempt number.

#### Scenario: Review submission history
- **WHEN** a learner requests `GET /api/v1/users/me/challenges/{challengeId}/submissions`
- **THEN** the backend returns only that learner's attempts for the challenge in newest-first order

#### Scenario: Access another learner's submission
- **WHEN** an authenticated learner requests a submission owned by another user
- **THEN** the backend returns no submission details and does not disclose whether the identifier exists

#### Scenario: Submit a corrected commit
- **WHEN** a learner whose latest attempt is `NEEDS_CHANGES` submits a different valid commit SHA
- **THEN** the backend creates and evaluates a new attempt while preserving prior feedback history

#### Scenario: Retry an infrastructure failure
- **WHEN** a learner invokes the retry endpoint for an owned `FAILED` submission
- **THEN** the backend requeues that submission idempotently and does not increment its attempt number
