## Why

The roadmap currently becomes unusable at the first `PRACTICE` node because non-lesson nodes cannot be self-completed and the existing challenge/submission entities are not connected to an API, evaluator, or progress transition. Journi.dev needs a safe proof-of-work loop that lets learners use their normal development tools, submit an exact GitHub revision, receive deterministic feedback, and unlock the roadmap only after passing.

## What Changes

- Expose progress-gated practice briefs for unlocked `PRACTICE` and `PROJECT` nodes, including instructions, acceptance criteria, hints, starter-repository metadata, expected artifacts, and evaluation limits, even while automated submission is disabled.
- Let an authenticated learner start an attempt and submit a public GitHub repository URL, branch, and immutable commit SHA only when both global practice submission and challenge-level evaluation are enabled.
- Validate repository and commit metadata, prevent duplicate active evaluation for the same learner/challenge/commit, and track explicit submission states from `SUBMITTED` through `EVALUATING`, `PASSED`, `NEEDS_CHANGES`, or `FAILED`.
- Evaluate the exact submitted revision with challenge-owned deterministic tests in an isolated runner; user code is never executed inside the Spring Boot API process.
- Store bounded test output and structured feedback, expose submission status to the learner, and support a corrected commit as a new attempt after an unsuccessful result.
- Mark the associated node `COMPLETED` only when a required submission passes, then reuse the existing prerequisite computation to unlock dependent nodes.
- Add a focused practice workspace to the authenticated roadmap flow while keeping code authoring in the learner's local IDE and GitHub repository; an embedded mini IDE and AI-authored pass/fail decisions are explicitly out of scope.
- Enrich every seeded Backend Java Spring Boot `PRACTICE` and `PROJECT` node with a complete deterministic challenge definition and explicit activation state; enable `Collections and Generics` as the first pilot while keeping the remaining graders read-only until validated.

## Capabilities

### New Capabilities
- `github-practice-submissions`: Progress-gated challenge delivery, GitHub revision submission, isolated deterministic evaluation, retryable feedback, and learner-scoped submission history.

### Modified Capabilities
- `assessment-community-and-integrations`: Distinguish the implemented deterministic submission lifecycle from future peer-review, AI-review, webhook, and connected-repository scaffolding.
- `learning-roadmap-domain`: Complete assessment-oriented nodes from a passing required submission instead of learner self-confirmation, then unlock dependents through the existing prerequisite rules.
- `roadmap-seed-data`: Require complete challenge and grader metadata for every seeded `PRACTICE` and `PROJECT` node.
- `roadmap-view`: Add practice entry, submission, evaluation-status, feedback, retry, and passed-state behavior to the authenticated roadmap experience.
- `system-architecture`: Document the five-service local topology, authenticated API baseline, and dedicated grader trust boundary.

## Impact

- Backend: challenge/submission DTOs, repositories, services, controllers, validation, progress integration, evaluation job orchestration, and result persistence.
- Frontend: a roadmap-owned practice workspace, typed challenge/submission services, polling or bounded status refresh, and passed/failed feedback states.
- Data model: stronger `Challenge`/`Submission` relationships plus challenge instructions, acceptance criteria, starter repository, evaluator configuration, attempt metadata, and evaluation result fields.
- Runtime: an isolated grader boundary with strict CPU, memory, timeout, filesystem, network, and output limits; Redis coordinates asynchronous jobs, and the API never executes untrusted code.
- Seed/docs: Backend Java Spring Boot challenge data, ERD/SRS/API documentation, local grader setup, and security guidance must be updated.
- Scope exclusions: private repository authorization, GitHub webhook ingestion, AI code review, peer review, production autoscaling, and a browser-based mini IDE.
