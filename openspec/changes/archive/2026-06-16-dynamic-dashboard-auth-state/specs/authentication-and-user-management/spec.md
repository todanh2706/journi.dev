## ADDED Requirements

### Requirement: Frontend Sign-In Redirect
The frontend sign-in experience SHALL redirect the user to the `/dashboard` route upon a successful authentication response from the backend.

#### Scenario: Successful sign-in redirect
- **WHEN** a user submits valid credentials and receives a successful login response
- **THEN** the frontend automatically navigates the user to `/dashboard`
