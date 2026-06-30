## Context

Journi.dev already contains a non-trivial automated test suite, especially on the backend: service tests for auth sessions, roadmap progress, practice submission, grader contracts, controller tests, and repository tests are all present. The frontend is thinner, with targeted Node-based checks for roadmap/practice logic and one API verification script, but there is no single inventory that tells contributors which implemented features are already protected, which are only partially covered, and which regressions still depend on manual verification.

The requested change is intentionally cross-cutting but still incremental. It should improve confidence in features that already exist without introducing a heavy new test framework, a coverage-percentage gate, or a broad rewrite of how tests are organized.

## Goals / Non-Goals

**Goals:**
- Produce an explicit audit of existing feature coverage across backend and frontend tests.
- Define the minimum regression expectations for the features the repo already ships today.
- Add or revise targeted tests where important current behavior is uncovered or under-specified.
- Keep test execution lightweight and discoverable through the commands already used in the repo.

**Non-Goals:**
- Rebuild the whole test strategy around a new coverage tool or end-to-end browser framework.
- Require exhaustive UI rendering tests for every screen.
- Change product requirements or add new user-facing features as part of the test pass.
- Guarantee live-network verification against external systems such as GitHub from the normal local test suite.

## Decisions

### Decision: Organize the work around implemented feature areas, not blanket coverage targets
The change will start from a feature inventory for auth/session, roadmap catalog/detail, node completion/unlocking, practice submission lifecycle, and seed-data invariants. The goal is to know which shipped behaviors have at least one useful automated regression check.

Alternative considered:
- Percentage-based code coverage targets. Rejected because they can improve the metric without protecting the highest-risk user flows.

### Decision: Use the narrowest effective test layer for each gap
Backend business rules should continue to prefer service and repository tests. HTTP contracts should use controller or API verification tests. Frontend behavior should prefer pure logic tests and small API verification scripts before adding component or browser-level complexity.

Alternative considered:
- Push every gap into full-stack integration tests. Rejected because the suite would get slower, noisier, and harder to maintain for MVP velocity.

### Decision: Treat the audit artifact itself as part of the deliverable
The implementation should leave behind a contributor-readable mapping of implemented features to the tests or scripts that protect them, plus a short list of intentional remaining gaps if any cannot be closed in the same pass.

Alternative considered:
- Only add missing tests without documenting the current test surface. Rejected because contributors would still have to rediscover the suite manually.

### Decision: Keep test execution aligned with existing commands and structure
The change should reuse Maven tests on the backend and the existing frontend script pattern under `src/frontend/tests`. New commands should only be added when they improve discoverability for already-present test types.

Alternative considered:
- Introduce a new monolithic test runner or CI-only wrapper. Rejected because it adds ceremony without solving the immediate audit and regression problem.

## Risks / Trade-offs

- [Risk] The audit may uncover more gaps than can be fixed comfortably in one pass. → Mitigation: prioritize the currently shipped MVP and practice flows first, and record any deferred gaps explicitly.
- [Risk] Adding overlapping tests at too many layers can slow maintenance. → Mitigation: choose one primary layer per behavior and only duplicate checks when the second layer protects a different risk.
- [Risk] Frontend confidence may still lean on utility/API tests instead of full UI interaction coverage. → Mitigation: document this trade-off clearly and reserve browser-level testing for future requests.
- [Risk] Verification scripts that depend on seeded data can become brittle if the seed contract changes silently. → Mitigation: pair seed-flow verification with explicit seed-data expectations in the audited inventory.
