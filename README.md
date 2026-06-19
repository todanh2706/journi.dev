# Journi.dev: Interactive Programming Learning and Roadmap Navigation System

Journi.dev is a comprehensive, gamified Learning Management System (LMS) and Social Learning Network designed to guide Information Technology students and entry-level developers along structured programming paths. The platform replaces static learning roadmaps with interactive skill trees, practical hands-on challenges, automated AI-powered code reviews, and community cluster discussion rooms to boost engagement and retention.

## Core Features

- **Interactive Roadmap System**: A directed-acyclic-graph (DAG) visualization representing hierarchical skill nodes. Users must complete prerequisite nodes to unlock advanced technologies, with options for dynamic, prompt-based AI roadmap personalization.
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

The database relies on 17 interconnected tables designed to represent user metrics, progress states, submissions, community clusters, and AI review configurations.

Key entities include:

- **User**: General profile and role settings.
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

Create a `.env` file inside the `src/` directory containing the following variables:

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

# Integrations
JWT_SECRET_KEY=your_secure_jwt_secret
GEMINI_API_KEY=your_gemini_api_key
```

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

- **Phase 1**: Construct learning roadmaps, construct visual interactive graph interfaces, and link reference materials.
- **Phase 2**: Implement the AI reviewer agent pipeline, integrate GitHub webhooks, and finalize challenge submissions.
- **Phase 3**: Roll out cluster-based community boards, leaderboards, heatmap updates, and automated email reminders.
- **Phase 4**: Add context-aware prompt personalization for dynamic roadmap customization.
