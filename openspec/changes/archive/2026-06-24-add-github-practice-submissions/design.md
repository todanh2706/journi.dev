## Context

Journi.dev already distinguishes theory `LESSON` nodes from assessment-oriented `PRACTICE` and `PROJECT` nodes. Lesson completion is implemented, but manual completion deliberately rejects assessment nodes. The seeded Backend Java Spring Boot roadmap reaches `Collections and Generics` as its first `PRACTICE`, while the backend currently has only skeletal `Challenge` and `Submission` entities and no delivery, submission, grading, or progress integration. This leaves the main progression path blocked.

The current product direction expects learners to work in a real local IDE and use GitHub for proof of work. That is a better fit for multi-file Java, Maven, Spring Boot, PostgreSQL, security, testing, Docker, and deployment exercises than a browser editor. The main technical constraint is that submitted repositories contain untrusted code and MUST NOT execute in the public Spring Boot API process or its container.

The intended flow is:

```text
Unlocked challenge
      |
      v
Local IDE + public GitHub repository
      |
      v
Submit repository + branch + exact commit SHA
      |
      v
SUBMITTED -> EVALUATING -> PASSED --------> node COMPLETED -> unlock dependents
                         -> NEEDS_CHANGES -> new commit and submission
                         -> FAILED --------> retry infrastructure evaluation
```

## Goals / Non-Goals

**Goals:**

- Deliver complete challenge briefs for assessment nodes the authenticated learner has unlocked, independently from whether automated submission is enabled.
- Record learner-scoped GitHub submissions against an immutable commit and expose a clear asynchronous state model.
- Grade the exact revision with deterministic challenge-owned tests in an isolated runtime with strict resource and security controls.
- Preserve actionable, bounded feedback and submission history without exposing internal grader details or other learners' work.
- Make a passing required challenge the only path that completes its `PRACTICE` or `PROJECT` node.
- Fill challenge definitions and explicit evaluation-activation flags for every assessment node in the seeded Backend Java Spring Boot roadmap.
- Ship incrementally within the existing repository and use the current PostgreSQL and Redis infrastructure where appropriate.

**Non-Goals:**

- An embedded Monaco/CodeMirror editor, terminal, or browser-hosted workspace.
- Private GitHub repositories, GitHub OAuth, webhooks, pull-request automation, or repository write access.
- AI-generated grading, AI pass/fail decisions, peer review, plagiarism detection, or instructor authoring tools.
- Arbitrary user-selected build commands, unrestricted network access, production autoscaling, or multi-language grading.
- Automatic completion of `QUIZ` or `CHALLENGE` node types in this change.

## Decisions

### 1. Use one required challenge as the completion contract for each seeded `PRACTICE` or `PROJECT`

Every assessment node in the current roadmap will have exactly one required challenge. A challenge carries learner-facing instructions plus immutable evaluator configuration: acceptance criteria, hints, expected artifacts, starter repository URL, passing score, timeout, grader image pinned by digest, a fixed command/argument list, and a non-null evaluation-activation flag. `Collections and Generics` is the only enabled challenge pilot; the remaining seven definitions stay available as read-only briefs until their graders are enabled.

This closes the current seed gap and keeps completion deterministic. The schema may retain a one-to-many node/challenge relationship for future optional exercises, but this change does not aggregate scores across multiple required challenges.

Alternative considered: keep challenges only at selected milestones. Rejected because the sequential roadmap contains non-lesson nodes without any valid completion path.

### 2. Submit an immutable public GitHub revision, not editable source text

The submission API accepts only an allowlisted `https://github.com/{owner}/{repository}` URL, a validated branch name, and a full commit SHA. The normalized repository URL and SHA form the proof-of-work identity. The grader clones the repository and checks out that exact commit; it does not grade the branch head.

The API will reject locked nodes, challenges not belonging to the node, malformed or non-GitHub URLs, missing commits, and repositories that cannot be read anonymously. It will return the existing submission for repeated requests by the same learner, challenge, and commit rather than enqueueing duplicate work.

Alternative considered: accept ZIP uploads or pasted files. Rejected because they introduce storage/scanning concerns and do not match the agreed GitHub workflow.

### 3. Model attempts explicitly and use terminal states with different retry semantics

`Submission` becomes the attempt record and links to `User` and `Challenge` with JPA relationships. It stores normalized repository metadata, attempt number, status, score, bounded feedback/result JSON, timestamps, and failure category. Status is an enum:

