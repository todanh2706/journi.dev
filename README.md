# Journi.dev: Interactive Programming Learning and Roadmap Navigation System

Journi.dev is currently a roadmap tracker for beginner developers. Its MVP guides an authenticated learner through predefined skill nodes, learning content, explicit lesson completion, GitHub-based practice, and prerequisite unlocking. AI review, webhooks, social learning, and dynamic roadmaps remain future work.

## Core Features

- **Interactive Roadmap System**: A directed-acyclic-graph (DAG) visualization representing hierarchical skill nodes. For unlocked theory lessons, learners explicitly mark the lesson as completed after reading it; completing every prerequisite unlocks the dependent node.
- **Deterministic GitHub Practice**: Learners work in a local IDE, push a public GitHub repository, and submit a branch plus exact commit SHA. A separate grader worker runs server-owned checks in a network-disabled, resource-limited container. No AI model or webhook participates in pass/fail decisions.
- **Milestone Communities**: Discussions organized around major milestones (e.g., Database Cluster, DevOps Cluster) to foster interactions among peers at similar stages of learning.
- **Gamification and Retention**: Activity heatmaps, learning streak tracking, automated inactive-user reminders, and specialized niche leaderboards (Frontend, Backend, Security) based on code quality and contributions.

## Technology Stack

### Frontend

- **Framework**: React.js with TypeScript for type-safe rendering of complex graph structures.
- **Styling**: Tailwind CSS for modern, premium visual design.
- **Graph Interaction**: React Flow for handling smooth drag-and-drop, zoom, and interactive skill nodes.
- **Hosting**: Vercel for continuous integration and optimized delivery.

### Backend

- **Framework**: Java Spring Boot for robust, enterprise-grade business logic.
- **Database**: PostgreSQL (primary database) for storing transactional user state, relationships, and JSONB roadmap structures.
- **Caching & Leaderboards**: Redis (in-memory database) for fast calculations of user streaks, cache management, and real-time leaderboards.
- **Hosting**: AWS EC2 virtual servers hosting Docker containers.

### Integration Services

- **GitHub API**: Verifies public repositories, branches, and immutable commit SHAs submitted by learners.
- **Redis Streams**: Delivers submission identifiers from the public API to the separate grader process.

## Directory Structure

```text
journi.dev/
├── docs/
│   ├── ERD.md          # Entity-Relationship Diagram specifications
│   ├── ERD.png          # Visual entity diagram
│   └── SRS.md          # Software Requirements Specification (PRD)
├── src/
│   ├── backend/        # Spring Boot application
│   │   ├── pom.xml
│   │   └── src/        # Java package sources
│   ├── frontend/       # React / Vite SPA
│   │   ├── package.json
│   │   └── src/        # Route pages, feature domains, and shared frontend primitives
│   └── docker-compose.yml
└── README.md           # Project documentation
```

### Frontend Source Layout

```text
src/frontend/src/
├── pages/              # Route-level screens only
│   ├── Auth/
│   ├── Dashboard/
│   ├── Home/
│   └── Roadmaps/
├── features/           # Domain-owned frontend code
│   ├── auth/
│   ├── dashboard/
│   └── roadmaps/
├── components/         # Cross-feature reusable UI
├── services/           # App-wide service infrastructure, such as the shared Axios client
├── utils/              # Cross-feature utilities
└── assets/             # Static frontend assets
```

## System Architecture and Database Schema

The database includes an authentication-session table alongside the roadmap, progress, submission, community, and AI-oriented domain tables.

Key entities include:

- **User**: General profile and role settings.
- **RefreshSession**: Stores only refresh-token digests and rotation-family state for revocation and replay detection.
- **LearningRoadmap & SkillNode**: Represent roadmap hierarchies.
- **Challenge & Submission**: Store learner briefs, server-owned deterministic grader configuration, immutable GitHub attempts, bounded feedback, and evaluation leases.
- **CodeReview & AIReviewTask**: Reserved future domains; they are not used by deterministic practice grading.
- **CommunityClusters & ClusterMembership**: Support social network functionalities and discussions.

For a full specification of fields, types, and schema relationships, refer to the [ERD Documentation](docs/ERD.md).

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java Development Kit (JDK) 25 (for local backend development)
- Node.js 18 or higher (for local frontend development)
- PostgreSQL client (optional)
- Redis CLI (optional)

### Environment Setup

Copy both checked-in examples before starting the local stack:

```bash
cp src/.env.example src/.env
cp src/backend/.env.example src/backend/.env
```

The root file contains database and Redis settings. The backend file contains the JWT secret and non-secret auth-session settings. Generate the JWT secret locally with `openssl rand -base64 32`; never commit the generated value.

