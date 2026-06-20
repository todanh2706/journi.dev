## ADDED Requirements

### Requirement: Authenticated Logout Endpoint
The backend SHALL expose `POST /api/v1/auth/logout` as an authenticated boundary for ending the frontend's stateless JWT session. A successful request SHALL return `204 No Content`, after which the frontend SHALL remove its locally stored access token. The endpoint and normal JWT validation SHALL NOT depend on Redis or another cache service.

#### Scenario: Authenticated user logs out
- **WHEN** a client sends `POST /api/v1/auth/logout` with a valid bearer JWT
- **THEN** the backend returns `204 No Content` and the frontend removes its locally stored access token

#### Scenario: Logout request has no valid authentication
- **WHEN** a client sends `POST /api/v1/auth/logout` without a valid bearer JWT
- **THEN** the backend rejects the request as unauthenticated and the frontend keeps the current session available for retry

#### Scenario: Cache service is unavailable
- **WHEN** Redis or another cache service is unavailable and a client presents a valid JWT to a protected roadmap endpoint
- **THEN** the existing JWT validation flow authenticates the user without contacting the cache service
