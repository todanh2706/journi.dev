## ADDED Requirements

### Requirement: Frontend Session Bootstrap
The frontend SHALL provide one shared authentication state for the React application. On initial application load it SHALL attempt to restore the session through the refresh flow while exposing a loading state; it SHALL then expose the authenticated user and in-memory access token state, or a guest state when no refresh session can be restored.

#### Scenario: Application restores a valid session
- **WHEN** the application starts while the browser holds an active refresh cookie
- **THEN** the auth provider obtains a new access token, exposes the authenticated user, and allows auth-aware screens to render the signed-in state

#### Scenario: Application starts without a renewable session
- **WHEN** the application starts without a valid refresh session
- **THEN** the auth provider clears any stale client auth state, finishes loading, and exposes the guest state without entering a refresh loop

## MODIFIED Requirements

### Requirement: Token-Aware API Client
The frontend API client SHALL centralize outbound HTTP setup through the existing Axios instance. It SHALL keep the access token in application memory rather than `localStorage`, attach the current token as a bearer `Authorization` header, send cookies only where credentialed auth transport is required, and perform at most one retry after successfully renewing an eligible request that receives `401 Unauthorized`. Concurrent unauthorized requests SHALL share one in-flight refresh operation, and auth endpoints SHALL NOT recursively trigger refresh.

#### Scenario: Calling an API with an active access token
- **WHEN** frontend code uses the shared Axios client while an access token is held in application memory
- **THEN** the outgoing protected request includes `Authorization: Bearer <token>`

#### Scenario: Concurrent protected requests encounter expired access
- **WHEN** multiple eligible API requests receive `401 Unauthorized` for the same expired access token
- **THEN** the client performs one refresh operation, retries each request once with the replacement access token, and does not create a refresh storm

#### Scenario: Session renewal fails
- **WHEN** an eligible request receives `401 Unauthorized` and the shared refresh operation fails
- **THEN** the client clears in-memory authentication state, does not retry recursively, and exposes the guest session state

### Requirement: Auth Login Endpoint
The backend SHALL expose `POST /api/v1/auth/login` for username-and-password authentication. Successful login SHALL authenticate through Spring Security, create a refresh session, set its refresh cookie, and return a short-lived JWT access token plus its configured expiration time. The frontend SHALL hold the returned access token only in the shared in-memory auth session.

#### Scenario: Logging in with valid credentials
- **WHEN** a client submits a valid username and password with valid CSRF proof to `POST /api/v1/auth/login`
- **THEN** the backend returns a token payload containing `token` and `expiresIn`, sets the refresh cookie, and the frontend enters the authenticated state without writing the access token to `localStorage`

#### Scenario: Logging in with invalid credentials
- **WHEN** a client submits an invalid username or password to `POST /api/v1/auth/login`
- **THEN** the backend returns `401 Unauthorized` and creates no refresh session or refresh cookie

### Requirement: Authenticated Logout Endpoint
The backend SHALL expose `POST /api/v1/auth/logout` as the boundary for ending the current refresh-backed browser session. Logout SHALL revoke the presented refresh-token family when possible, clear the refresh cookie, and return `204 No Content` even when the access token is expired or absent. After the request settles, the frontend SHALL clear its in-memory access token and authenticated user state. The endpoint and normal JWT validation SHALL NOT depend on Redis or another cache service.

#### Scenario: Authenticated user logs out
- **WHEN** a client sends a valid CSRF-protected `POST /api/v1/auth/logout` with an active refresh cookie
- **THEN** the backend revokes the current browser session and returns `204 No Content`, and the frontend clears its in-memory auth state

#### Scenario: Logout occurs after access expiry
- **WHEN** a client sends a valid CSRF-protected logout request with no valid bearer JWT but with an active refresh cookie
- **THEN** the backend revokes the refresh session, clears the cookie, and returns `204 No Content`

#### Scenario: Logout cannot reach the backend
- **WHEN** a user requests logout but the network call fails
- **THEN** the frontend still clears its in-memory auth state while surfacing or recording the failed server revocation for deliberate handling rather than retaining local access

#### Scenario: Cache service is unavailable
- **WHEN** Redis or another cache service is unavailable and a client presents a valid JWT to a protected roadmap endpoint
- **THEN** the existing JWT validation flow authenticates the user without contacting the cache service
