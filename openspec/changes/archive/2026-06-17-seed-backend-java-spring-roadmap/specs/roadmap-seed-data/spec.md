## ADDED Requirements

### Requirement: Local Roadmap Seed Execution
The backend SHALL provide an explicit local-development seeding flow that inserts predefined roadmap data without requiring manual SQL editing. The seeding flow SHALL be safe to run more than once against the same database.

#### Scenario: Running the seeder on an empty local database
- **WHEN** a contributor executes the roadmap seed flow against an empty local database
- **THEN** the backend creates the predefined roadmap owner, roadmap, skill nodes, prerequisite edges, learning-content records, and challenge records required for the seeded roadmap

#### Scenario: Re-running the seeder on an already seeded database
- **WHEN** a contributor executes the roadmap seed flow again after the predefined roadmap already exists
- **THEN** the backend does not create duplicate roadmap, node, prerequisite, content, or challenge rows for the same seeded dataset

### Requirement: Realistic Backend Java Spring Boot Roadmap Dataset
The seeding flow SHALL create one realistic predefined roadmap titled `Backend Java Spring Boot Developer` with a stable slug, non-dynamic metadata, and an ordered beginner-to-junior progression. The seeded dataset SHALL cover the path from programming fundamentals through Java, SQL, Spring Core, Spring Boot, REST APIs, JPA, Security/JWT, testing, Docker, and deployment.

#### Scenario: Inspecting the seeded roadmap metadata
- **WHEN** a contributor loads the seeded roadmap from the database
- **THEN** they find a single roadmap record with stable title, slug, visibility, owner, and audit fields suitable for repeated local development

#### Scenario: Inspecting the seeded node sequence
- **WHEN** a contributor loads the seeded nodes for the roadmap ordered by `orderIndex`
- **THEN** they find a realistic multi-step learning path with stable slugs, valid node types, descriptive content, and milestone coverage across the Java Spring Boot backend journey

### Requirement: Rich Relational Roadmap Content
Each seeded roadmap node SHALL include realistic learning detail compatible with the current relational model. Nodes SHALL store structured learning summaries in `contentJson`, related `LearningContent` rows for external resources, and `Challenge` rows for milestone nodes where practical validation is appropriate.

#### Scenario: Inspecting a seeded roadmap node
- **WHEN** a contributor inspects a seeded node record
- **THEN** the node includes structured learning metadata such as summary, estimated effort, and checklist content suitable for beginner developers

#### Scenario: Inspecting relational resources and challenges
- **WHEN** a contributor inspects the `LearningContent` and `Challenge` rows related to seeded milestone nodes
- **THEN** they find rows linked through valid `@ManyToOne` relationships to the correct `SkillNode` records with realistic titles, descriptions, and resource links

### Requirement: Relationship-Safe Prerequisite Graph
The seeding flow SHALL persist prerequisite edges that respect the current roadmap relationship rules. Every seeded prerequisite SHALL reference nodes from the same roadmap, follow the intended unlock progression, and satisfy existing uniqueness constraints.

#### Scenario: Inspecting prerequisite edges for the seeded roadmap
- **WHEN** a contributor loads prerequisite rows for the seeded roadmap
- **THEN** every parent-child edge connects valid roadmap nodes in the intended learning order without orphaned references or duplicate edges
