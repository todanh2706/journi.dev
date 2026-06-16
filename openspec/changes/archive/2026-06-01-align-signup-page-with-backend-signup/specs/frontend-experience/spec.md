## MODIFIED Requirements

### Requirement: Sign-Up Form Experience
The sign-up page SHALL collect `username`, `email`, `password`, and terms acceptance state. The submit button SHALL remain disabled until all fields are present and the user has accepted the terms. When submitted, the page SHALL call the backend signup service, communicate loading state, show validation or submission failures inline, and present a clear success state that reflects account creation without assuming the user is already authenticated.

#### Scenario: Leaving required sign-up inputs incomplete
- **WHEN** the username, email, or password field is empty, or terms acceptance is false
- **THEN** the primary sign-up button remains disabled

#### Scenario: Submitting the sign-up form successfully
- **WHEN** a user submits the completed sign-up form with unique credentials
- **THEN** the page prevents the default browser submission, calls the backend signup endpoint, and shows account-creation success feedback without storing an access token

#### Scenario: Receiving a backend validation failure during signup
- **WHEN** the backend rejects the sign-up request because the username or email is already in use
- **THEN** the page keeps the user on the sign-up form and displays a clear error message describing the failure
