## Why

We recently standardized the `UserService` and `AuthenticationService` by applying MapStruct for DTO mapping and `@Transactional` for atomicity in data-modifying operations. However, the rest of the backend entities (`LearningRoadmap`, `Submission`, `AiAssistantConversation`, etc.) still use manual object mapping and lack transactional boundaries. Extending these patterns to the remaining entities ensures a consistent, secure, and maintainable codebase where data integrity is guaranteed across the entire system.

## What Changes

- Implement MapStruct mappers for all remaining entities (e.g., `LearningRoadmap`, `Submission`, `LeaderboardEntry`, `ClusterMembership`, `ReminderNotification`, etc.).
- Update corresponding service layers to utilize the MapStruct mappers instead of manually converting Entities to DTOs.
- Add `@Transactional` to all data-modifying methods (Create, Update, Delete) across the remaining service classes.
- Standardize exception handling in these services to throw `ResourceNotFoundException` proactively instead of returning nulls.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `backend-code-quality`: We are extending the standardized backend quality patterns (MapStruct, `@Transactional`, exception handling) to the remaining entity domain services.

## Impact

- **Affected Code**: All existing service classes and controllers that interact with the remaining entities.
- **Dependencies**: Uses the existing MapStruct dependencies configured in the previous change.
- **Systems**: No external system impact. Purely internal code quality and data integrity improvement.
