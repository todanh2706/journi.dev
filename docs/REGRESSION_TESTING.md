# Regression Testing Inventory

This inventory maps the current Journi.dev feature surface to the automated checks that protect it today. It is intentionally feature-first: contributors should be able to answer "what protects this behavior?" without reverse-engineering the whole test tree.

## Primary Commands

Run the smallest entry point that matches your change:

- Backend regression suite: `cd src/backend && ./mvnw test`
- Frontend feature logic regression: `cd src/frontend && npm run test:feature-regression`
- Seeded roadmap API verification: `cd src/frontend && npm run verify:roadmap-api`

`verify:roadmap-api` expects the local stack to be reachable at `http://localhost:8000/api/v1` by default. Override it with `JOURNI_API_BASE_URL` when you need a different host or port.

## Coverage Matrix

| Feature area | Automated coverage | What it protects |
| --- | --- | --- |
| Auth and session backend | `AuthenticationServiceTest`, `AuthSessionServiceTest`, `RefreshSessionServiceTest`, `RefreshSessionConcurrencyTest`, `RefreshCookieServiceTest`, `JwtServiceTest`, `JwtFilterTest`, `AuthenticationControllerTest`, `AuthenticationSecurityTest` | Signup validation, login/authentication, JWT issuance, refresh rotation, logout behavior, CSRF bootstrap, and auth security constraints. |
| Auth-adjacent frontend guardrails | `src/frontend/tests/auth-guardrails.test.mjs` | JWT payload decoding for session bootstrap and auth endpoint normalization used by the Axios refresh interceptor. |
| Roadmap catalog, node detail gating, and roadmap graph persistence | `SkillNodeServiceTest`, `RoadmapDomainRepositoryTest`, `src/frontend/tests/verify-roadmap-api.mjs` | Ordered node retrieval, locked-detail redaction, unlocked learning detail projection, and the seeded roadmap contract returned to authenticated learners. |
| Lesson completion and prerequisite unlocking | `UserNodeProgressServiceTest`, `src/frontend/tests/roadmap-node-details.test.mjs`, `src/frontend/tests/verify-roadmap-api.mjs` | Manual completion eligibility, idempotent lesson completion, dependent-node unlocking, and the frontend refresh flow after a completion request succeeds. |
| Practice challenge access | `ChallengeServiceTest`, `SkillNodeControllerTest`, `src/frontend/tests/roadmap-node-details.test.mjs`, `src/frontend/tests/verify-roadmap-api.mjs` | Unlocked challenge access only for supported assessment nodes, learner-safe challenge payloads, curated starter repository delivery, and the roadmap-side practice action presentation. |
| Submission lifecycle | `SubmissionServiceTest`, `SubmissionRepositoryTest`, `SubmissionControllerTest`, `src/frontend/tests/roadmap-node-details.test.mjs` | Submission creation, duplicate commit idempotency, learner-scoped history/detail lookup, retry semantics, polling behavior, validation rules, and learner-facing status presentation. |
| Seed data invariants | `RoadmapSeedServiceTest`, `docs/PRACTICE_STARTER_REPOSITORIES.md`, `src/frontend/tests/verify-roadmap-api.mjs`, grader contract tests such as `CollectionsGraderContractTest` and `RemainingGraderContractsTest` | Seeded roadmap shape, challenge metadata, curated starter repository mappings, starter repository formatting, grader contract payloads, and the expected first-unlock roadmap flow. |

## Deferred Gaps

- No browser-level end-to-end test currently exercises rendered auth restoration, roadmap canvas interaction, or the full practice workspace UI. Confidence there still comes from service tests, focused frontend logic tests, and the seeded API verification script.
- The default local regression flow does not perform live GitHub revision checks or run the full isolated grader worker against a submitted repository. Those behaviors remain protected by backend unit tests and should still receive focused manual validation when infrastructure code changes.
