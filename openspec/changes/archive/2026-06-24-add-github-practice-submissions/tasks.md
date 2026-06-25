## 1. Persist Challenge and Submission Contracts

- [x] 1.1 Add typed submission-status and failure-category enums, strengthen `Challenge` and `Submission` JPA relationships, and add the learner/challenge/commit uniqueness constraint without exposing entity objects from APIs.
- [x] 1.2 Add challenge fields for instructions, acceptance criteria, hints, expected artifacts, starter repository, passing score, timeout, pinned grader image, and fixed command arguments, keeping evaluator-only fields out of learner responses.
- [x] 1.3 Add submission fields for normalized repository URL, branch, full commit SHA, attempt number, score, bounded result/feedback, evaluation lease, and lifecycle timestamps.
- [x] 1.4 Add `SubmissionRepository` owner-scoped history, idempotency, attempt-number, and atomic worker-claim queries plus focused repository tests for constraints and ordering.
- [x] 1.5 Define the compatibility/migration handling for any legacy submission rows and update entity/schema integration tests so unresolved legacy rows cannot be evaluated.

## 2. Complete and Validate Seeded Practice Definitions

- [x] 2.1 Extend the roadmap seed-data records and loader validation for structured challenge fields, pinned image digests, fixed command arrays, timeouts, passing scores, HTTPS starter repositories, and non-empty learner-facing criteria.
- [x] 2.2 Add one required deterministic challenge with explicit non-null activation to each of the six seeded `PRACTICE` and two seeded `PROJECT` nodes, using `Collections and Generics` as the only enabled pilot and preserving the cumulative Book Catalog project from REST through deployment.
- [x] 2.3 Make challenge seeding idempotently upsert only the scoped predefined challenge data without duplicating rows or deleting unrelated learner submissions.
- [x] 2.4 Extend seed tests to assert exact assessment-node coverage, no required lesson challenges, complete learner/evaluator metadata, pinned images, valid starter URLs, and identical results after two seed runs.

## 3. Expose Progress-Gated Challenge Delivery

- [x] 3.1 Add explicit learner-facing challenge and current-submission-summary response DTOs that omit grader image, command, hidden tests, internal failure details, and other users' data.
- [x] 3.2 Add a challenge service that loads the required challenge, recomputes the authenticated learner's node status, supports only `PRACTICE` and `PROJECT`, and rejects locked, unsupported, or missing-challenge requests with deliberate HTTP errors.
- [x] 3.3 Add `GET /api/v1/skill-nodes/{nodeId}/challenge` through a thin controller and document it in the generated OpenAPI contract.
- [x] 3.4 Add service/controller tests for available, in-progress, completed, locked, unsupported-type, missing-challenge, evaluator-redaction, and current-user-summary behavior.

## 4. Implement GitHub Submission APIs

- [x] 4.1 Add validated request/response DTOs for repository URL, branch, and full commit SHA plus typed attempt status, score, safe feedback, and timestamps.
- [x] 4.2 Implement a GitHub revision verifier that accepts only normalized `https://github.com/{owner}/{repository}` URLs, verifies public repository and commit availability, uses an optional read-only server token, and never follows requests to arbitrary hosts.
- [x] 4.3 Implement transactional submission creation that validates challenge access, returns an existing learner/challenge/commit attempt idempotently, assigns the next attempt number, sets node progress to `IN_PROGRESS`, and publishes a queue message only after commit.
- [x] 4.4 Implement learner-owned submission history and single-submission reads with newest-first ordering and non-disclosing not-found behavior for another learner's identifier.
- [x] 4.5 Implement idempotent retry for owned `FAILED` infrastructure submissions while rejecting retry for active, passed, needs-changes, or foreign submissions.
- [x] 4.6 Add thin REST endpoints for create, history, detail, and retry under `/api/v1/users/me/...` using the existing authenticated-user resolution pattern.
- [x] 4.7 Add backend tests for URL/branch/SHA validation, locked access, commit verification failures, duplicate POSTs, concurrent duplicate inserts, attempt numbering, ownership isolation, retry rules, progress `IN_PROGRESS`, and after-commit publication.

## 5. Build Reliable Evaluation Orchestration

- [x] 5.1 Add Redis Stream configuration, a submission-ID-only producer, a consumer group, acknowledgement rules, and configurable lease/recovery settings without placing repository or grader secrets in messages.
- [x] 5.2 Add a dedicated non-web grader process/profile that consumes jobs, atomically claims `SUBMITTED` rows as `EVALUATING`, skips active/terminal redeliveries, and recovers stale leases.
- [x] 5.3 Implement a workspace manager that clones only normalized GitHub repositories, verifies and checks out the exact submitted commit SHA, caps clone size/time/output, and reliably removes temporary data.
- [x] 5.4 Implement a fixed-argument container runner that uses only challenge-owned pinned images and commands with disabled network, non-root execution, CPU/memory/process/time limits, safe mounts, no application secrets, and forced cleanup.
- [x] 5.5 Parse grader-owned structured results into bounded score, acceptance-criterion feedback, output excerpt, and safe failure categories while rejecting malformed or oversized result files.
- [x] 5.6 Implement idempotent terminal-result persistence so `PASSED`, `NEEDS_CHANGES`, and `FAILED` cannot be overwritten by replayed or late worker results.
- [x] 5.7 Add worker tests for queue redelivery, exclusive claims, stale recovery, clone/commit mismatch, timeout, malformed result, output caps, cleanup, server-owned command enforcement, and terminal-result idempotency.

