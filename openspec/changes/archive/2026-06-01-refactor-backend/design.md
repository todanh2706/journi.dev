## Context

The backend currently employs manual mapping of entities to DTOs in controllers and services, which is error-prone and scales poorly. Exception handling is minimal, lacking handlers for standard validation and missing resources. Services frequently return entities or nulls, leaving presentation logic to controllers, which blurs the architecture boundaries. Additionally, transactions aren't explicitly managed with `@Transactional`, magic strings are used instead of enums, and list endpoints do not support pagination, posing scalability issues.

## Goals / Non-Goals

**Goals:**
- Automate DTO ↔ Entity mapping using MapStruct.
- Establish a robust Global Exception Handling framework for standard and custom exceptions.
- Enforce strict layer boundaries: Controllers handle HTTP routing; Services contain business logic and return DTOs.
- Secure data integrity with explicit transaction boundaries (`@Transactional`).
- Replace domain-specific magic strings with Enums.
- Implement pagination for all list-returning endpoints.

**Non-Goals:**
- Completely rewriting the database schema or changing underlying technologies (e.g., Spring Data JPA, PostgreSQL).
- Introducing new functional features beyond the code quality and architectural improvements.
- Altering the frontend (apart from necessary adjustments if API response structures for errors or paginated lists change slightly, though we aim for backward compatibility where possible).

## Decisions

- **Mapping Library: MapStruct.** 
  - *Rationale:* MapStruct generates mapping code at compile time, offering better performance and type safety compared to reflection-based libraries like ModelMapper.
  - *Alternative Considered:* ModelMapper (rejected due to runtime reflection performance overhead).
- **Exception Handling: `@RestControllerAdvice` enhancement.**
  - *Rationale:* Expanding the existing `GlobalExceptionHandler` ensures all exceptions (e.g., `MethodArgumentNotValidException`, `ResourceNotFoundException`) return a standardized JSON structure across the entire API.
- **Transaction Management: Method-level `@Transactional`.**
  - *Rationale:* Applying `@Transactional` directly to service methods that modify data (e.g., `createUser`, `updateUser`) ensures atomic operations and rollback on unchecked exceptions.
- **Enums for Magic Strings:**
  - *Rationale:* Enums (e.g., `UserStatus`, `UserRole`) provide compile-time safety and self-documenting code over plain strings like `"ACTIVE"`.

## Risks / Trade-offs

- **[Risk] Pagination breaking existing frontend expectations** → *Mitigation:* We will communicate any changes in the `GET /api/v1/users` endpoint to the frontend team, or maintain backward compatibility by returning a list under a `content` node in a wrapper if needed, though native Spring `Page` serialization is preferred.
- **[Risk] MapStruct configuration complexity** → *Mitigation:* Keep mappings simple; use standard `@Mapper(componentModel = "spring")` for seamless dependency injection integration.
