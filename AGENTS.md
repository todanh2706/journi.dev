# AGENTS.md

## Project Overview

Journi.dev is a personal portfolio project that helps beginner developers follow structured career roadmaps.

The current MVP goal is:

> User signs up or signs in → chooses a predefined career roadmap → views roadmap skill nodes → opens a skill node → reads learning content → marks the node as completed → unlocks the next node.

Journi.dev should first become:

> A roadmap tracker for developer career paths.

Only after the MVP flow works should it evolve into:

> An AI-powered LMS and social learning network.

## Primary Rule

Always prioritize completing the MVP learning roadmap flow before adding new large features.

Do not expand scope unless explicitly asked.

Do not rewrite architecture, upgrade dependencies, or introduce new frameworks unless the user explicitly requests it.

## Current MVP Scope

The MVP includes:

1. Authentication
    - Sign up
    - Sign in
    - JWT-based protected routes

2. Roadmap Catalog
    - List predefined roadmaps
    - Example roadmap: Backend Java Spring Boot

3. Roadmap Detail
    - Show all skill nodes in order
    - Show locked, available, and completed states

4. Skill Node Detail
    - Title
    - Description
    - Prerequisites
    - Learning resources
    - Checklist
    - Basic challenge placeholder

5. User Progress
    - Mark node as completed
    - Store user progress
    - Unlock next node when prerequisites are completed

## Out of Scope for MVP

Do not implement these unless the user explicitly asks:

- AI code review
- Gemini integration
- GitHub webhook processing
- Peer review
- Community clusters
- Real-time chat
- Advanced leaderboard
- Dynamic AI-generated roadmaps
- Production AWS deployment
- Complex React Flow graph visualization
- Large-scale recommendation engine
- Payment/subscription system
- Mobile app

If a task seems to require one of these features, first look for a simpler MVP-compatible solution.

## Repository Structure

Important directories:

```text
journi.dev/
├── docs/
│   ├── ERD.md
│   ├── ERD.png
│   └── SRS.md
├── openspec/
│   └── specs/
├── src/
│   ├── backend/
│   │   ├── pom.xml
│   │   └── src/main/java/journi/dev/backend/
│   ├── frontend/
│   │   ├── package.json
│   │   └── src/
│   └── docker-compose.yml
└── README.md
```

## Source of Truth

Before making changes, inspect the relevant source-of-truth files instead of guessing.

Dependency versions:

- Frontend dependencies: `src/frontend/package.json`
- Backend dependencies: `src/backend/pom.xml`
- Local infrastructure: `src/docker-compose.yml`

Product/domain direction:

- `README.md`
- `docs/SRS.md`
- `docs/ERD.md`
- `openspec/specs/*/spec.md`

Existing implementation patterns:

- Relevant controller/service/entity/repository/DTO/mapper for backend work
- Relevant page/component/service/type/context for frontend work

## Technology and Dependency Rules

Agents must not upgrade, downgrade, replace, or add major dependencies unless explicitly requested.

Agents must follow the versions already defined in the project files.

### Frontend Stack

Main frontend technologies are defined in `src/frontend/package.json`.

Expected stack:

- React
- React DOM
- React Router DOM
- Vite
- TypeScript
- TailwindCSS
- Axios
- Lucide React

Frontend dependency rules:

1. Use React function components.
2. Use TypeScript for all new frontend code.
3. Use APIs compatible with the installed React Router major version.
4. Use TailwindCSS conventions compatible with the installed TailwindCSS major version.
5. Use the existing Axios setup instead of creating new Axios instances inside components.
6. Do not introduce Redux, Zustand, TanStack Query, React Flow, or another UI framework unless explicitly requested.
7. Do not rewrite the frontend architecture just to solve a local problem.
8. Do not duplicate API base URLs inside components.
9. Do not add dependencies for problems that can be solved with existing project tools.

### Journi.dev UI Direction

Use this as the project design constitution for frontend work:

