# 0000 Initial Baseline

**Baseline Date:** May 31, 2026  
**Status:** Archived repository baseline  
**Scope:** Pre-change snapshot before future OpenSpec-managed feature work

## 1. Project Goal and Current Context

Journi.dev is a gamified learning platform and social learning network for IT students and entry-level developers. The product vision combines guided learning roadmaps, practical coding challenges, AI-assisted review, milestone-based communities, and motivation systems such as streaks and leaderboards.

At the current repository baseline, the project is structured as a split frontend and backend application:

- A React + TypeScript + Vite frontend in `src/frontend`
- A Spring Boot backend in `src/backend`
- PostgreSQL and Redis in the local runtime topology
- Reference product documents in `doc/`
- OpenSpec baseline specifications in `openspec/specs/`

This baseline already contains meaningful UI flows, working backend APIs for several core domains, and a broader set of persistence models for future capabilities that are not fully connected yet.

## 2. Baseline Features and Components

The following major capabilities are documented in `openspec/specs/`:

- **System Architecture**
  Frontend/backend split, Docker Compose local stack, layered Spring Boot backend, and JWT-aware security wiring.

- **Frontend Experience**
  Public landing page, sign-in page, sign-up page, dashboard experience, shared logo/navigation patterns, and branded not-found page.

- **Authentication and User Management**
  Shared Axios client with bearer-token injection, auth signup and login endpoints, user CRUD API, password hashing, and soft-delete-aware user persistence.

- **Learning Roadmap Domain**
  Learning roadmap creation and retrieval, skill node catalog endpoints, prerequisite graph modeling, and reserved domain models for learning content, challenges, and user node progress.

- **Engagement Tracking**
  Heatmap streak APIs, reminder notification APIs, and persistence support for streak and notification lifecycle data.

- **Assessment, Community, and Integrations Foundation**
  UI concepts for milestones, leaderboard, peer review, and daily engagement, plus backend domain models for:
  community clusters, cluster membership, cluster-node mapping, submissions, code reviews, peer reviews, GitHub repositories, webhook events, AI review tasks, AI model configuration, leaderboard entries, AI assistant conversations, and deployment environments.

## 3. OpenSpec Adoption Baseline

This archive marks the system state before new features are managed through the OpenSpec change workflow.

It should be treated as the initial reference point for the repository:

- The files under `openspec/specs/` describe the current baseline behavior and domain structure.
- Future feature work should be proposed as new OpenSpec changes instead of being folded into this baseline archive.
- This document serves as the initial release note for the repository's documented starting state under OpenSpec.
