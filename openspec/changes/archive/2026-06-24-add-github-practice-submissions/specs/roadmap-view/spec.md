## ADDED Requirements

### Requirement: Practice Entry from the Roadmap
The authenticated roadmap experience SHALL expose a route to the practice workspace for unlocked `PRACTICE` and `PROJECT` nodes with a required challenge. Unlocked incomplete nodes SHALL show **Start practice** when automated submission is enabled and **View practice brief** when it is disabled. Locked nodes, lesson nodes, unsupported assessment types, and nodes without a valid required challenge SHALL NOT expose a practice route action.

#### Scenario: Start an enabled practice
- **WHEN** a learner opens an `AVAILABLE` or `IN_PROGRESS` `PRACTICE` or `PROJECT` node whose required challenge accepts submissions and activates **Start practice**
- **THEN** the frontend navigates to `/dashboard/roadmaps/{roadmapId}/nodes/{nodeId}/practice`
- **THEN** keyboard focus moves to the practice workspace heading

#### Scenario: Review a practice before grading is enabled
- **WHEN** a learner opens an unlocked `PRACTICE` or `PROJECT` node whose required challenge does not accept submissions and activates **View practice brief**
- **THEN** the frontend navigates to the same practice workspace route
- **THEN** the brief, criteria, hints, resources, artifacts, and starter repository remain readable while the submission form is unavailable

#### Scenario: Inspect a locked assessment node
- **WHEN** a learner opens a locked `PRACTICE` or `PROJECT` node
- **THEN** the drawer shows prerequisite guidance and no enabled practice or submission action

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
