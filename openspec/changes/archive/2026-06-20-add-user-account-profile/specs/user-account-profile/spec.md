## ADDED Requirements

### Requirement: Dashboard Profile Navigation
The frontend SHALL expose an authenticated profile page at `/dashboard/profile` within the existing dashboard layout. The authenticated identity block containing the user's avatar and username in the sidebar SHALL navigate to this route without a full-page reload.

#### Scenario: Authenticated user opens profile from the sidebar
- **WHEN** an authenticated user activates their avatar or identity block in the dashboard sidebar
- **THEN** the router navigates to `/dashboard/profile` and retains the dashboard layout

#### Scenario: Guest views the sidebar identity area
- **WHEN** no valid access token is available
- **THEN** the sidebar SHALL NOT present the guest identity block as a link to the profile page

#### Scenario: Unauthenticated user visits the profile URL directly
- **WHEN** a user without a valid, unexpired access token visits `/dashboard/profile`
- **THEN** the frontend redirects the user to `/signin`

### Requirement: Account Management Surface
The profile page SHALL display the current JWT-derived username and SHALL present visually grouped controls for personal information, username/email, password changes, and session management using the existing dashboard visual language. Profile mutations that lack a self-service backend contract SHALL be clearly identified as unavailable and SHALL NOT call ID-based user administration endpoints.

#### Scenario: Authenticated user views account settings
- **WHEN** an authenticated user opens `/dashboard/profile`
- **THEN** the page shows their current username, avatar identity, account fields, password fields, and a separate logout section

#### Scenario: Backend profile editing is unavailable
- **WHEN** the user reviews or interacts with an unsupported profile-editing section
- **THEN** the page communicates that saving is not yet available and does not send a mutation request to `/api/v1/users/{id}` or another nonexistent profile endpoint

#### Scenario: Unknown personal information is rendered
- **WHEN** a personal field such as display name or email is not present in the JWT-derived auth state
- **THEN** the page leaves the value empty or explains that it is unavailable and does not fabricate account data

### Requirement: Confirmed Logout Experience
The profile page SHALL provide a clearly labeled logout action that requires confirmation before changing session state. The confirmation dialog SHALL support confirm and cancel actions, SHALL expose accessible dialog semantics, and SHALL prevent duplicate confirmation while logout is pending.

#### Scenario: User opens the logout confirmation
- **WHEN** the user activates the logout action
- **THEN** a confirmation dialog explains that the current session will end and presents confirm and cancel actions

#### Scenario: User cancels logout
- **WHEN** the user cancels the confirmation or dismisses it with Escape before a request is pending
- **THEN** the dialog closes and the access token and current route remain unchanged

#### Scenario: Logout succeeds
- **WHEN** the user confirms logout and `POST /api/v1/auth/logout` succeeds
- **THEN** the frontend removes `access_token`, clears local auth state, and replaces the current route with `/signin`

#### Scenario: Logout fails
- **WHEN** the confirmed logout request fails
- **THEN** the frontend retains the access token, displays a retryable error in the confirmation experience, and does not report that logout succeeded

#### Scenario: Logout request is pending
- **WHEN** the confirmed logout request is in progress
- **THEN** the confirmation action shows a pending state and additional confirmation submissions are disabled
