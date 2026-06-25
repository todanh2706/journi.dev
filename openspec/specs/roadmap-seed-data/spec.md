## Purpose
Describe the deterministic local seed-data capability for roadmap content, including the predefined Backend Java Spring Boot roadmap dataset, its relational graph, and rerunnable local seeding flow.
## Requirements
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
Each of the 14 seeded roadmap nodes SHALL include topic-specific learning detail compatible with the current relational model. Nodes SHALL store a concrete summary, level, estimated effort, learner note, and at least three actionable checklist tasks in `contentJson`. Each node SHALL also have at least two related `LearningContent` rows whose HTTPS URLs resolve to real documentation, guides, courses, articles, or exercises directly relevant to that node. Resource titles and descriptions SHALL accurately identify the linked material, with authoritative sources preferred when available. Every seeded `PRACTICE` and `PROJECT` node SHALL have exactly one required deterministic `Challenge` with instructions, acceptance criteria, hints, expected artifacts, a public HTTPS starter repository, passing score, timeout, pinned grader image, fixed evaluator command, and explicit non-null evaluation activation owned by the seed dataset.

#### Scenario: Inspecting a seeded roadmap node
- **WHEN** a contributor inspects any of the 14 seeded node records
- **THEN** the node contains a topic-specific summary, level, estimated effort, learner note, and at least three checklist entries describing work the learner can perform

#### Scenario: Inspecting learning resources for every node
- **WHEN** a contributor inspects the `LearningContent` rows related to each seeded node
- **THEN** every node has at least two resources linked through valid `@ManyToOne` relationships to the correct `SkillNode`
- **THEN** every resource has a descriptive title, source type, HTTPS URL, and explanation relevant to the node topic

#### Scenario: Validating seeded external links
- **WHEN** the curated seed dataset is validated during development
- **THEN** every learning-resource URL resolves successfully or redirects to a valid page whose content matches the resource title and node topic

#### Scenario: Inspecting required assessment challenges
- **WHEN** a contributor inspects the seeded `PRACTICE` and `PROJECT` nodes
- **THEN** each has exactly one required challenge linked through a valid `@ManyToOne` relationship
- **THEN** each challenge contains complete learner-facing metadata and server-owned deterministic evaluator configuration
- **THEN** no `LESSON` node is assigned a required code-evaluation challenge

#### Scenario: Validating starter repositories and grader configuration
- **WHEN** the practice dataset is validated during development
- **THEN** every starter-repository URL resolves to the intended public GitHub repository
- **THEN** every grader image is pinned by digest and every command, timeout, passing score, acceptance criterion, and expected artifact passes schema validation

#### Scenario: Inspecting staged challenge activation
- **WHEN** a contributor inspects the seeded challenge activation values
- **THEN** `Collections and Generics` is enabled as the first pilot
- **THEN** the other seven `PRACTICE` and `PROJECT` challenges are explicitly disabled until their grader contracts are approved

#### Scenario: Re-running the enriched seed dataset
- **WHEN** a contributor reruns the seeder after the enriched content and challenge definitions have already been stored
- **THEN** the backend updates the scoped node details, relational resources, and challenges without creating duplicate rows

### Requirement: Relationship-Safe Prerequisite Graph
The seeding flow SHALL persist prerequisite edges that respect the current roadmap relationship rules. Every seeded prerequisite SHALL reference nodes from the same roadmap, follow the intended unlock progression, and satisfy existing uniqueness constraints.

#### Scenario: Inspecting prerequisite edges for the seeded roadmap
- **WHEN** a contributor loads prerequisite rows for the seeded roadmap
- **THEN** every parent-child edge connects valid roadmap nodes in the intended learning order without orphaned references or duplicate edges
