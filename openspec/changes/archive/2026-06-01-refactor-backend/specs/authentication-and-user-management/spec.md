## MODIFIED Requirements

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
