## ADDED Requirements

### Requirement: Responsive Roadmap Workspace
The roadmap workspace SHALL remain usable on desktop and narrow viewports without horizontal page overflow. Graph controls SHALL avoid overlapping essential content, and the node-detail surface SHALL adapt from a side drawer on wide viewports to a full-width bottom sheet or modal surface on narrow viewports.

#### Scenario: Inspect a node on a narrow viewport
- **WHEN** a user opens a roadmap node on a narrow viewport
- **THEN** the detail surface provides a visible close control, contained content scrolling, and sufficient separation from the graph
- **THEN** the page does not require horizontal scrolling to read the node details

#### Scenario: Return focus after closing details
- **WHEN** a keyboard user closes the node-detail surface
- **THEN** focus returns to the roadmap node that opened it

## MODIFIED Requirements

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
The system SHALL open a responsive node-detail surface when a user activates a roadmap skill node. The surface SHALL display only data available from the current node contract, SHALL distinguish locked and progress states, SHALL provide accessible close, scroll, focus, and Escape-key behavior, and SHALL allow authenticated manual completion of unlocked theory lessons.

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

#### Scenario: Complete an unlocked lesson
- **WHEN** an authenticated learner activates **Mark as complete** for an `AVAILABLE` or `IN_PROGRESS` `LESSON`
- **THEN** the action calls the authenticated completion endpoint, prevents duplicate submission while pending, and refreshes roadmap-node state after success

#### Scenario: Inspect a completed or assessment-oriented node
- **WHEN** the selected node is already `COMPLETED` or has type `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE`
- **THEN** the detail surface does not present another enabled manual lesson-completion action

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
