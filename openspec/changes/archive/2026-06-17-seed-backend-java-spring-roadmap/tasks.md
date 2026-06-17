## 1. Seed Infrastructure

- [x] 1.1 Add the seed dataset location, DTO/model classes, and config hook needed to load a structured roadmap seed definition from backend resources.
- [x] 1.2 Add any missing repositories/helpers required to create and replace `LearningContent` and `Challenge` rows safely for seeded roadmap nodes.
- [x] 1.3 Implement a dedicated backend seed orchestration service plus an explicit runner/property gate so contributors can trigger roadmap seeding locally.

## 2. Roadmap Dataset and Persistence

- [x] 2.1 Create a realistic `Backend Java Spring Boot Developer` seed dataset with stable roadmap/node slugs, ordered progression, summaries, estimated effort, checklists, resources, and milestone challenges.
- [x] 2.2 Implement system seed-owner creation or lookup so seeded roadmaps satisfy `LearningRoadmap.owner` and audit-field requirements.
- [x] 2.3 Implement idempotent roadmap and node upsert logic keyed by stable slugs instead of insert-only behavior.
- [x] 2.4 Implement prerequisite, learning-content, and challenge synchronization scoped to the seeded roadmap so reruns refresh the dataset without duplicates.

## 3. Verification and Documentation

- [x] 3.1 Add backend tests that verify the seeder creates the roadmap graph with valid relationships and remains idempotent on rerun.
- [x] 3.2 Run backend checks and verify the seeded roadmap exposes the expected node ordering, prerequisite graph, and relational content.
- [x] 3.3 Document how to trigger, rerun, and reset the roadmap seeder in local development.
