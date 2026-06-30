## Purpose

Define roadmap catalog retrieval and the responsive learning canvas used to inspect progress-aware skill nodes and explicitly complete unlocked theory lessons without enabling graph editing.
## Requirements
### Requirement: Fetch and Display Roadmaps
The system SHALL retrieve available learning roadmaps from the backend API and display them in the Roadmap Page.

#### Scenario: Successful roadmap load
- **WHEN** a user navigates to the Roadmap Page
- **THEN** the system makes a GET request to `/api/v1/roadmaps`
- **THEN** the system displays the returned roadmaps as styled cards showing available roadmap metadata

#### Scenario: Empty roadmap list
- **WHEN** the backend returns an empty list of roadmaps
- **THEN** the system displays a clear empty state indicating that no roadmaps are currently available

#### Scenario: API error
- **WHEN** the backend request fails
- **THEN** the system displays an error message with an inline retry action

### Requirement: Serve Roadmaps from Backend
The system SHALL provide a REST endpoint to retrieve all learning roadmaps.

#### Scenario: Requesting all roadmaps
- **WHEN** a GET request is made to `/api/v1/roadmaps`
- **THEN** the backend service queries the repository for all roadmaps
- **THEN** the backend maps the entities to `LearningRoadmapResponse` objects
- **THEN** it returns a 200 OK with the list of roadmaps

### Requirement: Responsive Roadmap Workspace
The roadmap workspace SHALL remain usable on desktop and narrow viewports without horizontal page overflow. Graph controls SHALL avoid overlapping essential content, and the node-detail surface SHALL adapt from a side drawer on wide viewports to a full-width bottom sheet or modal surface on narrow viewports.

#### Scenario: Inspect a node on a narrow viewport
- **WHEN** a user opens a roadmap node on a narrow viewport
- **THEN** the detail surface provides a visible close control, contained content scrolling, and sufficient separation from the graph
- **THEN** the page does not require horizontal scrolling to read the node details

#### Scenario: Return focus after closing details
- **WHEN** a keyboard user closes the node-detail surface
- **THEN** focus returns to the roadmap node that opened it

### Requirement: Interactive Roadmap Detail Canvas
The system SHALL render roadmap detail skill nodes as an interactive, read-only learning canvas when roadmap nodes are available. The canvas SHALL retain pan, zoom, minimap, controls, and fit view while allowing pointer and keyboard users to inspect nodes without enabling graph editing.

#### Scenario: Successful roadmap detail canvas load
- **WHEN** a user navigates to `/dashboard/roadmaps/:roadmapId`
- **THEN** the system fetches the roadmap and its nodes using the existing frontend roadmap service
- **THEN** the system displays the nodes in the existing graph canvas with the black-and-gold visual foundation
- **THEN** the canvas supports pan, zoom, minimap, background, controls, and fit view

#### Scenario: Read-only learning graph
- **WHEN** the roadmap graph is displayed
- **THEN** nodes MUST NOT be draggable
- **THEN** users MUST NOT be able to create or edit graph connections
- **THEN** users MUST be able to inspect nodes with a pointer or keyboard

#### Scenario: Inspect a node with the keyboard
- **WHEN** a keyboard user focuses a roadmap node and presses Enter or Space
- **THEN** the same node-detail surface opens as it does for pointer activation
- **THEN** the focused node exposes an accessible name containing its step, title, and learning state

### Requirement: Progress-Aware Roadmap Skill Nodes
The system SHALL display each roadmap skill node with its order, title, node type, progress status, locked state, and summary fallback. Node type SHALL use quiet neutral metadata, while progress and availability SHALL control the primary visual emphasis using the shared semantic state system.

#### Scenario: Render node metadata
- **WHEN** a skill node appears on the canvas
- **THEN** the node displays its order, title, node type, progress status, locked state, and a summary fallback derived from the slug when richer summary content is unavailable
- **THEN** node type styling does not compete with progress state through an unrelated accent color

#### Scenario: Render completed node
- **WHEN** a skill node has progress status `COMPLETED`
- **THEN** the node displays a green completed treatment with a check icon and explicit completed text

#### Scenario: Render current or in-progress node
- **WHEN** a skill node has progress status `IN_PROGRESS` or is the current available learning focus
- **THEN** the node displays a gold emphasis and explicit current or in-progress text without relying on glow alone

#### Scenario: Render locked node
- **WHEN** a skill node is locked
- **THEN** the node displays a readable muted treatment, lock icon, and explicit locked text

#### Scenario: Focus or select a node
- **WHEN** a node is keyboard-focused, search-matched, or selected
- **THEN** each interaction state is visually distinct and is not communicated by color alone

### Requirement: Roadmap Canvas Edges
The system SHALL connect roadmap skill nodes with smooth visual edges derived from available node ordering when prerequisite edge data is unavailable.

#### Scenario: Build sequential fallback edges
- **WHEN** the frontend has roadmap nodes without prerequisite edge data
- **THEN** the system sorts nodes by `orderIndex`
- **THEN** the system creates a smooth edge from each node to the next node in sorted order

