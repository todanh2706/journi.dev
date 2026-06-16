## ADDED Requirements

### Requirement: Roadmap State Computation
The backend SHALL compute and return the progress state of a skill node on a per-user basis instead of relying on a global static `isLocked` state. The state SHALL be derived dynamically from the user's progress and the node's prerequisites as `LOCKED`, `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED`.

#### Scenario: Determining node state based on prerequisites
- **WHEN** a client retrieves a node for a specific user
- **THEN** the system evaluates if all prerequisite nodes are completed to return `AVAILABLE` or `COMPLETED`, or `LOCKED` otherwise

### Requirement: Relational Integrity Constraints
The backend SHALL enforce strict relational constraints across roadmap entities using PostgreSQL constraints and JPA `@ManyToOne` relationships, preventing duplicate prerequisite edges and multiple progress records per node for a single user.

#### Scenario: Creating duplicate progress record
- **WHEN** the system attempts to create a duplicate `UserNodeProgress` record for a `(user_id, node_id)` pair
- **THEN** the database unique constraint rejects the insertion to preserve integrity

## MODIFIED Requirements

### Requirement: Skill Node Catalog API
The backend SHALL expose a skill node catalog at `/api/v1/skill-nodes`. The API SHALL support listing all nodes, retrieving a node by UUID, and creating a node from roadmap-linked metadata including title, slug, order, type, and content JSON. The node state SHALL NOT be stored as a static global lock state.

#### Scenario: Listing skill nodes
- **WHEN** a client requests `GET /api/v1/skill-nodes`
- **THEN** the backend returns the available nodes as `SkillNodeResponse` objects

#### Scenario: Creating a skill node
- **WHEN** a client submits a valid node payload to `POST /api/v1/skill-nodes/{creatorId}`
- **THEN** the backend persists node metadata for the referenced roadmap and returns the created node representation

### Requirement: Learning Content, Challenges, and Progress Domain Model
The learning roadmap domain SHALL employ robust relational persistence structures for node-linked content, practical challenges, and per-user node progress. `LearningContent`, `Challenge`, and `UserNodeProgress` SHALL define the canonical storage model with explicit JPA many-to-one mapping relationships to `SkillNode` rather than standalone UUID links.

#### Scenario: Inspecting the roadmap domain entities
- **WHEN** a contributor reviews the entity package
- **THEN** they find models mapped with `@ManyToOne` to `SkillNode`, ensuring strong foreign-key constraints and preventing infinite recursion
