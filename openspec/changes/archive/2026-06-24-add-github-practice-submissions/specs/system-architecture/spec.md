## MODIFIED Requirements

### Requirement: Local Runtime Topology
The local development environment SHALL be runnable as a five-service container topology. `src/docker-compose.yml` SHALL define frontend, backend, grader, PostgreSQL database, and Redis cache services. The public backend and non-web grader SHALL depend on the database and cache; only the grader boundary SHALL receive the local container-launch capability and grader workspace/assets mounts.

#### Scenario: Reading the local orchestration file
- **WHEN** a contributor reviews `src/docker-compose.yml`
- **THEN** they can trace how frontend, backend, grader, database, and cache are started together for local development
- **THEN** the Docker socket is mounted only into the grader service and no grader HTTP port is exposed

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
