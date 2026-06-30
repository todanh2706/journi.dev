## Why

Journi.dev already has a meaningful test base across backend services, controllers, repositories, grader contracts, and a small set of frontend logic checks, but the coverage is uneven and not clearly mapped to the features the product already ships. Before adding more scope, the project needs a deliberate audit-and-fill pass so the current authentication, roadmap progression, practice submission, and seed-data flows stay stable as MVP work continues.

## What Changes

- Audit the existing backend and frontend test suites against the features already implemented in the codebase.
- Define a regression-focused testing baseline for current auth, roadmap, progress, practice, and seed-data behavior.
- Add or revise targeted tests where implemented behavior is not yet covered well enough, especially for cross-feature workflows and edge cases.
- Standardize how contributors discover, run, and evaluate the relevant test commands for backend and frontend feature work.

## Capabilities

### New Capabilities
- `feature-regression-testing`: Defines a feature-to-test inventory, targeted regression expectations, and lightweight execution guidance for the Journi.dev features that already exist.

### Modified Capabilities
- `backend-unit-testing`: Expands the backend testing baseline so it explicitly covers service-level business rules and feature-critical regression scenarios in addition to existing slice-test support.

## Impact

- Backend test suites under `src/backend/src/test/java/journi/dev/backend/**`
- Frontend test scripts and logic checks under `src/frontend/tests/**` and `src/frontend/package.json`
- Feature areas already implemented in auth, roadmap, progress, practice submission, and seed seeding flows
- Contributor-facing testing guidance in project docs where the audited execution flow needs to be clarified
