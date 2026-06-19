## Purpose
Capture the repository-wide technical baseline for Journi.dev, including the code layout, runtime topology, backend layering, and current security posture.

## Requirements

### Requirement: Repository Layering
The system SHALL be organized as a split frontend and backend repository with documentation and OpenSpec assets alongside the product code. The primary source tree SHALL keep the React application in `src/frontend`, the Spring Boot application in `src/backend`, reference documents in `docs`, and specification artifacts in `openspec`.

#### Scenario: Inspecting the repository root
- **WHEN** a contributor explores the repository checkout
- **THEN** they find separate top-level locations for product code, reference documentation, and OpenSpec artifacts

### Requirement: Local Runtime Topology
The local development environment SHALL be runnable as a four-service container topology. `src/docker-compose.yml` SHALL define a frontend container, a backend container, a PostgreSQL database container, and a Redis cache container, with the backend configured to depend on the database and cache services.

#### Scenario: Reading the local orchestration file
- **WHEN** a contributor reviews `src/docker-compose.yml`
- **THEN** they can trace how the frontend, backend, database, and cache are started together for local development

### Requirement: Layered Backend Structure
The backend SHALL follow a layered Spring Boot structure. HTTP entry points SHALL live in `controllers`, business logic in `services`, persistence adapters in `repositories`, transport models in `dtos`, security wiring in `configurations`, and database-mapped aggregates in `entities`.

#### Scenario: Inspecting the backend package layout
- **WHEN** a contributor explores `src/backend/src/main/java/journi/dev/backend`
- **THEN** they find controllers, services, repositories, DTOs, entities, and security configuration packages with distinct responsibilities

### Requirement: JWT-Aware Security Baseline
The backend SHALL initialize stateless Spring Security with a JWT filter and DAO-backed authentication provider. The current security baseline SHALL permit anonymous access to all routes, while still attempting to parse bearer tokens and populate the security context when an `Authorization` header is present.

#### Scenario: Calling the API without a bearer token
- **WHEN** a request reaches the backend without an `Authorization` header
- **THEN** the security chain allows the request to continue without authenticated principal context

#### Scenario: Calling the API with a bearer token
- **WHEN** a request includes `Authorization: Bearer <token>`
- **THEN** the JWT filter attempts to extract the username, load the corresponding user, and attach authentication to the security context before the controller executes

### Requirement: Partially Connected Vertical Slice
The current repository baseline SHALL document an implementation gap between product intent and running behavior. The frontend already presents the main user journeys and product themes, while the backend exposes working auth, user, roadmap, node, streak, and notification endpoints and also carries a broader persistence model for future domains such as submissions, peer review, leaderboards, AI review, and deployment environments.

#### Scenario: Comparing the codebase with the product documents
- **WHEN** a contributor reads the README, SRS, ERD, and current source code together
- **THEN** they can distinguish which capabilities already have UI or API behavior and which capabilities currently exist as domain scaffolding only

### Requirement: Feature-Oriented Frontend Source Layout
The frontend source tree SHALL organize domain-specific code under feature folders while preserving route-level pages and shared primitives.

#### Scenario: Inspecting frontend feature folders
- **WHEN** a contributor explores `src/frontend/src/features`
- **THEN** they find domain-owned frontend code grouped by feature area such as auth, dashboard, and roadmaps
- **THEN** feature folders contain their own components, services, hooks, utilities, and types when those files are specific to that domain

#### Scenario: Inspecting route pages
- **WHEN** a contributor explores `src/frontend/src/pages`
- **THEN** they find route-level screen files that compose feature code
- **THEN** page folders do not own domain services or reusable feature components unless those files are strictly page-local

### Requirement: Shared Frontend Boundary
The frontend SHALL keep shared top-level folders limited to cross-feature code.

#### Scenario: Inspecting shared components and utilities
- **WHEN** a contributor explores `src/frontend/src/components`, `services`, `utils`, or `assets`
- **THEN** files in those folders are reusable across more than one feature or provide application-wide infrastructure

#### Scenario: Placing domain-specific code
- **WHEN** a contributor adds code used only by one product domain
- **THEN** the code is placed under the matching `features/<domain>` folder instead of a shared top-level folder

### Requirement: Frontend Refactor Behavior Preservation
The frontend structure migration SHALL preserve existing user-facing behavior and public contracts.

#### Scenario: Preserving routes
- **WHEN** the frontend source tree is reorganized
- **THEN** existing client routes continue to render the same screens at the same paths

#### Scenario: Preserving API contracts
- **WHEN** frontend services or types are moved into feature folders
- **THEN** API paths, request shapes, response shapes, token handling, and Axios base configuration remain unchanged

#### Scenario: Preserving MVP roadmap flow
- **WHEN** the structure migration is complete
- **THEN** the login-to-roadmap flow remains available without adding non-MVP behavior
