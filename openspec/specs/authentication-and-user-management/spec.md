## Purpose
Document the current authentication and user-management behavior across the shared frontend API client, backend auth endpoints, and user persistence layer.
## Requirements
### Requirement: Token-Aware API Client
The frontend API client SHALL centralize outbound HTTP setup through an Axios instance. When `localStorage` contains `access_token`, the client SHALL attach it to outgoing requests as a bearer token in the `Authorization` header.

#### Scenario: Calling an API with a stored token
- **WHEN** frontend code uses the shared Axios client and `localStorage.access_token` is present
- **THEN** the outgoing request includes `Authorization: Bearer <token>`

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
The backend SHALL expose `POST /api/v1/auth/login` for username-and-password authentication. Successful login SHALL authenticate against Spring Security and return a JWT plus its configured expiration time.

#### Scenario: Logging in with valid credentials
- **WHEN** a client submits a valid username and password to `POST /api/v1/auth/login`
- **THEN** the backend returns a token payload containing `token` and `expiresIn`

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
