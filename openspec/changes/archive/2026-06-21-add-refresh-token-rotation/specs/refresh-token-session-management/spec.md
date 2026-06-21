## ADDED Requirements

### Requirement: Refresh Session Issuance
After successful username-and-password authentication, the backend SHALL issue a short-lived JWT access token in the response body and a cryptographically random opaque refresh token in a host-only cookie. The refresh cookie SHALL be `HttpOnly`, SHALL have an explicit `SameSite` policy and bounded lifetime, SHALL be scoped to the auth API path, and SHALL be `Secure` outside the local-development profile. The backend SHALL persist only a one-way digest of the refresh token with its user, token-family, expiry, and revocation metadata.

#### Scenario: Login creates a renewable session
- **WHEN** a user submits valid credentials to `POST /api/v1/auth/login` with a valid CSRF request
- **THEN** the backend returns the access-token payload, sets the hardened refresh cookie, and stores the corresponding token digest without persisting the plaintext credential

#### Scenario: Separate logins create separate sessions
- **WHEN** the same user logs in successfully from two browser sessions
- **THEN** the backend creates independent refresh-token families so revoking one browser session does not revoke the other

### Requirement: Refresh Token Rotation
The backend SHALL expose `POST /api/v1/auth/refresh` as a public authentication endpoint that accepts the refresh credential only from its cookie. A valid, active, unexpired credential SHALL be consumed atomically through pessimistic write locking, replaced by a new refresh credential in the same token family, and accompanied by a new short-lived access token. Rotation SHALL retain the family's absolute expiry instead of extending the session indefinitely, and the rotated cookie's `Max-Age` SHALL equal only the family's remaining absolute lifetime.

#### Scenario: Active session renews access
- **WHEN** a client sends a valid refresh cookie and valid CSRF request to `POST /api/v1/auth/refresh`
- **THEN** the backend revokes the presented refresh record, persists a replacement digest in the same family, returns a new access token, and replaces the refresh cookie

#### Scenario: Concurrent rotation accepts one credential use
- **WHEN** two refresh requests concurrently present the same active refresh credential
- **THEN** the backend performs at most one successful rotation and rejects the other request without creating two active successors

### Requirement: Refresh Credential Rejection and Replay Response
The backend SHALL reject a missing, malformed, unknown, expired, or revoked refresh credential with `401 Unauthorized` and clear the refresh cookie. If a previously rotated credential is presented again, the backend SHALL treat it as replay, revoke every still-active record in that token family, and require a new login.

#### Scenario: Expired refresh credential is rejected
- **WHEN** a client presents a refresh credential whose absolute expiry has passed
- **THEN** the backend returns `401 Unauthorized`, creates no access token or successor session, and clears the refresh cookie

#### Scenario: Rotated credential is replayed
- **WHEN** a client presents a refresh credential that was already replaced during rotation
- **THEN** the backend revokes the remaining active token family, returns `401 Unauthorized`, and clears the refresh cookie

### Requirement: Refresh Session Logout
After CSRF validation succeeds, the backend SHALL make `POST /api/v1/auth/logout` idempotent for browser session cleanup. When a valid refresh cookie is present, logout SHALL revoke its token family; when the cookie is missing, unknown, expired, or already revoked, the response SHALL still clear the refresh cookie and return `204 No Content` without revealing refresh-token state or requiring a still-valid access token.

#### Scenario: User logs out an active browser session
- **WHEN** the client sends a valid CSRF-protected logout request with an active refresh cookie
- **THEN** the backend revokes that refresh-token family, clears the cookie, and returns `204 No Content`

#### Scenario: Client cleans up an already-invalid session
- **WHEN** the client sends a valid CSRF-protected logout request without an active refresh credential
- **THEN** the backend still clears the cookie and returns `204 No Content`

### Requirement: CSRF and Credentialed CORS Protection
The backend SHALL expose a safe auth CSRF bootstrap operation and SHALL require the corresponding CSRF header for login, refresh, and logout. Credentialed CORS SHALL allow only configured frontend origins; wildcard origins SHALL NOT be used with credentials. Requests that fail CSRF or trusted-origin validation SHALL NOT create, rotate, or revoke refresh sessions.

#### Scenario: Frontend obtains CSRF material
- **WHEN** the trusted frontend requests the auth CSRF bootstrap operation with credentials enabled
- **THEN** the backend returns CSRF material that the frontend can echo in the required header without exposing the refresh credential to JavaScript

#### Scenario: Cookie-backed auth mutation lacks CSRF proof
- **WHEN** a login, refresh, or logout request includes no valid CSRF header
- **THEN** the backend rejects the request with `403 Forbidden` and does not mutate refresh-session state

#### Scenario: Untrusted origin attempts credentialed auth
- **WHEN** a browser request originates outside the configured frontend allowlist
- **THEN** the backend does not grant credentialed CORS access and does not accept it as a refresh-session mutation

### Requirement: Refresh Persistence Independence from Redis
Refresh-session issuance, validation, rotation, replay detection, and revocation SHALL use the existing PostgreSQL persistence layer and SHALL NOT require Redis to be available. Normal JWT validation for protected resource requests SHALL remain stateless and SHALL NOT query the refresh-session table.

#### Scenario: Cache service is unavailable during refresh
- **WHEN** Redis is unavailable and a client presents a valid refresh credential
- **THEN** the PostgreSQL-backed refresh flow can still rotate the session and issue a new access token

#### Scenario: Protected API receives a valid access token
- **WHEN** a protected roadmap request contains a valid unexpired access JWT
- **THEN** the JWT filter authenticates it without querying refresh-session persistence