`FRONTEND_ALLOWED_ORIGINS` is required and has no source-code fallback. When the backend is started from `src/backend`, Spring imports `src/backend/.env`; Docker Compose supplies the same file through the backend service's `env_file` setting.

Relevant settings are:

```ini
# Database Configuration
DB_NAME=journi_db
DB_USER=journi_user
DB_PASSWORD=journi_password

# Spring Boot Configuration
SPRING_DB_URL=jdbc:postgresql://database:5432/journi_db

# Redis Configuration
REDIS_HOST=cache
REDIS_PORT=6379
GRADER_BOOTSTRAP_SECRET=replace_with_a_separate_base64_encoded_32_byte_secret

# src/backend/.env
JWT_SECRET_KEY=replace_with_a_base64_encoded_32_byte_secret
ACCESS_TOKEN_LIFETIME=PT15M
REFRESH_TOKEN_LIFETIME=P30D
REFRESH_COOKIE_NAME=journi_refresh
REFRESH_COOKIE_SECURE=false
REFRESH_COOKIE_SAME_SITE=Lax
CSRF_COOKIE_NAME=journi_csrf
CSRF_HEADER_NAME=X-CSRF-TOKEN
FRONTEND_ALLOWED_ORIGINS=http://localhost:3000
PRACTICE_SUBMISSIONS_ENABLED=false
```

Set `REFRESH_COOKIE_SECURE=true` behind production HTTPS. `SameSite=None` is rejected unless the secure flag is enabled. The origin allowlist is comma-separated and must contain exact frontend origins; credentialed CORS does not support a wildcard origin.

### Authentication Session Flow

- `POST /api/v1/auth/login` returns the existing short-lived access-token JSON and sets an opaque refresh token in a host-only, `HttpOnly` cookie.
- The SPA keeps the access token in memory only. On startup or an eligible API `401`, it obtains CSRF material from `GET /api/v1/auth/csrf` and calls `POST /api/v1/auth/refresh` with credentials.
- Every refresh rotates the cookie. Its `Max-Age` is the remaining absolute session-family lifetime, so rotation never extends the original session deadline.
- Reuse of a rotated token revokes its active family. Logout validates CSRF, remains idempotent for absent or stale refresh cookies, clears the cookie, and returns `204`.
- Browser tabs coordinate refresh with Web Locks where available and broadcast only session-ended events; raw access and refresh tokens are never sent through cross-tab channels.

The refresh-token lookup is protected by a pessimistic write lock. H2 exercises this path only as a best-effort development check; verify concurrent rotation and replay behavior against PostgreSQL before production rollout.

### MVP Lesson Completion Flow

Journi.dev uses explicit learner confirmation for theory-oriented `LESSON` nodes:

1. A `LESSON` with no incomplete prerequisites is returned as `AVAILABLE` and its drawer shows an enabled **Mark as complete** action.
2. Activating the action sends `POST /api/v1/users/me/progress/nodes/{nodeId}/complete` for the authenticated user.
3. The backend validates the node against all of its prerequisites, upserts the user's `(user_id, node_id)` progress as `COMPLETED`, and preserves the first completion timestamp on repeated requests.
4. The frontend reloads the roadmap-node data. Any dependent node whose prerequisites are now all completed becomes `AVAILABLE` immediately; no separate unlock row is required.
5. `LOCKED` nodes never expose an enabled completion action. Already completed lessons show a completed state instead of another active button.

Checklist items remain read-only guidance in the MVP. They help the learner decide when to self-confirm completion but are not stored individually and do not block the completion request. Manual lesson completion does not apply to `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE` nodes.

### GitHub Practice Flow

`PRACTICE` and `PROJECT` nodes use an assessment path instead of self-reported completion:

1. `GET /api/v1/skill-nodes/{nodeId}/challenge` returns the required brief only when the node is unlocked.
2. The learner works locally and submits a public `https://github.com/{owner}/{repository}` URL, branch, and full commit SHA to `POST /api/v1/users/me/challenges/{challengeId}/submissions`.
3. The backend verifies the public revision, stores an idempotent `SUBMITTED` attempt, marks the node `IN_PROGRESS`, and publishes only the submission ID to Redis after commit.
4. The `grader` process atomically claims the attempt, checks out the exact SHA, and launches the challenge-owned command in an ephemeral container with no network, no capabilities, a non-root user, read-only grader assets, and CPU, memory, process, output, and timeout limits.
5. `PASSED` completes the assessment node and unlocks eligible dependents. `NEEDS_CHANGES` accepts a corrected commit as a new attempt. Infrastructure `FAILED` can retry the same attempt number.