- Product type: developer learning platform, roadmap tracker, and skill-tree LMS.
- Primary experience: authenticated product UI, not a marketing landing page.
- Visual mood: premium dark learning OS, calm, technical, focused, and credible.
- Base surfaces: off-black backgrounds, subtle panels, thin borders, restrained depth, and soft glass only when it improves readability.
- Accent system: use one primary accent from the indigo, violet, or cyan family. Use green, amber, and red only for semantic states such as completed, available, locked, warning, and error.
- Typography: keep Inter unless the project explicitly adopts a new type system. Use clear hierarchy, compact spacing, and readable body text.
- Shape: use a consistent radius scale. Prefer `rounded-xl` and `rounded-2xl` for panels and `rounded-lg` or `rounded-xl` for controls. Avoid random pill-heavy UI.
- Components: prioritize roadmap nodes, side drawers, toolbars, tabs, command/search bars, badges, checklist rows, progress states, and empty/error/loading states.
- Icons: use `lucide-react`, because it is already installed. Keep stroke width consistent and do not mix icon families without an explicit reason.
- Motion: use subtle hover, focus, drawer, and state transitions. Avoid excessive bounce, cinematic scroll effects, or animation libraries not already present.
- Avoid generic AI UI tells: three-card marketing sections, purple mesh blobs, fake dashboards, decorative status dots, random gradients, and placeholder product screenshots.
- Prefer reusable components and small design tokens over one-off Tailwind class soup when the pattern is repeated.
- Preserve the MVP flow first: login, choose roadmap, view nodes, open node, complete node, unlock next node.
- When updating Taste Skill, preserve the `Journi.dev Project Override` section in `.agents/skills/design-taste-frontend/SKILL.md`.

### Backend Stack

Main backend technologies are defined in `src/backend/pom.xml`.

Expected stack:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- JWT
- Lombok
- MapStruct
- springdoc-openapi

Backend dependency rules:

1. Use Spring Boot APIs compatible with the installed Spring Boot major version.
2. Use `jakarta.*` imports where required by the installed Spring Boot version.
3. Do not use outdated `javax.*` imports unless existing project code requires them.
4. Prefer Spring MVC style for REST controllers unless the existing file is already reactive or the task explicitly requires WebFlux.
5. Do not introduce a new persistence framework.
6. Do not change Java version, Spring Boot version, or major dependency versions unless explicitly requested.
7. Do not expose password hashes or sensitive fields.
8. Do not return entities directly from new API endpoints. Use DTOs.

### Local Infrastructure

The local development environment uses Docker Compose under `src/docker-compose.yml`.

Expected services:

- `frontend`
- `backend`
- `database`
- `cache`

Infrastructure rules:

1. Do not change service names unless explicitly requested.
2. Do not change exposed ports unless explicitly requested.
3. Do not rename environment variables unless all related files are updated.
4. Do not hard-code secrets in source code.
5. Keep local infrastructure simple for MVP development.
6. Prefer editing `.env.example` or documentation when explaining required environment variables.
7. Never commit real secrets.

## Codebase Design Rules

Follow the existing project structure.

Do not create new top-level folders unless explicitly requested.

Do not move files unless the task is specifically about restructuring.

### Backend Structure

Backend source root:

```text
src/backend/src/main/java/journi/dev/backend
```

Expected backend package responsibilities:

```text
controllers/     REST API endpoints only
services/        business logic
repositories/    database access
entities/        JPA entities
dtos/            request/response DTOs
mappers/         MapStruct mappers
configs/         security, CORS, OpenAPI, infrastructure config
exceptions/      custom exceptions and global exception handling
```

Backend dependency direction:

```text
Controller -> Service -> Repository -> Entity
Controller -> DTO
Service -> DTO/Mapper/Entity
Repository -> Entity
```

Forbidden backend patterns:

```text
Controller -> Repository
Controller contains business logic
Controller contains database query logic
Frontend-specific logic inside backend
New endpoint returns Entity directly when a DTO should exist
Password hash returned in API response
```

When adding or changing a backend feature, follow this order:

