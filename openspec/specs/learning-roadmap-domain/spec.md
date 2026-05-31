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
The backend SHALL expose a skill node catalog at `/api/v1/skill-nodes`. The API SHALL support listing all nodes, retrieving a node by UUID, and creating a node from roadmap-linked metadata including title, slug, order, type, content JSON, and lock state.

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
The learning roadmap domain SHALL already reserve persistence structures for node-linked content, practical challenges, and per-user node progress. `LearningContent`, `Challenge`, and `UserNodeProgress` SHALL define the canonical storage model even though dedicated controllers and services are not yet exposed for them.

#### Scenario: Inspecting the roadmap domain entities
- **WHEN** a contributor reviews the entity package
- **THEN** they find separate models for node content sources, node challenges, and user progress state in addition to the roadmap and node APIs
