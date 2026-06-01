## Why

The current Spring Boot backend codebase suffers from several technical debts and code quality issues, such as manual DTO mapping, weak exception handling, leaky controller logic, lack of transaction management, use of magic strings, and inefficient un-paginated queries. Upgrading the code quality now will prevent bugs, improve maintainability, and ensure scalability as the Journi.dev platform grows.

## What Changes

- Implement a mapping library (e.g., MapStruct or ModelMapper) to automate DTO ↔ Entity conversions, removing manual and error-prone mapping code.
- Enhance `GlobalExceptionHandler` to handle common exceptions like validation errors (`MethodArgumentNotValidException`), custom business exceptions (e.g., `ResourceNotFoundException`), and generic exceptions, returning consistent error formats.
- Refactor Controllers and Services to enforce strict boundaries. Services will handle all mapping logic and return DTOs rather than entities, ensuring Controllers remain thin.
- Introduce `@Transactional` annotations across all state-modifying service methods to guarantee data integrity.
- Replace magic strings (e.g., "ACTIVE", "USER") with appropriate Enums for status and roles to ensure type safety.
- Introduce pagination (`Pageable`) for collection endpoints like `getAllUsers()` to handle large datasets efficiently.

## Capabilities

### New Capabilities
- `backend-code-quality`: Standardizing exception handling, transaction management, DTO mapping, and pagination across the backend application.

### Modified Capabilities
- `authentication-and-user-management`: Updating authentication and user-related services to use the new standardized mapping and exception handling.

## Impact

- **Code:** Widespread refactoring across the `controllers` and `services` layers to adopt the new standards.
- **APIs:** The structure of error responses will change to be more consistent. Endpoints returning lists (like `GET /api/v1/users`) will be updated to accept and return paginated data.
- **Dependencies:** Addition of a mapping library (e.g., MapStruct) to the `pom.xml`.
