## ADDED Requirements

### Requirement: Support Service-Level Feature Regression Tests
The backend testing baseline MUST support focused service-level regression tests for feature-critical business rules without requiring the full Spring application context.

#### Scenario: Test roadmap progress and practice lifecycle logic
- **WHEN** a developer writes or updates a JUnit-based service test for roadmap progression, user-node completion, challenge access, or submission lifecycle behavior
- **THEN** the test can isolate collaborators with mocks or lightweight fixtures
- **THEN** `mvn test` executes the regression alongside the rest of the backend suite

#### Scenario: Reproduce a backend feature regression
- **WHEN** a contributor fixes a bug in an existing backend feature
- **THEN** they can add or revise a service-level regression test that proves the bug and protects the corrected behavior
- **THEN** the regression does not depend on the production database or external network access
