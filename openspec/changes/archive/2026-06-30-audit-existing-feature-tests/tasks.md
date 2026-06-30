## 1. Audit Current Coverage

- [x] 1.1 Inventory the implemented auth/session, roadmap, progress, practice, and seed-data features against the existing backend and frontend automated tests.
- [x] 1.2 Record the current coverage map and any remaining high-signal gaps in a contributor-readable regression inventory artifact.

## 2. Strengthen Backend Regression Coverage

- [x] 2.1 Add or revise service-level backend regression tests for uncovered roadmap progression, challenge access, or submission lifecycle behavior.
- [x] 2.2 Add or revise controller or repository tests only where the audit shows a missing HTTP or persistence contract that is not already protected at another layer.
- [x] 2.3 Run `./mvnw test` and resolve any failures introduced by the audited backend regression suite.

## 3. Strengthen Frontend and API Regression Coverage

- [x] 3.1 Add or revise targeted frontend logic tests under `src/frontend/tests` for uncovered roadmap, practice, or auth-adjacent behavior already implemented in the repo.
- [x] 3.2 Review and update the seeded roadmap API verification flow so it still proves gated details, lesson completion, and dependent-node unlocking against the current contract.
- [x] 3.3 Run the documented frontend regression commands and fix any regressions surfaced by the updated checks.

## 4. Document and Handoff the Test Baseline

- [x] 4.1 Update the relevant contributor-facing docs or scripts so the audited backend and frontend regression entry points are easy to discover and run.
- [x] 4.2 Summarize the final covered feature areas and explicitly document any intentionally deferred regression gaps.
