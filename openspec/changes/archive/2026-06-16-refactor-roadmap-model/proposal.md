## Why

The current roadmap data model in Journi.dev needs to be improved to be safer, more relational, and easier to query. To support the MVP flow, roadmap state tracking needs to be computed per-user rather than relying on a global locked state, and entities must have robust constraints to prevent duplicate data or recursion issues.

## What Changes

- **LearningRoadmap**: Refactor to act as a system-level template/predefined roadmap. Add unique constraint on `slug`.
- **SkillNode**: Make it a static representation of a step belonging to a roadmap using a `@ManyToOne` relationship. Remove or deprecate global `isLocked` state.
- **UserNodeProgress**: Exclusively store progress for each user using `@ManyToOne` to both `User` and `SkillNode` with a `(user_id, node_id)` unique constraint.
- **NodePrerequisite**: Ensure duplicate prerequisite edges are prevented and relationship accurately represents parent/child nodes.
- **LearningContent & Challenge**: Link properly to `SkillNode` with a `@ManyToOne` relationship.
- **Enum Adoption**: Introduce enums like `ProgressStatus` (`LOCKED`, `AVAILABLE`, `IN_PROGRESS`, `COMPLETED`) to represent computed node states per user in DTOs and internal logic.
- **Service & DTOs**: Update services to dynamically calculate user node states based on prerequisites and completions. Map these states safely into DTOs to avoid JPA recursion.
- **Lesson Completion Contract**: Treat completion of an unlocked theory `LESSON` as an explicit learner action stored in `UserNodeProgress`; completing all prerequisites makes dependent nodes available without persisting global lock state.

## Capabilities

### New Capabilities

### Modified Capabilities
- `learning-roadmap-domain`: Update entity relationships, per-user progress logic, constraints, and enums for safe querying and data integrity.

## Impact

- **Database**: Schema updates including new constraints, foreign keys, and potentially new enum columns for progress and visibility.
- **Backend Code**: Changes across `LearningRoadmap`, `SkillNode`, `UserNodeProgress`, `NodePrerequisite`, `LearningContent`, and `Challenge` domains (entities, repositories, services, DTOs, mappers, and controllers).
- **APIs**: The roadmap endpoints will compute node statuses per user based on prerequisites instead of returning a global locked field. (Contract shapes largely the same, but internal computation changes).
- **Progress API**: `POST /api/v1/users/me/progress/nodes/{nodeId}/complete` is the authenticated, idempotent completion path for unlocked `LESSON` nodes only.
- **Tests**: Updating and fixing backend tests related to roadmap generation, progression, and unlocking logic.