#### Scenario: Highlight active target edge
- **WHEN** a sequential edge targets a node with progress status `IN_PROGRESS`
- **THEN** the edge is animated to indicate the current learning focus

### Requirement: Roadmap Canvas Toolbar
The system SHALL provide a compact responsive toolbar for progress context, fit view, and node search. The toolbar SHALL remain operable with a keyboard, SHALL avoid obscuring essential graph content, and SHALL communicate search results without removing unmatched nodes.

#### Scenario: Show progress summary
- **WHEN** the roadmap canvas is displayed
- **THEN** the toolbar displays completed node count and total node count using the shared semantic progress treatment

#### Scenario: Fit graph from toolbar
- **WHEN** the user activates the fit-view control with a pointer or keyboard
- **THEN** the canvas adjusts zoom and position to fit the roadmap graph in view

#### Scenario: Search roadmap nodes
- **WHEN** the user types in the node search input
- **THEN** matching node titles are emphasized and non-matching nodes remain present but visually secondary
- **THEN** the toolbar communicates the number of matches or a clear no-results state

### Requirement: Roadmap Node Drawer
The system SHALL open a responsive node-detail surface when a user activates a roadmap skill node. The surface SHALL display only data available from the current node contract, SHALL distinguish locked and progress states, and SHALL provide accessible close, scroll, focus, Escape-key, and manual lesson-completion behavior.

#### Scenario: Open node details
- **WHEN** a user activates a roadmap skill node
- **THEN** the system opens a side drawer or responsive modal surface with the node title, node type, progress status, locked state, and summary
- **THEN** keyboard focus moves into the detail surface without being lost behind the graph

#### Scenario: Close node details
- **WHEN** the user activates the close control or presses Escape
- **THEN** the system closes the detail surface without leaving the roadmap detail page

#### Scenario: Display unavailable learning details
- **WHEN** checklist or resource content is not available from the node data
- **THEN** the detail surface displays concise non-interactive empty copy and does not fabricate resource links or persisted checklist progress

#### Scenario: Complete an available theory lesson
- **WHEN** an authenticated learner activates **Mark as complete** for a `LESSON` with status `AVAILABLE` or `IN_PROGRESS`
- **THEN** the frontend sends `POST /api/v1/users/me/progress/nodes/{nodeId}/complete` through the shared authenticated API client
- **THEN** the action is disabled while the request is pending to prevent duplicate submissions
- **THEN** after success the frontend refreshes or reconciles roadmap-node data so the lesson is `COMPLETED`, the completed count updates, and newly satisfied dependent nodes become `AVAILABLE`

#### Scenario: Completion request fails
- **WHEN** the lesson-completion request fails
- **THEN** the drawer keeps the prior node state and displays an inline retryable error without closing the drawer

#### Scenario: Inspect a completed lesson
- **WHEN** the selected `LESSON` is already `COMPLETED`
- **THEN** the drawer displays an explicit completed state and does not present another enabled completion action

#### Scenario: Inspect a non-lesson node
- **WHEN** the selected node type is `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE`
- **THEN** the drawer does not present the manual lesson-completion action
- **THEN** it does not claim that read-only checklist completion can satisfy the node's assessment

#### Scenario: Inspect a locked node
- **WHEN** the selected node is locked
- **THEN** the detail surface explains the locked state and does not present an enabled completion or start action

### Requirement: Preserve Roadmap Detail States
The system SHALL preserve distinct roadmap-detail loading, error, and empty states while using the shared black-and-gold visual system. Error recovery SHALL retry the relevant request without requiring a full browser reload.

#### Scenario: Roadmap detail loading state
- **WHEN** roadmap detail data is loading
- **THEN** the system displays a stable skeleton or loading treatment instead of an empty graph

#### Scenario: Roadmap detail error state
- **WHEN** roadmap detail data fails to load
- **THEN** the system displays a clear error treatment and an inline request retry action

#### Scenario: Roadmap detail empty state
- **WHEN** the roadmap has no skill nodes
- **THEN** the system displays a concise empty state instead of an empty graph or fabricated node content

### Requirement: Practice Entry from the Roadmap
The authenticated roadmap experience SHALL expose a route to the practice workspace for unlocked `PRACTICE` and `PROJECT` nodes with a required challenge. Unlocked incomplete nodes SHALL show **Start practice** when automated submission is enabled and **View practice brief** when it is disabled. Unlocked assessment node metadata SHALL include the curated challenge-specific `starterRepositoryUrl` when a required challenge exists, and locked assessment node metadata SHALL NOT expose that URL before prerequisites are complete. Locked nodes, lesson nodes, unsupported assessment types, and nodes without a valid required challenge SHALL NOT expose a practice route action.

#### Scenario: Start an enabled practice
- **WHEN** a learner opens an `AVAILABLE` or `IN_PROGRESS` `PRACTICE` or `PROJECT` node whose required challenge accepts submissions and activates **Start practice**
- **THEN** the frontend navigates to `/dashboard/roadmaps/{roadmapId}/nodes/{nodeId}/practice`
- **THEN** keyboard focus moves to the practice workspace heading

