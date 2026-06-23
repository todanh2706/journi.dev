## MODIFIED Requirements

### Requirement: Roadmap Node Drawer
The system SHALL open a responsive node-detail surface when a user activates a roadmap skill node. For `AVAILABLE`, `IN_PROGRESS`, and `COMPLETED` nodes, the node API contract and drawer SHALL provide the seeded summary, level, estimated effort, learner note, checklist, and learning resources. For `LOCKED` nodes, the backend SHALL redact those learning-detail fields and the drawer SHALL display only basic node identity, state, and prerequisite guidance. The surface SHALL provide accessible close, scroll, focus, Escape-key, checklist, external-link, and learner-confirmed lesson-completion behavior.

#### Scenario: Open unlocked node details
- **WHEN** a user activates a node with status `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED`
- **THEN** the detail surface displays the node title, node type, progress status, summary, level, estimated effort, learner note, actionable checklist, and related learning resources returned by the backend
- **THEN** keyboard focus moves into the detail surface without being lost behind the graph

#### Scenario: Open a learning resource
- **WHEN** a user activates a learning-resource link in an unlocked node drawer
- **THEN** the browser opens the real external HTTPS resource in a new tab without granting the destination access to the opener context

#### Scenario: Close node details
- **WHEN** the user activates the close control or presses Escape
- **THEN** the system closes the detail surface without leaving the roadmap detail page

#### Scenario: Display unavailable unlocked learning details
- **WHEN** an unlocked node has a missing or malformed optional learning-detail field
- **THEN** the detail surface renders the remaining valid details and displays concise empty copy for the missing checklist or resources without fabricating content

#### Scenario: Request a locked node through the API
- **WHEN** an authenticated user requests roadmap nodes or an individual node whose computed status is `LOCKED`
- **THEN** the response retains the node ID, title, slug, order, type, `LOCKED` progress status, and locked indicator
- **THEN** the response omits or empties summary, level, estimated effort, learner note, checklist, and learning resources

#### Scenario: Inspect a locked node in the drawer
- **WHEN** the selected node is locked
- **THEN** the detail surface explains that prerequisites must be completed
- **THEN** the detail surface does not render summary, level, estimated effort, learner note, checklist, learning resources, or an enabled completion or start action

#### Scenario: Complete an unlocked lesson
- **WHEN** an authenticated learner activates **Mark as complete** for an `AVAILABLE` or `IN_PROGRESS` `LESSON`
- **THEN** the frontend calls `POST /api/v1/users/me/progress/nodes/{nodeId}/complete` through the shared authenticated API client
- **THEN** the action remains disabled while pending
- **THEN** success refreshes roadmap-node state so the lesson becomes `COMPLETED` and any newly satisfied dependent node becomes `AVAILABLE`

#### Scenario: Completion fails
- **WHEN** the completion request returns an error
- **THEN** the drawer stays open, retains the previous state, and presents an inline retryable error

#### Scenario: Inspect a completed or assessment-oriented node
- **WHEN** the selected node is already `COMPLETED` or has type `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE`
- **THEN** the drawer does not expose another enabled manual lesson-completion action

#### Scenario: Use progress states from the API contract
- **WHEN** the frontend receives a skill-node response
- **THEN** it recognizes `LOCKED`, `AVAILABLE`, `IN_PROGRESS`, and `COMPLETED` as the supported progress states and renders a corresponding explicit label
