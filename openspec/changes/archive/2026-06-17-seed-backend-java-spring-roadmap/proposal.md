## Why

Journi.dev now has a roadmap domain with JPA-backed unlock rules, but the local environment still lacks realistic roadmap data to exercise the MVP flow end to end. After the recent model refactor, the project needs a reliable seeder that can consistently create a realistic `Backend Java Spring Boot Developer` roadmap with enough depth and with full respect for the current constraints and relationships.

## What Changes

- Add a deterministic backend seeding flow that inserts one realistic predefined roadmap for `Backend Java Spring Boot Developer`.
- Seed the full roadmap graph, including roadmap metadata, ordered skill nodes, prerequisite edges, learning-content rows, and challenge rows that respect the current relationship model.
- Introduce a structured seed dataset format so roadmap content stays readable and maintainable instead of being scattered across hard-coded entity creation blocks.
- Ensure the seeder is idempotent for local development by using stable slugs and duplicate checks instead of creating repeated rows on every run.
- Seed a system-owned roadmap record compatible with the current `LearningRoadmap.owner` requirement and existing `createdBy` / `updatedBy` audit fields.
- Include realistic node copy: titles, summaries, estimated effort, learning resources, checklists, and practical challenge placeholders aligned with a beginner-to-junior backend path.

## Capabilities

### New Capabilities
- `roadmap-seed-data`: Deterministic local seed data for a realistic Backend Java Spring Boot roadmap, including relational graph data and rich learning content.

### Modified Capabilities

## Impact

- Backend code under `src/backend` for seed orchestration, dataset parsing, and repository-driven inserts.
- Local developer workflow for bootstrapping roadmap data in PostgreSQL without manual SQL.
- Roadmap-domain tables and relationships: `users`, `learning_roadmap`, `skill_node`, `node_prerequisite`, `learning_content`, and `challenge`.
- Documentation and OpenSpec artifacts describing how the seeded roadmap should behave and what content it includes.
