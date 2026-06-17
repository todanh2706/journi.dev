## Context

Journi.dev recently completed a roadmap-domain refactor toward stricter JPA relationships, but the local environment still does not have realistic seed data to exercise the MVP flow: `login -> choose roadmap -> view nodes -> open node detail -> complete node -> unlock the next node`. The backend currently has no `data.sql`, no seed runner, and no dedicated repository or service flow for `LearningContent` and `Challenge`, so using raw SQL or a detached script would be likely to drift away from the real constraints and relationships enforced by the application.

This seeder needs to satisfy two goals at the same time:
- Create a realistic `Backend Java Spring Boot Developer` roadmap that is strong enough for demos, manual testing, and local development.
- Respect the current domain rules exactly: `LearningRoadmap.owner` is required, `SkillNode` is attached through `@ManyToOne`, prerequisite edges must not duplicate, and learning content and challenges must remain attached to `SkillNode`.

## Goals / Non-Goals

**Goals:**
- Provide a clear local seeding mechanism that can be run repeatedly without creating duplicate data.
- Seed one `Backend Java Spring Boot Developer` roadmap with a realistic node graph, prerequisite edges, learning content, and challenge data that supports the MVP.
- Reuse the existing entity and repository layer instead of hand-written SQL that can drift away from the schema.
- Keep the seed data readable and easy to update as roadmap content evolves.

**Non-Goals:**
- Do not seed default `UserNodeProgress` data for real users.
- Do not add a new admin UI or public API solely to trigger seeding.
- Do not seed unrelated product domains such as leaderboards, clusters, AI review, or notification workflows.
- Do not turn the seed system into a CMS or a dynamic roadmap generation platform.

## Decisions

### 1. Implement seeding inside the Spring Boot backend, not as a standalone Python importer

The seeder will be implemented in backend code and will use the current repositories and entities to persist data.

Rationale:
- It reuses the existing JPA relationships and constraints directly, which reduces the risk of incorrect foreign keys or enum values.
- It avoids adding Python packages, a new database driver, or a second configuration path into PostgreSQL.
- The seed logic can follow the same slug rules, duplicate checks, and audit-field conventions used by production code.

Alternatives considered:
- **Standalone Python script**: flexible, but it adds dependencies and duplicates mapping and schema knowledge outside the backend.
- **Raw SQL / data.sql**: quick to start with, but difficult to keep idempotent and harder to maintain as the entity model evolves.

### 2. Store the roadmap dataset as structured resource data, then map it through a seed service

The dataset should live in `src/backend/src/main/resources` as a structured file, such as JSON, and should be parsed by the seed service.

Rationale:
- Realistic roadmap content will be fairly long: titles, summaries, estimated effort, checklists, resources, and challenge copy. Hard-coding all of that in Java builders would be difficult to read and review.
- Spring Boot already includes Jackson, so JSON parsing does not require extra dependencies.
- A structured dataset file lets the team update learning-path content without digging into persistence logic.

Alternatives considered:
- **Hard-coded Java object graph**: simple at first, but it quickly turns into a very large file that is harder to maintain.
- **External CSV or Markdown bundle**: good for content authoring, but more awkward than JSON when mapping nested roadmap data into a relational graph.

### 3. Use a dedicated system seed owner user

The seeder will create or reuse a stable system user to serve as `LearningRoadmap.owner` and as the source for `createdBy` and `updatedBy`.

Rationale:
- `LearningRoadmap.owner` is currently required, so a seeded roadmap template needs a valid owner.
- Keeping the seed owner separate from real users makes the local data lineage clear and avoids attaching a template roadmap to an arbitrary account.

Implementation direction:
- Use a stable username and email such as `system_roadmap_seed` and `system+roadmaps@journi.dev`.
- This account does not need to participate in the normal login flow.

### 4. Make the seeder explicitly opt-in and idempotent

The seeder should run only when enabled through an explicit property or profile, rather than automatically on every boot. On rerun, it should update or reuse existing seeded data instead of inserting new duplicate records.

Rationale:
- This avoids polluting local databases unintentionally.
- It allows contributors to rerun the seed after editing the dataset without dropping the entire database.

Implementation direction:
- Use a gated runner such as `ApplicationRunner` or `CommandLineRunner` that activates only when a specific seed property is enabled.
- Upsert the roadmap and nodes by slug.
- For prerequisites, learning content, and challenges under the target roadmap, clear and rebuild or otherwise replace them within the roadmap scope so the final result stays deterministic.

Alternatives considered:
- **Always seed on startup**: convenient, but too likely to create unwanted side effects.
- **Insert-only with duplicate checks**: avoids duplicates, but handles dataset changes poorly.

### 5. Split realistic content across `contentJson`, `LearningContent`, and `Challenge`

The seeded data should be distributed according to the current model:
- `SkillNode.contentJson`: summary, estimated time, level, checklist, short note.
- `LearningContent`: documentation, videos, articles, and other resource links for each node.
- `Challenge`: hands-on exercises for milestone nodes such as REST, JPA, Security, Testing, and Deployment.

Rationale:
- This is the closest match to the domain model already implemented in the backend.
- It makes the seeded roadmap feel more realistic than packing all narrative content into one large JSON column.

## Risks / Trade-offs

- **[Risk] Dataset drift when entity fields change again** -> Mitigation: seed through repositories/entities and add focused tests around seeded counts and relationships.
- **[Risk] Large realistic content becomes noisy in code review** -> Mitigation: keep narrative content in a structured resource file and keep orchestration logic separate.
- **[Risk] Re-run logic accidentally deletes contributor-owned data** -> Mitigation: scope replacement logic strictly to the seeded roadmap slug and seeded owner identity.
- **[Risk] System seed owner becomes visible as a normal user** -> Mitigation: use stable naming/documentation and keep the account clearly marked as system-owned.

## Migration Plan

1. Add resource-backed roadmap dataset for `Backend Java Spring Boot Developer`.
2. Add seed orchestration service plus repositories/helpers needed for `LearningContent` and `Challenge`.
3. Add an explicit seed runner gated by property/profile for local use.
4. Add tests that verify idempotency and relational integrity for seeded data.
5. Document how to trigger the seeder locally and how to reset/reseed if needed.

Rollback strategy:
- Disable the seed property/profile to stop future runs.
- Delete the seeded roadmap and system seed owner in local DB, or reset the local database if needed.

## Open Questions

- Should milestone nodes always have a `Challenge` row, or only the hands-on nodes where challenge copy is clearly meaningful?
- Do we want the seed runner to live behind a custom property only, or also behind a dedicated Spring profile for developer convenience?