Submission history and detail endpoints are learner-owned. Evaluator images, commands, hidden checks, provider credentials, and other learners' attempts are never returned. `PRACTICE_SUBMISSIONS_ENABLED` defaults to `false`; enable it only after local isolation checks pass. `Collections and Generics` is the first challenge-level pilot, while the remaining assessment definitions stay disabled individually.

Seeded assessment starter repositories are curated outside this app repository. The mapping, owner policy, default branch, and readiness notes live in [docs/PRACTICE_STARTER_REPOSITORIES.md](docs/PRACTICE_STARTER_REPOSITORIES.md). Seed validation rejects malformed GitHub repository URLs, duplicate starter repository mappings, and links back to the main `journi.dev` source repository.

Rollback is configuration-first: set `PRACTICE_SUBMISSIONS_ENABLED=false` and stop the `grader` service. Existing attempts and completed progress remain auditable; do not delete submission history or reverse completed assessment progress without an explicit data migration.

### Regression Checks

Use the targeted regression commands before or after feature work:

```bash
cd src/backend && ./mvnw test
cd src/frontend && npm run test:feature-regression
cd src/frontend && npm run verify:roadmap-api
```

`verify:roadmap-api` assumes the local stack is available at `http://localhost:8000/api/v1` unless `JOURNI_API_BASE_URL` is set. The current feature-to-test inventory lives in [docs/REGRESSION_TESTING.md](docs/REGRESSION_TESTING.md).

### Running with Docker Compose

To start the complete local environment (frontend, backend, grader, database, and cache) concurrently, run:

```bash
cd src
docker-compose up --build
```

Access the applications on the following ports:

- **Frontend SPA**: `http://localhost:3000`
- **Backend API**: `http://localhost:8000` (mapped to `8080` internally)
- **PostgreSQL**: `localhost:5432`
- **Redis Cache**: `localhost:6379`
- **Grader worker**: no public port; it alone receives the local Docker socket mount

### Local Development Setup

#### Running the Backend Locally

1. Navigate to the backend directory:
    ```bash
    cd src/backend
    ```
2. Configure your local `.env` variables or system properties.
3. Build the application using Maven:
    ```bash
    ./mvnw clean install
    ```
4. Run the Spring Boot application:
    ```bash
    ./mvnw spring-boot:run
    ```

#### Seeding the Backend Java Spring Boot Roadmap

Use the dedicated script from the repository root to seed the predefined roadmap locally:

```bash
python3 scripts/seed_backend_java_spring_roadmap.py
```

What this does:

- Builds the backend jar locally
- Starts the backend with `JOURNI_SEED_ROADMAPS_ENABLED=true`
- Loads `classpath:seed-data/backend-java-spring-roadmap.json`
- Reads local values from `src/.env` and `src/backend/.env` when available
- Normalizes Docker-only hosts such as `database` to `localhost` for host-machine execution
- Seeds or refreshes the `Backend Java Spring Boot Developer` roadmap
- Exits automatically after the seeding pass finishes

The seeder is idempotent, so you can rerun the same command after editing the dataset or backend seed logic.

If the local database was created before the current challenge schema, recreate the disposable local schema and seed it in one run:

```bash
cd src
docker compose stop backend grader
cd ..
python3 scripts/seed_backend_java_spring_roadmap.py --reset-database
cd src
docker compose up --build
```

`--reset-database` deletes all data in the configured database schema. It is intended only for local development data that can be recreated.

To use a different dataset location:

```bash
python3 scripts/seed_backend_java_spring_roadmap.py --dataset classpath:seed-data/backend-java-spring-roadmap.json
```

To reset only the seeded roadmap while preserving the rest of the schema, either:

- delete the seeded roadmap and the `system_roadmap_seed` user from the local database, then rerun the script, or
- recreate the local database and rerun the script

#### Running the Frontend Locally

1. Navigate to the frontend directory:
    ```bash
    cd src/frontend
    ```
2. Install the package dependencies:
    ```bash
    npm install
    ```
3. Run the Vite development server:
    ```bash
    npm run dev
    ```

## Development Roadmap

- **Phase 1**: Construct learning roadmaps, visual interactive graph interfaces, linked learning materials, learner-confirmed lesson completion, and prerequisite-based unlocking.
- **Phase 2**: Harden and enable deterministic GitHub practice challenges beyond the first pilot.
- **Phase 3**: Roll out cluster-based community boards, leaderboards, heatmap updates, and automated email reminders.
- **Phase 4**: Evaluate AI review, GitHub webhooks, and dynamic roadmap generation as separate post-MVP features.
