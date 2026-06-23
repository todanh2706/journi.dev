## Context

Journi.dev's learning roadmap model needs to transition from a simplistic standalone structure into a robust relational model to support the MVP flow where each user tracks their own progress. The current design risks data inconsistencies and lacks standard relational constraints. Progress is often modeled globally or loosely. By adopting JPA relationships (`@ManyToOne`), defining constraints (e.g., preventing duplicate prerequisites), and computing progression strictly per-user, the platform can safely serve roadmap views and compute node states dynamically.

## Goals / Non-Goals

**Goals:**
- Transition `SkillNode`, `UserNodeProgress`, `NodePrerequisite`, `LearningContent`, and `Challenge` to strict relational models referencing their parent entities via JPA `@ManyToOne`.
- Compute node availability (e.g. `AVAILABLE`, `LOCKED`, `COMPLETED`, `IN_PROGRESS`) dynamically for each user based on their individual progress and the node's prerequisites.
- Define robust constraints such as uniqueness on `(roadmap_id, slug)` and `(user_id, node_id)`.
- Improve mapping and DTO layers to reflect user-specific statuses in APIs without exposing internal JPA recursion.
- Support learner-confirmed completion for unlocked theory `LESSON` nodes and immediately recompute dependent-node availability.

**Non-Goals:**
- Do not implement complex DAG graph-resolution algorithms beyond basic prerequisite checks.
- Do not add new admin panels or UI screens.
- Do not implement AI-based roadmap generation or complex dynamic roadmaps.
- Do not migrate from PostgreSQL or introduce new persistence frameworks like MongoDB.

## Decisions

- **JPA Relationships over Raw UUIDs**: We will replace standalone `UUID roadmapId` fields with `@ManyToOne(fetch = FetchType.LAZY)` references to `LearningRoadmap` inside `SkillNode`, and similar updates for other entities. This ensures referential integrity at the database level and simplifies nested queries.
- **Dynamic Node States via Enums**: We will use a `ProgressStatus` enum (`LOCKED`, `AVAILABLE`, `IN_PROGRESS`, `COMPLETED`) to represent computed node states per user in service layers and DTOs.
- **Explicit Lesson Completion**: An authenticated learner can mark an `AVAILABLE` or `IN_PROGRESS` `LESSON` as `COMPLETED`. The operation upserts the unique user-node progress row, preserves the first completion timestamp when repeated, and rejects locked or assessment-oriented nodes. Checklist items remain guidance rather than separately persisted completion gates.
- **Complete Prerequisite Scope**: Both full-roadmap and single-node evaluation must load progress for every prerequisite ID. Availability cannot depend on whether the caller happened to request prerequisite nodes in the same list.
- **Avoid Bidirectional Mappings**: Where possible, we will rely on unidirectional `@ManyToOne` to avoid infinite recursion and simplify serialization/`toString` behavior in Lombok `@Data`. When bidirectional mapping is necessary, we will exclude references in `@ToString` and `@EqualsAndHashCode`.

## Risks / Trade-offs

- **Risk: Breaking Existing Queries**: Refactoring raw UUIDs to JPA relationships might break existing repository methods (e.g., `findByRoadmapId(...)`).
  - *Mitigation*: Carefully inspect and update repository interfaces to `findByRoadmap_RoadmapId(...)` or use clear `@Query` annotations.
- **Risk: Infinite Recursion (StackOverflowError)**: Lombok `@Data` or JSON serialization may loop infinitely with relationships.
  - *Mitigation*: We will carefully review and switch to `@Getter`/`@Setter` or exclude JPA references from `toString`, `equals`, and `hashCode` implementations. MapStruct mappers and DTOs will decouple the response from the entity model.
- **Risk: Database Migration Breakage**: We rely on `spring.jpa.hibernate.ddl-auto=update` for MVP, which might fail or create messy schema changes.
  - *Mitigation*: The project structure encourages keeping standard JPA mappings. If a schema change fails to apply smoothly, manual drop/create locally is acceptable for this early stage MVP, but we will limit destructive renames where possible.
