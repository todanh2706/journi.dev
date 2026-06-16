## ADDED Requirements

### Requirement: Frontend Signup Submission Contract
The frontend sign-up experience SHALL submit account-creation requests to `POST /api/v1/auth/signup` using the backend-owned request contract. The request body SHALL contain `username`, `email`, and `password`, and the frontend SHALL treat a successful response as account creation rather than authentication because the backend returns a sanitized `UserResponse` and no JWT.

#### Scenario: Submitting a valid signup request from the frontend
- **WHEN** a user completes the sign-up form and submits valid `username`, `email`, and `password` values
- **THEN** the frontend sends those fields to `POST /api/v1/auth/signup` and treats the returned `UserResponse` as a successful registration result

#### Scenario: Handling duplicate signup credentials from the backend
- **WHEN** the backend rejects a sign-up request with `400 Bad Request` because the username or email is already registered
- **THEN** the frontend surfaces a validation error to the user and does not treat the request as a successful login
