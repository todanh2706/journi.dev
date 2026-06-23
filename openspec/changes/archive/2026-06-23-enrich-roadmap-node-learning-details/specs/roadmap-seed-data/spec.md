## MODIFIED Requirements

### Requirement: Rich Relational Roadmap Content
Each of the 14 seeded roadmap nodes SHALL include topic-specific learning detail compatible with the current relational model. Nodes SHALL store a concrete summary, level, estimated effort, learner note, and at least three actionable checklist tasks in `contentJson`. Each node SHALL also have at least two related `LearningContent` rows whose HTTPS URLs resolve to real documentation, guides, courses, articles, or exercises directly relevant to that node. Resource titles and descriptions SHALL accurately identify the linked material, with authoritative sources preferred when available. `Challenge` rows SHALL remain available for milestone nodes where practical validation is appropriate.

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

#### Scenario: Inspecting milestone challenges
- **WHEN** a contributor inspects the `Challenge` rows related to seeded milestone nodes
- **THEN** they find realistic challenge titles and descriptions linked through valid `@ManyToOne` relationships to the correct `SkillNode` records

#### Scenario: Re-running the enriched seed dataset
- **WHEN** a contributor reruns the seeder after the enriched content has already been stored
- **THEN** the backend updates the scoped node details and relational resources without creating duplicate rows