- `SUBMITTED`: persisted and awaiting a worker.
- `EVALUATING`: exclusively claimed by a worker.
- `PASSED`: deterministic tests met the challenge passing score.
- `NEEDS_CHANGES`: the learner revision was evaluated but did not pass.
- `FAILED`: infrastructure prevented a valid evaluation.

A corrected commit creates a new attempt after `NEEDS_CHANGES`. An infrastructure retry requeues the same `FAILED` submission without incrementing the learner's attempt number. A uniqueness constraint on `(user_id, challenge_id, commit_hash)` makes submission idempotency enforceable at the database boundary.

The first accepted submission upserts node progress to `IN_PROGRESS`; it never bypasses prerequisite validation.

### 4. Keep the API and grader as separate runtime trust boundaries

The Spring Boot API validates and persists submissions, publishes the submission ID to a Redis Stream, and serves learner status. A dedicated `grader` process consumes jobs, launches evaluation containers, and records the trusted terminal result and progress transition. It reuses code from the backend Maven module under a non-web worker profile and receives a separate bootstrap secret required by shared application configuration, never the public API JWT or refresh-token secrets.

The submission row remains the source of truth. Stream messages contain only a submission ID. Worker claims use an atomic status transition; startup recovery requeues stale `SUBMITTED` records and returns stale `EVALUATING` records to a retryable state after a configured lease.

Alternative considered: run grading from an `@Async` method in the API container. Rejected because it mixes trust boundaries, couples request capacity to untrusted workloads, and would require privileged container access in the public backend.

### 5. Execute only challenge-owned commands in ephemeral, network-disabled containers

The grader clones the allowlisted public repository into a fresh workspace, verifies the requested commit, and starts a challenge-owned image pinned by digest. The learner cannot select the image, executable, arguments, mounts, environment variables, or limits. Each run uses:

- disabled outbound network;
- non-root user;
- CPU, memory, process, and wall-clock limits;
- one fresh per-submission workspace bind-mounted writable plus read-only grader assets, with no arbitrary host paths or application-secret mounts;
- read-only grader/test assets;
- capped stdout/stderr and capped structured result files;
- forced cleanup after success, failure, timeout, or worker restart.

Images include the required JDK, Maven dependencies, and hidden deterministic tests so evaluation does not need network access. Exit status and a grader-owned structured result file determine score and feedback. AI feedback is not part of the decision.

### 6. Complete progress transactionally from a trusted terminal result

Only the trusted evaluation-result path can change an assessment node from `IN_PROGRESS` to `COMPLETED`. On `PASSED`, the backend transaction locks the submission, records its first terminal result, and upserts the learner's progress while preserving an existing completion timestamp. Replayed results are idempotent. `NEEDS_CHANGES` and `FAILED` never complete or unlock the node.

The normal roadmap status computation remains unchanged: once progress is `COMPLETED`, dependent nodes become `AVAILABLE` when all prerequisites are satisfied.

### 7. Add a route-level practice workspace and poll a bounded status endpoint

An unlocked assessment node drawer always exposes the practice route at `/dashboard/roadmaps/{roadmapId}/nodes/{nodeId}/practice`. It labels the action **Start practice** when both submission flags are enabled and **View practice brief** when grading is disabled. The route owns data loading while domain components/services live under `features/practice`.

The workspace shows Brief, Acceptance criteria, Resources, Submission, and Feedback sections. When submission is disabled, the brief and starter repository remain available while the form is replaced by an explicit read-only notice. After submission, the frontend polls the learner-scoped submission endpoint with bounded backoff while status is non-terminal, pauses network polling when the tab is hidden, stops after a terminal result, and offers explicit retry on network failure. It never infers `PASSED` locally. After a pass, it refreshes roadmap nodes so completed counts and newly available nodes update without a full-page reload.

Alternative considered: keep the entire flow inside the existing drawer. Rejected because challenge instructions, submission history, and feedback require a stable route with more space and recoverable navigation.

### 8. Keep challenge details progress-gated

Locked nodes retain graph metadata only. Their challenge brief, starter repository, evaluator limits, submission form, and attempt history are unavailable. Every challenge/submission endpoint recomputes access on the backend; frontend hiding is not treated as authorization.

## API Contracts

