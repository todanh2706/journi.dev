# backend-unit-testing Specification

## Purpose

Define the backend testing baseline for Journi.dev, including Maven test execution, Spring MVC controller slice tests, service unit tests, and repository slice tests that run in an isolated test environment.
## Requirements
### Requirement: Execute Unit Tests During The Build Phase

The backend system MUST successfully compile and run all defined JUnit 6 tests as part of the standard Maven build lifecycle.

#### Scenario: Running `mvn test`

- **WHEN** a developer or CI pipeline runs `mvn test` in the `src/backend` directory
- **THEN** all tests execute and generate a test report
- **THEN** the build fails if any test fails
- **THEN** the build passes if all tests pass

### Requirement: Support Isolated Controller Testing

The backend testing framework MUST provide support for testing REST API controllers without initializing the full application context.

#### Scenario: Testing a controller

- **WHEN** a developer writes a test annotated with `@WebMvcTest`
- **THEN** only the web layer beans are instantiated
- **THEN** the developer can use `RestTestClient` to send HTTP-style requests and verify responses

### Requirement: Support Isolated Repository Testing

The backend testing framework MUST allow testing data access layers using an in-memory database or a lightweight container without touching the production database.

#### Scenario: Testing a repository

- **WHEN** a developer writes a test annotated with `@DataJpaTest`
- **THEN** the test executes against an isolated database environment, such as H2 or Testcontainers
- **THEN** data modifications are rolled back after each test

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

