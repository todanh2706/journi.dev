# Journi.dev: Interactive Programming Learning and Roadmap Navigation System

Journi.dev is a comprehensive, gamified Learning Management System (LMS) and Social Learning Network designed to guide Information Technology students and entry-level developers along structured programming paths. The platform replaces static learning roadmaps with interactive skill trees, practical hands-on challenges, automated AI-powered code reviews, and community cluster discussion rooms to boost engagement and retention.

## Core Features

- **Interactive Roadmap System**: A directed-acyclic-graph (DAG) visualization representing hierarchical skill nodes. For unlocked theory lessons, learners explicitly mark the lesson as completed after reading it; completing every prerequisite unlocks the dependent node.
- **AI-Powered Code Review**: Practical, submission-based evaluation rather than standard multiple-choice testing. Users push their work to personal GitHub repositories, which are processed via GitHub Webhooks. The AI agent performs static code analysis to evaluate directory structures, clean code practices, security configurations, and design patterns.
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

- **GitHub API and Webhooks**: Subscriptions to user submission events (push / pull request) to ingest repository content.
- **Gemini API**: Powers the automated code reviewer agent, providing context-aware feedback and evaluation results.

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
- **Submission, CodeReview, & AIReviewTask**: Track the lifecycle of pushed code challenges, AI evaluation feedback, and processing tasks.
- **CommunityClusters & ClusterMembership**: Support social network functionalities and discussions.

For a full specification of fields, types, and schema relationships, refer to the [ERD Documentation](docs/ERD.md).

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java Development Kit (JDK) 17 or higher (for local backend development)
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

Checklist items remain read-only guidance in the MVP. They help the learner decide when to self-confirm completion but are not stored individually and do not block the completion request. Manual lesson completion does not apply to `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE` nodes; those node types require their own assessment workflow when implemented.

### Running with Docker Compose

To start the complete local environment (frontend, backend, database, and cache) concurrently, run:

```bash
cd src
docker-compose up --build
```

Access the applications on the following ports:

- **Frontend SPA**: `http://localhost:3000`
- **Backend API**: `http://localhost:8000` (mapped to `8080` internally)
- **PostgreSQL**: `localhost:5432`
- **Redis Cache**: `localhost:6379`

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

To use a different dataset location:

```bash
python3 scripts/seed_backend_java_spring_roadmap.py --dataset classpath:seed-data/backend-java-spring-roadmap.json
```

To fully reset the seeded roadmap in local development, either:

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
- **Phase 2**: Implement the AI reviewer agent pipeline, integrate GitHub webhooks, and finalize challenge submissions.
- **Phase 3**: Roll out cluster-based community boards, leaderboards, heatmap updates, and automated email reminders.
- **Phase 4**: Add context-aware prompt personalization for dynamic roadmap customization.
