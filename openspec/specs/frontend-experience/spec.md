## Purpose
Describe the implemented frontend experience, including routing, public marketing pages, auth screens, the dashboard demo surface, and the branded not-found flow.

## Requirements

### Requirement: Client-Side Route Shell
The frontend SHALL be a client-rendered React application using `react-router-dom`. The route shell SHALL define a public landing page at `/`, auth pages at `/signin` and `/signup`, a dashboard page at `/dashboard`, and a catch-all route that renders a dedicated not-found screen.

#### Scenario: Navigating between top-level routes
- **WHEN** a user visits `/`, `/signin`, `/signup`, `/dashboard`, or an unknown path
- **THEN** the router renders the corresponding page component without a full page reload

### Requirement: Public Landing Experience
The landing page SHALL present Journi.dev as a gamified developer learning platform. It SHALL surface the core product themes through hero copy, sign-in and sign-up calls to action, and floating showcase cards for skill tree progress, peer code review, and daily streaks.

#### Scenario: Viewing the home page
- **WHEN** a visitor opens `/`
- **THEN** they see product positioning, primary auth entry points, community stats, and animated product teaser cards

### Requirement: Sign-In Form Experience
The sign-in page SHALL provide a form-driven sign-in experience with local UI state for email, password, remember-me selection, and password visibility. The current implementation SHALL remain presentation-first: submit handling logs the captured values locally and does not yet call a backend auth service.

#### Scenario: Toggling password visibility on sign-in
- **WHEN** a user presses the sign-in password visibility button
- **THEN** the password field toggles between masked and plain-text display

#### Scenario: Submitting the sign-in form
- **WHEN** a user submits the current sign-in form
- **THEN** the page prevents default browser submission and handles the data locally without issuing an API request

### Requirement: Sign-Up Form Experience
The sign-up page SHALL collect name, email, password, and terms acceptance state. The submit button SHALL remain disabled until all fields are present and the user has accepted the terms, and the current submit behavior SHALL log values locally rather than creating a real account.

#### Scenario: Leaving required sign-up inputs incomplete
- **WHEN** any sign-up field is empty or terms acceptance is false
- **THEN** the primary sign-up button remains disabled

#### Scenario: Completing the sign-up form
- **WHEN** all sign-up fields are filled and terms acceptance is enabled
- **THEN** the primary sign-up button becomes interactive even though the form still uses a local placeholder submit handler

### Requirement: Dashboard Demo Surface
The dashboard SHALL act as a high-fidelity demo of the intended logged-in experience. It SHALL render a sidebar, streak badge, contribution heatmap, milestone cards, quick actions, and a leaderboard panel using hard-coded sample data rather than backend-driven state.

#### Scenario: Viewing the dashboard page
- **WHEN** a user opens `/dashboard`
- **THEN** they see a fully composed learning dashboard with static user identity, static milestone and leaderboard data, and a heatmap generated from placeholder values

### Requirement: Not-Found Recovery Screen
The frontend SHALL provide a branded 404 experience for unknown client-side routes. The screen SHALL communicate that the requested resource is unavailable and SHALL provide a clear link back to the landing page.

#### Scenario: Visiting an unknown route
- **WHEN** a user opens a route that is not registered in the router
- **THEN** the application renders the not-found page and offers a return path to `/`
