## ADDED Requirements

### Requirement: Roadmap-First Dashboard Overview
The authenticated dashboard overview SHALL orient users toward the implemented roadmap experience using the current auth context and existing roadmap service. It SHALL NOT present fabricated progress, streak, rank, leaderboard, activity, or review data when those values are not available from an implemented backend contract.

#### Scenario: Load the dashboard with available roadmaps
- **WHEN** an authenticated user opens `/dashboard` and the roadmap request succeeds with one or more roadmaps
- **THEN** the dashboard greets the authenticated user and presents a clear action to browse or open the available roadmap experience

#### Scenario: Load the dashboard without roadmaps
- **WHEN** the roadmap request succeeds with an empty list
- **THEN** the dashboard displays an honest empty state and does not substitute sample milestones or progress

#### Scenario: Fail to load dashboard roadmap context
- **WHEN** the roadmap request fails
- **THEN** the dashboard displays an inline error with a retry action that does not require a full browser reload

### Requirement: Honest Product Content and Affordances
Frontend copy and controls SHALL describe implemented MVP behavior and SHALL NOT present unverified community counts, generated user activity, or out-of-scope features as currently available. Every visible link or button SHALL lead to an implemented route, invoke an implemented action, or be clearly rendered as unavailable non-interactive content.

#### Scenario: Review public product claims
- **WHEN** a visitor reads the landing or authentication pages
- **THEN** the product description focuses on choosing and following structured developer roadmaps
- **THEN** the page does not claim a fabricated user count or promote peer review, AI review, or community features as available

#### Scenario: Activate application navigation
- **WHEN** a user activates a visible navigation item
- **THEN** the item leads to a route implemented by the current router
- **THEN** the navigation does not expose undefined Skill Tree, Learning Space, Code Review, or Leaderboard destinations

## MODIFIED Requirements

### Requirement: Public Landing Experience
The landing page SHALL present Journi.dev as a focused developer roadmap tracker. It SHALL explain the implemented MVP journey in concise language and SHALL provide sign-up and sign-in calls to action for visitors or a dashboard call to action for an authenticated user. Decorative content SHALL remain subordinate to the auth entry points and SHALL NOT advertise unavailable product modules.

#### Scenario: Viewing the home page as a visitor
- **WHEN** an unauthenticated visitor opens `/`
- **THEN** they see roadmap-focused product positioning and clear sign-up and sign-in entry points
- **THEN** they do not see fabricated community metrics or teaser cards for unavailable peer-review or social features

#### Scenario: Viewing the home page while authenticated
- **WHEN** an authenticated user opens `/`
- **THEN** they see a clear action to return to the dashboard without being prompted to create another account

### Requirement: Sign-In Form Experience
The sign-in page SHALL collect username and password using the existing backend login contract. It SHALL support password visibility, communicate pending and failure states, prevent duplicate submissions while pending, and navigate to `/dashboard` after storing the successful JWT response through the existing auth flow.

#### Scenario: Toggling password visibility on sign-in
- **WHEN** a user presses the sign-in password visibility button
- **THEN** the password field toggles between masked and plain-text display
- **THEN** the control exposes an accessible label describing the resulting action

#### Scenario: Submitting valid credentials
- **WHEN** a user submits a username and password accepted by `POST /api/v1/auth/login`
- **THEN** the frontend communicates the pending state, stores the returned access token, and navigates to `/dashboard`

#### Scenario: Receiving invalid credentials
- **WHEN** the login endpoint rejects the submitted credentials
- **THEN** the form keeps the entered username, presents an inline actionable error, and returns keyboard focus to an appropriate location without logging sensitive values

### Requirement: Dashboard Layout with Nested Routes
The system SHALL preserve the dashboard shell while navigating between implemented authenticated sections. Desktop navigation SHALL expose Overview and Roadmaps plus the existing Profile identity entry, and narrow viewports SHALL expose the same destinations through a collapsible accessible navigation surface.

#### Scenario: Navigate to Roadmaps on desktop
- **WHEN** a user clicks the Roadmaps item in the persistent dashboard navigation
- **THEN** the URL updates to `/dashboard/roadmaps`
- **THEN** the main content renders the Roadmap Page without unmounting the dashboard shell

#### Scenario: Navigate on a narrow viewport
- **WHEN** a user opens the dashboard navigation menu on a narrow viewport and selects an implemented destination
- **THEN** the selected nested route renders and the navigation menu closes

## REMOVED Requirements

### Requirement: Dashboard Demo Surface
**Reason**: The static heatmap, milestone cards, quick actions, leaderboard, and fabricated identities conflict with the MVP roadmap focus and can mislead users into believing unimplemented features or progress data are real.

**Migration**: Replace the demo surface with the Roadmap-First Dashboard Overview requirement, using the existing auth context and roadmap service only.

