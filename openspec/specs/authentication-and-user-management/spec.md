## Purpose
Document the current authentication and user-management behavior across the shared frontend API client, backend auth endpoints, and user persistence layer.
## Requirements
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

### Requirement: Auth Signup Endpoint
The backend SHALL expose `POST /api/v1/auth/signup` for self-service account creation. The endpoint SHALL validate username and email uniqueness, rejecting duplicates with a `400 Bad Request`. A successful signup SHALL create a user with the submitted username and email, hash the submitted password, set role to `USER`, set status to `ACTIVE`, mark the account as enabled, and return a sanitized `UserResponse`.

#### Scenario: Registering a new account
- **WHEN** a client sends a valid signup request with a unique username and email to `POST /api/v1/auth/signup`
- **THEN** the backend persists a new enabled user record with hashed credentials and default active user status, and returns a `UserResponse`

#### Scenario: Registering with duplicate credentials
- **WHEN** a client sends a signup request with an already-registered username or email
- **THEN** the backend rejects the request with a `400 Bad Request` status

### Requirement: Frontend Signup Submission Contract
The frontend sign-up experience SHALL submit account-creation requests to `POST /api/v1/auth/signup` using the backend-owned request contract. The request body SHALL contain `username`, `email`, and `password`, and the frontend SHALL treat a successful response as account creation rather than authentication because the backend returns a sanitized `UserResponse` and no JWT.

#### Scenario: Submitting a valid signup request from the frontend
- **WHEN** a user completes the sign-up form and submits valid `username`, `email`, and `password` values
- **THEN** the frontend sends those fields to `POST /api/v1/auth/signup` and treats the returned `UserResponse` as a successful registration result

#### Scenario: Handling duplicate signup credentials from the backend
- **WHEN** the backend rejects a sign-up request with `400 Bad Request` because the username or email is already registered
- **THEN** the frontend surfaces a validation error to the user and does not treat the request as a successful login

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

### Requirement: User CRUD API
The backend SHALL expose user management endpoints at `/api/v1/users`. All endpoints in this path SHALL require authentication. The API SHALL support listing all users with pagination, retrieving a user by UUID, creating an enabled user from request DTO fields, updating a user by UUID using a sanitized `UserRequest` DTO (preventing mass assignment), and deleting a user by UUID.

#### Scenario: Listing user records
- **WHEN** an authenticated client calls `GET /api/v1/users` (optionally with page and size parameters)
- **THEN** the backend responds with a paginated collection of `UserResponse` DTOs, including metadata such as total elements and total pages

#### Scenario: Creating a user through the CRUD endpoint
- **WHEN** an authenticated client sends a valid `POST /api/v1/users` request
- **THEN** the backend persists an enabled user record and returns a `201 Created` response with a `UserResponse` payload

#### Scenario: Updating a user through the CRUD endpoint
- **WHEN** an authenticated client sends a `PUT /api/v1/users/{id}` request with a `UserRequest` payload
- **THEN** the backend safely updates only the allowed fields (username, email, password) without altering internal state like roles or statuses, and returns a `UserResponse` payload

### Requirement: Password Hashing for Backend-Provisioned Users
Any user account created through backend-owned creation flows SHALL store a password hash rather than the raw password. This applies both to the auth signup flow and to the direct user creation endpoint.

#### Scenario: Creating a user through backend flows
- **WHEN** the backend provisions a user from `UserRequest`
- **THEN** it encodes the submitted password before persisting the record

### Requirement: Soft-Delete Aware User Persistence
The `User` aggregate SHALL support soft deletion and Spring Security compatibility. User records SHALL implement `UserDetails`, expose no authorities by default, and use Hibernate soft-delete filters so deleted rows are hidden from ordinary reads.

#### Scenario: Deleting a user entity through JPA-managed flows
- **WHEN** the `User` entity is deleted through Hibernate-managed persistence
- **THEN** the mapped table uses the configured soft-delete behavior and excludes deleted rows from normal entity queries

### Requirement: Frontend Sign-In Redirect
The frontend sign-in experience SHALL redirect the user to the `/dashboard` route upon a successful authentication response from the backend.

#### Scenario: Successful sign-in redirect
- **WHEN** a user submits valid credentials and receives a successful login response
- **THEN** the frontend automatically navigates the user to `/dashboard`

### Requirement: Frontend Session Bootstrap
The frontend SHALL provide one shared authentication state for the React application. On initial application load it SHALL attempt to restore the session through the refresh flow while exposing a loading state; it SHALL then expose the authenticated user and in-memory access token state, or a guest state when no refresh session can be restored.

#### Scenario: Application restores a valid session
- **WHEN** the application starts while the browser holds an active refresh cookie
- **THEN** the auth provider obtains a new access token, exposes the authenticated user, and allows auth-aware screens to render the signed-in state

#### Scenario: Application starts without a renewable session
- **WHEN** the application starts without a valid refresh session
- **THEN** the auth provider clears any stale client auth state, finishes loading, and exposes the guest state without entering a refresh loop
