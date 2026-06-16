## Why

The backend currently lacks unit tests, which increases the risk of regressions and reduces code reliability. Introducing JUnit tests ensures that our backend services, controllers, and repositories function correctly and maintain high code quality as the project evolves. This provides a safety net for future refactoring and feature additions.

## What Changes

- Set up JUnit 6 and Mockito in the Spring Boot backend for unit and integration testing.
- Create unit tests for core services and business logic (e.g., authentication, roadmap progression).
- Create tests for REST controllers using `RestTestClient`.
- Create data-layer tests for repositories using `@DataJpaTest`.
- Update the Maven build configuration to include test execution in the build process.

## Capabilities

### New Capabilities

- `backend-unit-testing`: Establishing the testing framework, patterns, and initial test coverage for the Spring Boot backend using JUnit 6 and Mockito.

### Modified Capabilities

None

## Impact

- **Affected code**: `src/backend/pom.xml` (adding test dependencies if missing), creating new test files in `src/backend/src/test/java/...`.
- **Systems**: CI/CD pipeline (tests will now run on build), developer workflow (running tests locally).
