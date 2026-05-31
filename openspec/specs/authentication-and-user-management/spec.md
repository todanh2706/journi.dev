## Purpose
Document the current authentication and user-management behavior across the shared frontend API client, backend auth endpoints, and user persistence layer.

## Requirements

### Requirement: Token-Aware API Client
The frontend API client SHALL centralize outbound HTTP setup through an Axios instance. When `localStorage` contains `access_token`, the client SHALL attach it to outgoing requests as a bearer token in the `Authorization` header.

#### Scenario: Calling an API with a stored token
- **WHEN** frontend code uses the shared Axios client and `localStorage.access_token` is present
- **THEN** the outgoing request includes `Authorization: Bearer <token>`

### Requirement: Auth Signup Endpoint
The backend SHALL expose `POST /api/v1/auth/signup` for self-service account creation. A successful signup SHALL create a user with the submitted username and email, hash the submitted password, set role to `USER`, set status to `ACTIVE`, and mark the account as enabled.

#### Scenario: Registering a new account
- **WHEN** a client sends a valid signup request to `POST /api/v1/auth/signup`
- **THEN** the backend persists a new enabled user record with hashed credentials and default active user status

### Requirement: Auth Login Endpoint
The backend SHALL expose `POST /api/v1/auth/login` for username-and-password authentication. Successful login SHALL authenticate against Spring Security and return a JWT plus its configured expiration time.

#### Scenario: Logging in with valid credentials
- **WHEN** a client submits a valid username and password to `POST /api/v1/auth/login`
- **THEN** the backend returns a token payload containing `token` and `expiresIn`

### Requirement: User CRUD API
The backend SHALL expose user management endpoints at `/api/v1/users`. The API SHALL support listing all users, retrieving a user by UUID, creating a user from request DTO fields, updating a user by UUID, and deleting a user by UUID.

#### Scenario: Listing user records
- **WHEN** a client calls `GET /api/v1/users`
- **THEN** the backend responds with a collection of `UserResponse` DTOs

#### Scenario: Creating a user through the CRUD endpoint
- **WHEN** a client sends a valid `POST /api/v1/users` request
- **THEN** the backend persists a user record and returns a `201 Created` response with a `UserResponse` payload

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