## 6. Connect Passing Results to Roadmap Progress

- [x] 6.1 Add a trusted service path that completes only the submitting learner's `PRACTICE` or `PROJECT` node from a required `PASSED` submission and preserves the first completion timestamp.
- [x] 6.2 Keep `SUBMITTED`, `EVALUATING`, `NEEDS_CHANGES`, and `FAILED` nodes `IN_PROGRESS`, continue rejecting the public lesson-completion endpoint for assessment nodes, and leave dependent nodes locked.
- [x] 6.3 Add transactional/concurrency tests for passing-result replay, progress uniqueness, first-timestamp preservation, unsuccessful results, wrong-user/wrong-challenge results, and dependent-node unlock computation.

## 7. Add the Frontend Practice Workspace

- [x] 7.1 Add `features/practice` types and shared-Axios service methods for challenge loading, submission creation, history/detail reads, and infrastructure retry.
- [x] 7.2 Add the protected `/dashboard/roadmaps/:roadmapId/nodes/:nodeId/practice` route and route-level page with loading, locked, unsupported, empty, error, and retry states.
- [x] 7.3 Update the roadmap node drawer to show **Start practice** for unlocked submission-enabled assessments, **View practice brief** for unlocked read-only assessments, a history route for completed assessments, and no action for locked or unsupported nodes.
- [x] 7.4 Build accessible Brief, Acceptance criteria, Resources, Submission, and Feedback sections with a safe starter-repository link and focus restoration/navigation back to the roadmap.
- [x] 7.5 Build the repository/branch/commit form with field validation, pending duplicate protection, retained values on failure, API-error mapping, and idempotent-success handling.
- [x] 7.6 Add bounded-backoff status polling that pauses for hidden tabs, stops for terminal states or unmount, avoids overlapping requests, and provides explicit network retry.
- [x] 7.7 Render distinct submitted, evaluating, needs-changes, failed, and passed states; support corrected-commit submission or infrastructure retry according to backend rules.
- [x] 7.8 Refresh roadmap-node data after `PASSED` so completed counts and dependent availability update while practice-route focus and result context remain stable.
- [x] 7.9 Add frontend tests for enabled and read-only practice-action visibility, locked gating, form validation, duplicate prevention, polling lifecycle, ownership-safe errors, retry semantics, feedback rendering, successful refresh, navigation, and focus behavior.

## 8. Package the Isolated Local Grader

- [x] 8.1 Add the grader process to local Docker Compose without changing existing service names or ports, grant container-launch capability only to the grader boundary, and keep the public backend unprivileged.
- [x] 8.2 Add the first pinned Java grader image and hidden deterministic test bundle for `Collections and Generics`, with preloaded dependencies so evaluation runs with outbound network disabled.
- [x] 8.3 Add grader images/test bundles for the remaining seeded practice/project contracts and validate their pass/fail fixtures before enabling each challenge.
- [x] 8.4 Add safe `.env.example` settings for queue names, concurrency, leases, GitHub read token, workspace/output limits, and challenge enablement without committing real credentials.

## 9. Verify, Document, and Roll Out

- [ ] 9.1 Run backend unit/integration tests and add an end-to-end test that submits a known passing and failing public pilot commit through API, queue, grader, result, progress, and unlock transitions.
- [x] 9.2 Run frontend tests, lint, and production build after the practice route, services, polling, and roadmap-refresh changes.
- [x] 9.3 Run isolation abuse checks for arbitrary-host URLs, command injection strings, symlinks, fork bombs, oversized output, timeouts, network access, secret access, and container/workspace cleanup; do not enable public grading until they pass.
- [x] 9.4 Run the enriched seeder twice and verify all six `PRACTICE` and two `PROJECT` nodes have one valid required challenge with no duplicate challenge records.
- [ ] 9.5 Enable the global local-development submission flag and manually verify the authenticated learner flow from the `Collections and Generics` pilot through `NEEDS_CHANGES`, corrected commit, `PASSED`, completed progress, and next-node unlock without a full-page reload.
- [x] 9.6 Update README, SRS, ERD, OpenAPI notes, local Docker instructions, and security/rollback guidance to distinguish implemented deterministic GitHub grading from future AI review, webhooks, private repositories, and mini IDE work.