#### Scenario: Review a practice before grading is enabled
- **WHEN** a learner opens an unlocked `PRACTICE` or `PROJECT` node whose required challenge does not accept submissions and activates **View practice brief**
- **THEN** the frontend navigates to the same practice workspace route
- **THEN** the brief, criteria, hints, resources, artifacts, and starter repository remain readable while the submission form is unavailable

#### Scenario: Open a starter repository from an unlocked assessment node
- **WHEN** a learner opens the details for an unlocked `PRACTICE` or `PROJECT` node with a required challenge
- **THEN** the roadmap node metadata includes the curated public starter repository URL for that challenge
- **THEN** the **Open starter repository** action opens that backend-provided GitHub URL in a safe new tab

#### Scenario: Inspect a locked assessment node
- **WHEN** a learner opens a locked `PRACTICE` or `PROJECT` node
- **THEN** the drawer shows prerequisite guidance and no enabled practice or submission action
- **THEN** the roadmap node metadata does not expose the starter repository URL

#### Scenario: Reopen a completed practice
- **WHEN** a learner opens a completed `PRACTICE` or `PROJECT` node
- **THEN** the drawer identifies it as completed and provides a read-only route to its challenge and owned submission history without offering manual completion

### Requirement: GitHub Practice Workspace
The frontend SHALL provide a route-level practice workspace that loads the progress-gated challenge through typed feature services and presents Brief, Acceptance criteria, Resources, Submission, and Feedback content. Code authoring SHALL remain outside Journi.dev in the learner's IDE and GitHub repository.

#### Scenario: Load a practice workspace
- **WHEN** an authenticated learner opens an unlocked supported practice route
- **THEN** the workspace displays challenge instructions, acceptance criteria, hints, expected artifacts, starter-repository link, passing score, timeout, and owned attempt history

#### Scenario: Practice workspace cannot load
- **WHEN** challenge loading fails or the learner no longer has access
- **THEN** the workspace shows a precise error or locked state with navigation back to the roadmap and an inline retry only when retry can succeed

#### Scenario: Open the starter repository
- **WHEN** the learner activates the starter-repository link
- **THEN** the frontend opens the public HTTPS GitHub repository in a safe new tab

### Requirement: Practice Submission Interaction
When the challenge response reports submission enabled, the workspace SHALL collect a GitHub repository URL, branch, and full commit SHA, validate required syntax before submission, call the shared authenticated API client, prevent duplicate requests while pending, and keep learner input recoverable after a request failure. When submission is disabled, the workspace SHALL remain readable and SHALL replace the active form with an explicit availability notice.

#### Scenario: Submit a GitHub revision
- **WHEN** the learner enters valid submission fields and activates **Submit for evaluation**
- **THEN** the frontend sends the challenge submission request once, disables duplicate submission while pending, and renders the returned `SUBMITTED` attempt

#### Scenario: Submission request fails
- **WHEN** the API rejects the repository reference or the network request fails
- **THEN** the workspace retains the form values, shows an inline actionable error, and permits correction or retry

#### Scenario: Submit incomplete fields
- **WHEN** repository URL, branch, or full commit SHA is missing or syntactically invalid
- **THEN** the frontend shows field-level guidance and does not send the API request

#### Scenario: Open a read-only practice workspace
- **WHEN** the challenge response reports submission disabled
- **THEN** the workspace shows why automated evaluation is unavailable and does not expose an enabled submit action

### Requirement: Evaluation Status and Feedback
The frontend SHALL treat backend submission status as authoritative, poll with bounded backoff only while evaluation is non-terminal, pause polling when the page is hidden, and stop after a terminal result. It SHALL render distinct pending, evaluating, needs-changes, infrastructure-failure, and passed states with accessible status announcements.

#### Scenario: Observe an active evaluation
- **WHEN** the current attempt is `SUBMITTED` or `EVALUATING`
- **THEN** the workspace communicates the active state and refreshes status without allowing another identical submission

#### Scenario: Receive needs-changes feedback
- **WHEN** the current attempt becomes `NEEDS_CHANGES`
- **THEN** the workspace displays bounded score and acceptance-criterion feedback and permits submission of a different commit

#### Scenario: Receive an infrastructure failure
- **WHEN** the current attempt becomes `FAILED`
- **THEN** the workspace distinguishes infrastructure failure from learner test failure and offers an idempotent retry for that attempt

#### Scenario: Pass a practice challenge
- **WHEN** the current attempt becomes `PASSED`
- **THEN** the workspace displays the passing result, refreshes roadmap-node data, updates completed progress, and makes newly satisfied dependent nodes available without a full-page reload

#### Scenario: Preserve navigation and focus after refresh
- **WHEN** roadmap data refreshes after a passing result
- **THEN** the learner remains in the practice workspace with focus and status context preserved and can navigate back to the updated roadmap
