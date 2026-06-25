## Purpose
Capture the repository-wide technical baseline for Journi.dev, including the code layout, runtime topology, backend layering, and current security posture.
## Requirements
### Requirement: Repository Layering
The system SHALL be organized as a split frontend and backend repository with documentation and OpenSpec assets alongside the product code. The primary source tree SHALL keep the React application in `src/frontend`, the Spring Boot application in `src/backend`, reference documents in `docs`, and specification artifacts in `openspec`.

#### Scenario: Inspecting the repository root
- **WHEN** a contributor explores the repository checkout
- **THEN** they find separate top-level locations for product code, reference documentation, and OpenSpec artifacts

### Requirement: Local Runtime Topology
The local development environment SHALL be runnable as a five-service container topology. `src/docker-compose.yml` SHALL define frontend, backend, grader, PostgreSQL database, and Redis cache services. The public backend and non-web grader SHALL depend on the database and cache; only the grader boundary SHALL receive the local container-launch capability and grader workspace/assets mounts.

#### Scenario: Reading the local orchestration file
- **WHEN** a contributor reviews `src/docker-compose.yml`
- **THEN** they can trace how frontend, backend, grader, database, and cache are started together for local development
- **THEN** the Docker socket is mounted only into the grader service and no grader HTTP port is exposed

### Requirement: Layered Backend Structure
The backend SHALL follow a layered Spring Boot structure. HTTP entry points SHALL live in `controllers`, business logic in `services`, persistence adapters in `repositories`, transport models in `dtos`, security wiring in `configurations`, and database-mapped aggregates in `entities`.

#### Scenario: Inspecting the backend package layout
- **WHEN** a contributor explores `src/backend/src/main/java/journi/dev/backend`
- **THEN** they find controllers, services, repositories, DTOs, entities, and security configuration packages with distinct responsibilities

### Requirement: JWT-Aware Security Baseline
The backend SHALL use stateless Spring Security with a JWT filter and DAO-backed authentication provider. Signup, login, refresh, logout, and CSRF bootstrap endpoints SHALL remain publicly reachable according to their endpoint contracts, while other API routes SHALL require an authenticated principal. A valid bearer access token SHALL populate the security context before protected controllers execute.

#### Scenario: Call a protected API without authentication
- **WHEN** a request reaches a protected roadmap, challenge, submission, or user endpoint without valid authentication
- **THEN** the security chain rejects it with an authentication error before protected business logic executes

#### Scenario: Call a protected API with a bearer token
- **WHEN** a request includes a valid `Authorization: Bearer <token>` header
- **THEN** the JWT filter loads the corresponding user and attaches authentication to the security context before the controller executes

#### Scenario: Call a public authentication endpoint
- **WHEN** a client calls an allowlisted authentication bootstrap endpoint without an access token
- **THEN** the security chain permits the request to reach that endpoint's own validation and CSRF rules

### Requirement: Partially Connected Vertical Slice
The current repository SHALL distinguish implemented MVP behavior from future domain scaffolding. Authentication, roadmap retrieval, progress-aware nodes, learner-confirmed lesson completion, challenge delivery, public GitHub revision submission, deterministic grader orchestration, submission feedback, and assessment-driven progress are implemented vertical slices. Peer review, AI review, GitHub webhooks, connected private repositories, community collaboration, advanced ranking, and deployment automation remain future or persistence-only capabilities.

#### Scenario: Comparing the codebase with product documents
- **WHEN** a contributor reads the README, SRS, ERD, OpenSpec artifacts, and current source code together
- **THEN** they can identify deterministic GitHub practice as implemented but configuration-gated
- **THEN** they can distinguish future peer, AI, webhook, private-repository, community, ranking, and deployment capabilities from current runtime behavior

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