- `GET /api/v1/skill-nodes/{nodeId}/challenge`
  - Returns the required challenge for an unlocked `PRACTICE` or `PROJECT` node.
  - Includes whether submission is currently enabled without exposing the global or evaluator configuration.
  - Returns `400` for unsupported node types, `403` for locked nodes, and `404` when no required challenge exists.
- `POST /api/v1/users/me/challenges/{challengeId}/submissions`
  - Body: `repositoryUrl`, `branch`, `commitSha`.
  - Rejects submission when the global practice flag or challenge evaluation flag is disabled.
  - Returns `202 Accepted` with the new or idempotently reused submission.
- `GET /api/v1/users/me/challenges/{challengeId}/submissions`
  - Returns only the authenticated learner's attempts, newest first.
- `GET /api/v1/users/me/submissions/{submissionId}`
  - Returns only an owned submission, including bounded feedback and timestamps.
- `POST /api/v1/users/me/submissions/{submissionId}/retry`
  - Requeues only an owned `FAILED` infrastructure evaluation and returns `202 Accepted`.

No public endpoint can directly set `PASSED`, score, feedback, or assessment-node completion.

## Persistence Changes

`Challenge` gains structured learner-facing and evaluator-owned fields. JSON fields are parsed through typed DTOs and validated during seeding; raw grader configuration is not returned to learners. `evaluation_enabled` is non-null, and the seed dataset writes an explicit value for every challenge.

`Submission` gains a `ManyToOne` challenge relationship, normalized repository URL, attempt number, enum status, score, result summary, bounded feedback/output, evaluation lease, started/completed timestamps, and the unique learner/challenge/commit constraint. Existing UUID-only fields are migrated without deleting valid rows; legacy rows without a resolvable challenge are quarantined from evaluation.

No `CodeReview` or `AiReviewTask` row is required for deterministic grading in this change. Those future domains remain separate.

## Risks / Trade-offs

- [Untrusted code can escape or exhaust the host] → Use a separate grader process, pinned images, non-root execution, disabled network, strict resource limits, no secret mounts, and production-grade isolation before public deployment.
- [Mounting a Docker socket grants host-level power] → Never mount it into the public API; restrict it to the local grader runtime and replace the adapter with a managed job boundary in production.
- [Public GitHub access is rate-limited or unavailable] → Support an optional server-side read-only GitHub token, cache metadata briefly, classify provider outages as `FAILED`, and expose retry without consuming an attempt.
- [Learners alter visible tests] → Keep pass/fail tests in pinned grader images and treat repository tests only as developer feedback.
- [Maven builds are slow and images are large] → Pre-bake dependencies, cap concurrency, reuse immutable images, and start with one Java/Spring toolchain.
- [Redis delivery can be duplicated] → Make submission creation, worker claims, result writes, and progress completion idempotent at database boundaries.
- [Polling adds request load] → Use bounded backoff, pause in hidden tabs, stop at terminal states, and return compact status responses.
- [The change is larger than the current MVP] → Deliver in vertical slices: challenge access, submission persistence, one isolated pilot grader, then seed all assessment nodes and enable progression.

## Migration Plan

1. Add challenge/submission columns, enums, indexes, and relationships. Keep legacy submission references nullable and evaluation-ineligible, but require non-null challenge activation data.
2. For disposable local databases created before the challenge schema, run the dedicated seeder with `--reset-database` to recreate the schema and seed explicit activation values; do not mask invalid challenge data with nullable entity state.
3. Seed and validate grader-ready challenge definitions. Keep practice briefs accessible while automated submission remains disabled behind global and per-challenge configuration.
4. Deploy the API changes and grader worker with no public submission activation; run end-to-end evaluation against a pilot repository.
5. Enable the `Collections and Generics` pilot, verify isolation, retries, idempotency, feedback, and progress completion.
6. Enable the remaining seeded `PRACTICE` and `PROJECT` nodes after their grader images and hidden tests pass validation.
7. Update README, SRS, ERD, API documentation, environment examples, and local Docker instructions.

Rollback disables new submissions and stops the grader worker. Existing terminal submissions and completed progress remain auditable; reverting completion requires an explicit data migration rather than deleting history.

## Open Questions

- Which container execution adapter will be accepted for production after the local Docker-based grader proves the contract?

Resolved in the MVP implementation: public GitHub verification begins unauthenticated with an optional read-only server token, and `Collections and Generics` is the first challenge-level pilot.
