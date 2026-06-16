## Context

The Spring Boot backend for Journi.dev currently lacks unit testing. To ensure reliability and simplify regression testing, we are introducing JUnit 6 along with Mockito for writing automated tests. The initial scope focuses on testing core services, repositories, and controllers.

## Goals / Non-Goals

**Goals:**

- Add JUnit 6 (Jupiter), Mockito, and Spring Boot Starter Test dependencies if they do not exist.
- Establish standard testing patterns for the backend:
    - `@WebMvcTest` with `RestTestClient` for Controller tests (mocking service layers).
    - `@DataJpaTest` for Repository tests.
    - Plain Mockito tests with `@ExtendWith(MockitoExtension.class)` for Services.
- Set up Maven to execute these tests automatically during the `test` phase.

**Non-Goals:**

- Writing full End-to-End (E2E) integration tests involving external dependencies (e.g., real Redis, actual database) beyond what Spring Boot provides through test slices.
- Providing 100% test coverage immediately. This PR provides the foundation and a meaningful initial suite.

## Decisions

- **JUnit 6 over JUnit 5**: Standard for modern Spring Boot applications, better annotation support, and modular architecture.
- **RestTestClient for Controllers**: Ensures controllers map routes properly and serialize/deserialize JSON correctly with a fluent HTTP-client style, without starting a full HTTP server.
- **H2 or Testcontainers for DataJpaTest**: We will rely on an in-memory database (like H2) or Testcontainers for repository testing. For simplicity in the initial setup, Spring's default behavior (H2 if available) or a simplified Postgres configuration will be used.

## Risks / Trade-offs

- **Risk**: Test configuration might clash with existing runtime properties (e.g., database URLs).
    - **Mitigation**: Introduce an `application-test.properties` to isolate test configurations (using H2 database or Mock setups).
- **Trade-off**: High test coverage vs. Delivery speed. We will focus on testing the most critical domain areas first (Authentication, Roadmaps, AI Review interactions) to balance coverage with velocity.
