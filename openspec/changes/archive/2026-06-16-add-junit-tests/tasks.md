## 1. Setup

- [x] 1.1 Verify Maven configuration and add `spring-boot-starter-test` and JUnit 6 dependencies if not present
- [x] 1.2 Create `application-test.properties` for test isolation (e.g., using H2 database)

## 2. Core Service Tests

- [x] 2.1 Set up `@ExtendWith(MockitoExtension.class)` for a core service (e.g., AuthService or RoadmapService)
- [x] 2.2 Write basic unit tests for the selected service methods using Mockito to mock dependencies

## 3. Controller Tests

- [x] 3.1 Setup `@WebMvcTest` for a core controller (e.g., AuthController)
- [x] 3.2 Write a test using `RestTestClient` to verify HTTP status and response payload

## 4. Repository Tests

- [x] 4.1 Set up `@DataJpaTest` for a core repository (e.g., UserRepository)
- [x] 4.2 Write tests to verify custom query methods in the repository

## 5. Verification

- [x] 5.1 Run `mvn clean test` and verify that all test suites execute and pass successfully
