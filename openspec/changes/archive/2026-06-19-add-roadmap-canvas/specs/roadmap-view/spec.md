## ADDED Requirements

### Requirement: Interactive Roadmap Detail Canvas
The system SHALL render roadmap detail skill nodes as an interactive learning canvas when roadmap nodes are available.

#### Scenario: Successful roadmap detail canvas load
- **WHEN** a user navigates to `/dashboard/roadmaps/:roadmapId`
- **THEN** the system fetches the roadmap and its nodes using the existing frontend roadmap service
- **THEN** the system displays the nodes in a graph canvas instead of a vertical timeline
- **THEN** the canvas supports pan, zoom, minimap, background, controls, and fit view

#### Scenario: Read-only learning graph
- **WHEN** the roadmap graph is displayed
- **THEN** nodes MUST NOT be draggable
- **THEN** users MUST NOT be able to create or edit graph connections
- **THEN** users MUST be able to select or click nodes for inspection

### Requirement: Progress-Aware Roadmap Skill Nodes
The system SHALL display each roadmap skill node with learning-specific visual state and metadata.

#### Scenario: Render node metadata
- **WHEN** a skill node appears on the canvas
- **THEN** the node displays its order, title, node type, progress status, locked state, and a summary fallback derived from the slug when richer summary content is unavailable

#### Scenario: Render completed node
- **WHEN** a skill node has progress status `COMPLETED`
- **THEN** the node displays a completed badge with a green check treatment

#### Scenario: Render in-progress node
- **WHEN** a skill node has progress status `IN_PROGRESS`
- **THEN** the node displays an active progress treatment using a blue or purple glow

#### Scenario: Render locked node
- **WHEN** a skill node is locked
- **THEN** the node displays a lock treatment and muted visual styling

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
The system SHALL provide a compact toolbar for progress context, fit view, and node search.

#### Scenario: Show progress summary
- **WHEN** the roadmap canvas is displayed
- **THEN** the toolbar displays completed node count and total node count

#### Scenario: Fit graph from toolbar
- **WHEN** the user clicks the fit view control
- **THEN** the canvas adjusts zoom and position to fit the roadmap graph in view

#### Scenario: Search roadmap nodes
- **WHEN** the user types in the node search input
- **THEN** matching node titles are visually highlighted without removing non-matching nodes from the graph

### Requirement: Roadmap Node Drawer
The system SHALL open a responsive node detail drawer when a user clicks a roadmap skill node.

#### Scenario: Open node drawer
- **WHEN** the user clicks a roadmap skill node
- **THEN** the system opens a right-side or floating drawer with the node title, node type, progress status, locked state, and slug-derived summary

#### Scenario: Close node drawer
- **WHEN** the user clicks the drawer close control
- **THEN** the system closes the drawer without leaving the roadmap detail page

#### Scenario: Display placeholder learning details
- **WHEN** checklist or resource content is not available from the node data
- **THEN** the drawer displays a clear placeholder for checklist and learning resources without failing

### Requirement: Preserve Roadmap Detail States
The system SHALL preserve existing roadmap detail loading, error, and empty states while replacing the populated node view with the canvas.

#### Scenario: Roadmap detail loading state
- **WHEN** roadmap detail data is loading
- **THEN** the system displays the existing loading treatment instead of an empty graph

#### Scenario: Roadmap detail error state
- **WHEN** roadmap detail data fails to load
- **THEN** the system displays the existing error treatment and retry option

#### Scenario: Roadmap detail empty state
- **WHEN** the roadmap has no skill nodes
- **THEN** the system displays the existing empty state instead of an empty graph
