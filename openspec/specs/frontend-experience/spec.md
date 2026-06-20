## Purpose

Describe the implemented client-side routing, roadmap-focused public and authentication screens, authenticated dashboard shell, roadmap-first overview, and not-found recovery flow.

## Requirements

### Requirement: Client-Side Route Shell
The frontend SHALL be a client-rendered React application using `react-router-dom`. The route shell SHALL define a public landing page at `/`, auth pages at `/signin` and `/signup`, a dashboard page at `/dashboard`, and a catch-all route that renders a dedicated not-found screen.

#### Scenario: Navigating between top-level routes
- **WHEN** a user visits `/`, `/signin`, `/signup`, `/dashboard`, or an unknown path
- **THEN** the router renders the corresponding page component without a full page reload

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

### Requirement: Sign-Up Form Experience
The sign-up page SHALL collect `username`, `email`, `password`, and terms acceptance state. The submit button SHALL remain disabled until all fields are present and the user has accepted the terms. When submitted, the page SHALL call the backend signup service, communicate loading state, show validation or submission failures inline, and present a clear success state that reflects account creation without assuming the user is already authenticated.

#### Scenario: Leaving required sign-up inputs incomplete
- **WHEN** the username, email, or password field is empty, or terms acceptance is false
- **THEN** the primary sign-up button remains disabled

#### Scenario: Submitting the sign-up form successfully
- **WHEN** a user submits the completed sign-up form with unique credentials
- **THEN** the page prevents default browser submission, calls the backend signup endpoint, and shows account-creation success feedback without storing an access token

#### Scenario: Receiving a backend validation failure during signup
- **WHEN** the backend rejects the sign-up request because the username or email is already in use
- **THEN** the page keeps the user on the sign-up form and displays a clear error message describing the failure

### Requirement: Dashboard Layout with Nested Routes
The system SHALL preserve the dashboard shell while navigating between implemented authenticated sections. Desktop navigation SHALL expose Overview and Roadmaps plus the existing Profile identity entry, and narrow viewports SHALL expose the same destinations through a collapsible accessible navigation surface.

#### Scenario: Navigate to Roadmaps on desktop
- **WHEN** a user clicks the Roadmaps item in the persistent dashboard navigation
- **THEN** the URL updates to `/dashboard/roadmaps`
- **THEN** the main content renders the Roadmap Page without unmounting the dashboard shell

#### Scenario: Navigate on a narrow viewport
- **WHEN** a user opens the dashboard navigation menu on a narrow viewport and selects an implemented destination
- **THEN** the selected nested route renders and the navigation menu closes

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

### Requirement: Not-Found Recovery Screen
The frontend SHALL provide a branded 404 experience for unknown client-side routes. The screen SHALL communicate that the requested resource is unavailable. If the user has a prior navigation history within the app, the screen SHALL provide a "Go Back" button that returns them to the previous page. If there is no navigation history, the screen SHALL provide a "Go to Homepage" button linking back to the landing page (`/`).

#### Scenario: Visiting an unknown route with navigation history
- **WHEN** a user navigates from an existing app page to an unknown route
- **THEN** the application renders the not-found page with a "Go Back" button
- **THEN** clicking the button executes a browser back action (`navigate(-1)`)

#### Scenario: Visiting an unknown route without navigation history
- **WHEN** a user opens an unknown route directly
- **THEN** the application renders the not-found page with a "Go to Homepage" button
- **THEN** clicking the button navigates the user to `/`

