## Context

The backend currently employs `MapStruct` and `@Transactional` strictly within the `UserService` and `AuthenticationService` domains. This was implemented as part of a previous refactoring effort to establish a gold-standard pattern. The remaining entities (`LearningRoadmap`, `Submission`, `PeerReview`, `LeaderboardEntry`, `AiAssistantConversation`, `ClusterMembership`, `ReminderNotification`) still rely on manual mapping from Entities to DTOs and do not use transaction boundaries. This leads to boilerplate code, potential data exposure, and risk of corrupted data if a multi-step database operation fails mid-execution.

## Goals / Non-Goals

**Goals:**
- Centralize all Entity <-> DTO conversion for the remaining entities using MapStruct.
- Add `@Transactional` to all create, update, and delete methods across the remaining service classes.
- Standardize exception handling across all services to proactively throw `ResourceNotFoundException`.

**Non-Goals:**
- Altering the business logic or workflow of any existing features.
- Creating entirely new features or modifying the database schema.
- Refactoring the frontend.

## Decisions

**Decision 1: MapStruct Mappers per Domain**
- We will create a dedicated MapStruct Mapper interface for each domain (e.g., `RoadmapMapper`, `SubmissionMapper`, `ReviewMapper`).
- *Rationale*: Keeps mapping logic isolated, cohesive, and easy to maintain compared to a single monolithic mapper.

**Decision 2: Service-Level Transactional Boundaries**
- `@Transactional` will be applied at the method level for any service method that modifies data, rather than at the class level.
- *Rationale*: Applying it at the method level prevents unnecessary transaction overhead on read-only methods (like `getById` or `getAll`), improving database connection pool efficiency.

## Risks / Trade-offs

- **Risk**: Missed manual mappings when converting to MapStruct, leading to missing data in API responses.
  - *Mitigation*: We will carefully review the existing manual mappings and configure `unmappedTargetPolicy = ReportingPolicy.IGNORE` or explicit `@Mapping` annotations to ensure 1-to-1 parity.
- **Risk**: `@Transactional` could cause unanticipated rollbacks if a service method purposefully catches and handles an exception internally.
  - *Mitigation*: We will review the try-catch blocks in the service layer to ensure exceptions are thrown correctly where rollbacks are desired.
