## Purpose
Describe the roadmap-centered learning domain that is already represented by backend APIs and supporting persistence models.
## Requirements
### Requirement: User-Scoped Roadmap Creation
The backend SHALL expose roadmap creation at `POST /api/v1/roadmaps/user/{userId}`. A created roadmap SHALL be owned by the supplied user, store title, description, visibility, and dynamic/static status, and default `isDynamic` to `false` when the request omits it.

#### Scenario: Creating a roadmap without an explicit dynamic flag
- **WHEN** a client posts a valid roadmap request to `POST /api/v1/roadmaps/user/{userId}` and omits `isDynamic`
- **THEN** the created roadmap is stored as a non-dynamic roadmap

### Requirement: Roadmap Retrieval by Identifier
The backend SHALL expose roadmap lookup at `GET /api/v1/roadmaps/{roadmapId}`. The response SHALL return roadmap metadata through `LearningRoadmapResponse`.

#### Scenario: Fetching a saved roadmap
- **WHEN** a client requests `GET /api/v1/roadmaps/{roadmapId}` for an existing record
- **THEN** the backend returns the roadmap metadata mapped into `LearningRoadmapResponse`

### Requirement: Skill Node Catalog API
The backend SHALL expose a skill node catalog at `/api/v1/skill-nodes`. The API SHALL support listing all nodes, retrieving a node by UUID, and creating a node from roadmap-linked metadata including title, slug, order, type, and content JSON. The node state SHALL NOT be stored as a static global lock state.

#### Scenario: Listing skill nodes
- **WHEN** a client requests `GET /api/v1/skill-nodes`
- **THEN** the backend returns the available nodes as `SkillNodeResponse` objects

#### Scenario: Creating a skill node
- **WHEN** a client submits a valid node payload to `POST /api/v1/skill-nodes/{creatorId}`
- **THEN** the backend persists node metadata for the referenced roadmap and returns the created node representation

### Requirement: Node Prerequisite Graph Edges
The backend SHALL expose prerequisite relationships at `/api/v1/node-prerequisites`. Relationships SHALL be modeled as composite parent-child edges with an optional relation type and creation timestamp.

#### Scenario: Creating a prerequisite edge
- **WHEN** a client posts a parent node UUID in the path and a child node UUID in the request body to `POST /api/v1/node-prerequisites/{parentNodeId}`
- **THEN** the backend stores a prerequisite edge connecting the two nodes

#### Scenario: Looking up a specific prerequisite edge
- **WHEN** a client calls `GET /api/v1/node-prerequisites/{parentNodeId}` with the child node UUID in the request body
- **THEN** the backend resolves the composite prerequisite identifier and returns the matching edge when present

### Requirement: Learning Content, Challenges, and Progress Domain Model
The learning roadmap domain SHALL employ robust relational persistence structures for node-linked content, practical challenges, and per-user node progress. `LearningContent`, `Challenge`, and `UserNodeProgress` SHALL define the canonical storage model with explicit JPA many-to-one mapping relationships to `SkillNode` rather than standalone UUID links.

#### Scenario: Inspecting the roadmap domain entities
- **WHEN** a contributor reviews the entity package
- **THEN** they find models mapped with `@ManyToOne` to `SkillNode`, ensuring strong foreign-key constraints and preventing infinite recursion

### Requirement: Roadmap State Computation
The backend SHALL compute and return the progress state of a skill node on a per-user basis instead of relying on a global static `isLocked` state. The state SHALL be derived dynamically from the user's progress and the node's complete prerequisite set as `LOCKED`, `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED`. The same prerequisite evaluation SHALL be used for roadmap-list, individual-node, and completion-validation paths.

#### Scenario: Determining node state based on prerequisites
- **WHEN** a client retrieves a node for a specific user
- **THEN** the system loads progress for every prerequisite node and returns `AVAILABLE` when all prerequisites are completed, `LOCKED` when any prerequisite is incomplete, or the persisted `IN_PROGRESS` or `COMPLETED` state when present

#### Scenario: Determining a single dependent node state
- **WHEN** the backend evaluates one dependent node outside a full-roadmap query
- **THEN** it still loads progress for all prerequisite node IDs and produces the same state that the full-roadmap query would produce

### Requirement: Learner-Confirmed Lesson Completion
The backend SHALL allow an authenticated learner to explicitly mark their own unlocked theory lesson as completed through `POST /api/v1/users/me/progress/nodes/{nodeId}/complete`. Manual completion SHALL apply only to nodes of type `LESSON` whose computed state is `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED`. It SHALL NOT require every read-only checklist item to be persisted or checked.

#### Scenario: Complete an available lesson
- **WHEN** an authenticated learner submits the completion request for an `AVAILABLE` `LESSON`
- **THEN** the backend upserts the learner's unique `(user_id, node_id)` progress row as `COMPLETED`
- **THEN** it records `completedAt` and returns the learner's completed progress response

#### Scenario: Repeat lesson completion
- **WHEN** the learner submits completion again for the same `COMPLETED` lesson
- **THEN** the operation succeeds idempotently without creating another progress row or replacing the original `completedAt`

#### Scenario: Reject completion of a locked lesson
- **WHEN** the learner submits completion for a `LOCKED` lesson
- **THEN** the backend rejects the request without changing progress

#### Scenario: Reject manual completion for an assessment node
- **WHEN** the learner submits the manual completion request for a `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE` node
- **THEN** the backend rejects the request because that node requires its type-specific assessment workflow

#### Scenario: Unlock dependent nodes after completion
- **WHEN** completing a lesson satisfies every prerequisite of a dependent node
- **THEN** the next roadmap-node retrieval returns that dependent node as `AVAILABLE` without requiring a separate unlock record

### Requirement: Relational Integrity Constraints
The backend SHALL enforce strict relational constraints across roadmap entities using PostgreSQL constraints and JPA `@ManyToOne` relationships, preventing duplicate prerequisite edges and multiple progress records per node for a single user.

#### Scenario: Creating duplicate progress record
- **WHEN** the system attempts to create a duplicate `UserNodeProgress` record for a `(user_id, node_id)` pair
- **THEN** the database unique constraint rejects the insertion to preserve integrity