1. Check existing entity/repository/service/controller/DTO patterns.
2. Add or update entity only if persistence changes are needed.
3. Add or update repository method only if needed.
4. Add or update request/response DTOs.
5. Add or update mapper if mapping is non-trivial or a mapper already exists.
6. Add or update service method.
7. Add or update controller endpoint.
8. Add or update tests if practical.
9. Run the most relevant backend check.

### Frontend Structure

Frontend source root:

```text
src/frontend/src
```

Expected frontend responsibilities:

```text
pages/           route-level screens and route wrappers
features/        domain-owned UI, services, hooks, types, and utilities
components/      cross-feature reusable UI components
services/        app-wide service infrastructure, such as the shared Axios client
utils/           cross-feature pure helper functions
assets/          static assets
```

Frontend dependency direction:

```text
Page -> Feature
Feature -> Shared Component/Hook/Util
Feature Service -> Shared Axios instance
```

Forbidden frontend patterns:

```text
Component hard-codes API base URL
Component creates a new Axios instance
Business rules duplicated across many components
Large page file mixes unrelated UI, API, and business logic
New global state added before local state/context is insufficient
```

When adding or changing a frontend feature, follow this order:

1. Check existing route/page and feature folder patterns.
2. Add or update API request/response types.
3. Add or update service function.
4. Add or update feature component.
5. Connect route if needed.
6. Add loading, error, and empty states.
7. Run the most relevant frontend check.

## File Placement Rules

### Authentication

Backend auth files should stay around:

```text
controllers/*Authentication*
services/*Authentication*
configs/security-related files
dtos/auth-related request and response classes
```

Frontend auth files should stay around:

```text
features/auth/services/auth-related API functions
features/auth/hooks/auth-related hooks
features/auth/components/auth-specific components and icons
pages/sign-in or sign-up pages
protected route components
```

### Roadmap and Skill Node

Backend roadmap files should stay around:

```text
controllers/*Roadmap*
controllers/*SkillNode*
services/*Roadmap*
services/*SkillNode*
entities/*Roadmap*
entities/*SkillNode*
repositories/*Roadmap*
repositories/*SkillNode*
dtos/roadmap-related DTOs
dtos/skill-node-related DTOs
mappers/roadmap-or-skill-node-related mappers
```

Frontend roadmap files should stay around:

```text
pages/roadmap-related pages
features/roadmaps/components or feature-specific UI
features/roadmaps/services/roadmap-related API functions
features/roadmaps/types/roadmap-related types
```

### User Progress

User progress is a first-class MVP domain.

Backend progress files should stay around:

```text
entities/*Progress*
repositories/*Progress*
services/*Progress*
controllers/*Progress*
dtos/progress-related DTOs
mappers/progress-related mappers
```

Frontend progress files should stay around:

```text
features/progress or features/roadmaps progress-related API functions
features/progress or features/roadmaps progress-related types
roadmap/node pages that consume progress state
```

## Version Safety Rules

Before using a framework-specific API, check whether it matches the installed major version.

Examples:

- Do not use React Router patterns from a different major version without checking `package.json`.
- Do not use TailwindCSS configuration patterns from a different major version without checking `package.json`.
- Do not use Spring Boot assumptions from another major version without checking `pom.xml`.
- Do not use `javax.*` imports when `jakarta.*` is required.
- Do not add deprecated APIs when a current project-compatible API exists.
- Do not solve TypeScript errors by weakening types to `any`.

If unsure, inspect the existing code and follow the local pattern instead of inventing a new one.

## API Design Rules

Use REST-style endpoints.

Keep API paths consistent with `/api/v1/...`.

Recommended MVP endpoints:

```text
POST   /api/v1/auth/signup
POST   /api/v1/auth/login

GET    /api/v1/roadmaps
GET    /api/v1/roadmaps/{roadmapId}
GET    /api/v1/roadmaps/{roadmapId}/nodes

GET    /api/v1/skill-nodes/{nodeId}

GET    /api/v1/users/me/progress
POST   /api/v1/users/me/progress/nodes/{nodeId}/complete
```

When adding an endpoint:

