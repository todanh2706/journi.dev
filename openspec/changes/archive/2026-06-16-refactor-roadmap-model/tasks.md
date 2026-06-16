## 1. Domain Entities Refactoring

- [x] 1.1 Update `LearningRoadmap` entity to add `slug` uniqueness constraints and standard JPA annotations.
- [x] 1.2 Refactor `SkillNode` entity to replace raw `roadmapId` with a `@ManyToOne` relationship to `LearningRoadmap`.
- [x] 1.3 Refactor `NodePrerequisite` to properly represent parent-child node relationships using standard composite keys or entity relationships, preventing duplicates.
- [x] 1.4 Refactor `LearningContent` to use `@ManyToOne` referencing `SkillNode`.
- [x] 1.5 Refactor `Challenge` to use `@ManyToOne` referencing `SkillNode`.
- [x] 1.6 Refactor `UserNodeProgress` to use `@ManyToOne` referencing `User` and `SkillNode` with unique constraints.
- [x] 1.7 Implement Enums (`ProgressStatus`, `NodeType`, etc.) inside the entities where applicable, ensuring backward compatibility is considered.

## 2. Repositories and Queries Update

- [x] 2.1 Update `SkillNodeRepository` to use `findByRoadmap_Id` or equivalent JPA query syntax.
- [x] 2.2 Update `UserNodeProgressRepository` to support querying by user and lists of nodes.
- [x] 2.3 Update any other repositories broken by the entity changes.

## 3. Services and Business Logic

- [x] 3.1 Update roadmap/node creation logic to properly save JPA relationships instead of raw UUID strings.
- [x] 3.2 Implement logic in roadmap/node service to dynamically calculate node status (`ProgressStatus`) based on prerequisites and existing `UserNodeProgress`.
- [x] 3.3 Remove/deprecate dependencies on `SkillNode.isLocked`.

## 4. DTOs, Mappers, and API Layer

- [x] 4.1 Update Response DTOs to include computed `ProgressStatus` and handle the new entity structure safely without infinite recursion.
- [x] 4.2 Update MapStruct mappers (e.g., `RoadmapMapper`, `SkillNodeMapper`) to handle entity-to-DTO conversion for the new relationships and enums.
- [x] 4.3 Verify controller endpoints return the expected payloads without exposing internal JPA state.

## 5. Verification and Testing

- [x] 5.1 Run all backend unit and integration tests (`./mvnw test`).
- [x] 5.2 Fix failing tests due to entity and mapping changes.
- [x] 5.3 Verify the MVP flow manually if possible or via tests (roadmap fetch -> compute node status -> user marks complete).
