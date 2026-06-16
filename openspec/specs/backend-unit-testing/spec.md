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