1. Add or update request DTO.
2. Add or update response DTO.
3. Add controller method.
4. Add service method.
5. Add repository method only if needed.
6. Add frontend API service method if the endpoint is consumed by the frontend.
7. Update the consuming frontend page/component.
8. Keep frontend and backend contracts consistent.

## API Contract Rule

Whenever a backend API response changes, update the frontend type and service that consume it.

Whenever a frontend service expects a new response shape, verify or update the backend DTO/controller.

Do not leave frontend and backend contracts inconsistent.

Do not silently change public API contracts.

Do not silently rename fields used by the frontend.

Do not silently change environment variable names.

Do not silently change Docker ports.

## Authentication Rules

### Frontend Authentication

Use the existing token flow unless asked to redesign authentication.

Rules:

1. Store and read the access token consistently.
2. Attach JWT using `Authorization: Bearer <token>`.
3. Protected pages should redirect unauthenticated users to sign in.
4. Keep auth API calls inside auth-related service/context code.
5. Do not scatter login/logout/token logic across unrelated components.

### Backend Authentication

Existing auth endpoints should remain under `/api/v1/auth`.

Rules:

1. Login should return a JWT response.
2. Signup should create a user and return a safe user response.
3. Do not return password hashes in API responses.
4. Do not log passwords, JWT secrets, or full tokens.
5. Keep security configuration centralized.

## Roadmap Domain Rules

Core domain entities should support:

- Learning roadmap
- Skill node
- Prerequisite relationship
- Learning content/resource
- User node progress
- Challenge/submission later

For MVP, implement roadmap progression with simple rules:

1. A node is `COMPLETED` if the user has completed it.
2. A node is `AVAILABLE` if all prerequisite nodes are completed.
3. A node is `LOCKED` if at least one prerequisite is incomplete.

Do not implement complex DAG algorithms unless needed.

Start with simple prerequisite checks.

Do not implement dynamic AI-generated roadmaps for MVP.

## Database and Seed Data Rules

For MVP, prefer predefined seed data over dynamic AI-generated content.

Seed at least one roadmap:

```text
Backend Java Spring Boot
```

Suggested node order:

```text
1. Programming fundamentals
2. Java basics
3. OOP in Java
4. Collections and generics
5. SQL basics
6. JDBC basics
7. Spring Core
8. Spring Boot basics
9. REST API development
10. Spring Data JPA
11. Spring Security and JWT
12. Testing basics
13. Docker basics
14. Deployment basics
```

Each seed node should have:

- title
- short description
- level
- estimated time
- prerequisites
- learning resources
- checklist

Seed data should be simple, deterministic, and easy to reset.

## Token-Saving Rules for AI Agents

Before editing, do not read the entire repository.

Read only the minimum files needed for the task.

Use search tools such as `rg` before opening many files.

Recommended context loading order:

1. For product direction:
    - `README.md`
    - `docs/SRS.md`
    - `docs/ERD.md`
    - relevant `openspec/specs/*/spec.md`

2. For frontend tasks:
    - `src/frontend/package.json`
    - relevant route file
    - relevant page/component file
    - relevant service file
    - relevant type/context file

3. For backend tasks:
    - `src/backend/pom.xml`
    - relevant controller
    - relevant service
    - relevant entity
    - relevant repository
    - relevant DTO
    - relevant mapper

4. For full-stack API connection tasks:
    - backend controller
    - backend DTOs
    - backend service method
    - frontend API service
    - frontend type definition
    - frontend page/component using the API

Never rewrite a whole file if a small patch is enough.

Never refactor unrelated code while fixing a specific issue.

Never perform formatting-only rewrites unless explicitly requested.

## Change Discipline

When editing code:

1. Prefer small patches.
2. Preserve existing folder structure.
3. Preserve existing naming conventions.
4. Preserve existing API path style.
5. Preserve existing dependency versions.
6. Preserve existing visual style unless the task is about redesign.
7. Do not move files unless the task is specifically about restructuring.
8. Do not add new abstractions until there are at least two real use cases.
9. Do not implement out-of-scope features while fixing MVP issues.
10. Do not make frontend and backend contract changes independently.
11. Do not hide errors with broad `catch` blocks or silent fallbacks.
12. Do not leave dead code, unused imports, or debug logs.

## Coding Style

General:

- Prefer clear names over clever abstractions.
- Prefer simple code over premature architecture.
- Keep functions short when possible.
- Avoid duplicate business logic.
- Do not leave dead code.
- Do not leave console logs unless useful for development and requested.
- Do not introduce large formatting-only diffs.

Frontend:

- Use `.tsx` for React components.
- Use `.ts` for services, types, hooks, and utilities.
- Prefer typed props and typed API responses.
- Avoid `any` unless unavoidable.
- Keep API response types close to API service files or in a shared `types` folder.
- Use Tailwind classes consistently.
- Keep pages responsible for route-level layout and data fetching.
- Keep reusable UI pieces inside components.
- Use local state/context first unless a real need for global state appears.

Backend:

- Use constructor injection where possible.
- Keep controller methods thin.
- Put business logic in services.
- Use repositories only from the service layer.
- Use DTOs for request and response bodies.
- Validate request DTOs with annotations where appropriate.
- Keep DTO names explicit, for example `CreateRoadmapRequest`, `RoadmapResponse`, `SkillNodeResponse`.
- Keep package naming consistent under `journi.dev.backend`.
- Use exceptions intentionally and map them to appropriate HTTP status codes.

## Security Rules

Never commit real secrets.

Never hard-code:

- JWT secret
- database password
- Gemini API key
- GitHub token
- production URL secrets

Use environment variables and `.env.example`.

Do not log sensitive data.

Do not return password hashes, JWT secrets, full tokens, or internal stack traces to the frontend.

When adding configuration, document required variables without exposing real values.

## Development Commands

### Run full local environment

```bash
cd src
docker compose up --build
```

If the local machine only supports the legacy command, use:

```bash
cd src
docker-compose up --build
```

### Frontend

```bash
cd src/frontend
npm install
npm run dev
npm run build
npm run lint
```

### Backend

```bash
cd src/backend
./mvnw spring-boot:run
./mvnw test
./mvnw clean install
```

If Maven Wrapper is unavailable, use the local Maven installation:

```bash
cd src/backend
mvn spring-boot:run
mvn test
mvn clean install
```

## Testing Rules

Run the smallest relevant check after changes.

For frontend:

- Run `npm run build` after TypeScript, route, or API contract changes.
- Run `npm run lint` after component, service, or hook changes when possible.

For backend:

- Run `./mvnw test` after service, controller, entity, repository, DTO, mapper, or security changes.
- Run `./mvnw clean install` after dependency or project-wide changes.

For Docker-related changes:

```bash
cd src
docker compose up --build
```

If checks cannot be run, explain why.

Do not claim tests passed unless they were actually run.

## Implementation Workflow

When given a task:

1. Restate the goal in one short sentence.
2. Identify the smallest set of files needed.
3. Inspect only those files.
4. Make a small plan.
5. Apply the smallest safe patch.
6. Run the most relevant check.
7. Summarize what changed.
8. Mention any follow-up task clearly.

Do not make broad unrelated improvements.

Do not silently change public API contracts.

Do not silently change environment variable names.

Do not silently change Docker ports.

## Priority Order

When multiple improvements are possible, follow this priority:

1. Make the MVP user flow work.
2. Fix broken authentication or routing.
3. Connect frontend to real backend APIs.
4. Add seed roadmap data.
5. Add user progress tracking.
6. Improve UI clarity.
7. Add tests.
8. Improve documentation.
9. Add non-MVP features later.

## Response Format for AI Agents

When finishing a task, respond with:

```text
Summary:
- ...

Changed files:
- ...

Checks:
- ...

Notes:
- ...
```

If no checks were run, say:

```text
Checks:
- Not run. Reason: ...
```

## Final Reminder

Stay focused on this MVP flow:

> Login → choose roadmap → view nodes → open node → complete node → unlock next node.

This is more important than adding impressive but unfinished features.
