# PROJECT REQUIREMENTS DOCUMENT (PRD) (v1)
**Project Name:** Journi.dev - Interactive Programming Learning & Roadmap Navigation System
**Author:** To Huu Danh
**Date:** April 02, 2026
**Status:** Draft

---

## 1. PROJECT OVERVIEW

### 1.1 Project Objectives
Journi.dev is an Interactive Learning Management System (LMS) combined with a Social Learning Network. The project aims to solve the lack of direction commonly faced by IT students and entry-level developers. The system provides visual, personalized learning roadmaps, integrated with automated capability assessments via an AI Agent, and drives motivation through gamification.

### 1.2 Target Audience
* Information Technology students needing practical learning paths.
* Developers looking to switch programming languages/frameworks or advance their specialized skills.

---

## 2. DETAILED FEATURE SPECIFICATIONS

### 2.1 Interactive Roadmap System
The system utilizes a directed graph representation to illustrate the hierarchical structure of skills.

**Skill Tree Progression:**
Each skill is represented as a "Node". A Node is `LOCKED` while any prerequisite remains incomplete and becomes `AVAILABLE` when all prerequisites are `COMPLETED`. For theory-oriented `LESSON` Nodes, the learner explicitly confirms completion with a **Mark as complete** action after reading the material. The backend stores completion per user and recalculates dependent Node states; the frontend then refreshes the roadmap so newly available Nodes are visible immediately. The initial MVP uses predefined roadmap data rather than AI-generated learning branches.

**Integrated Learning Space:**
Each unlocked Node provides a self-contained learning environment with a summary, estimated effort, checklist, notes, and external learning resources. Checklist items are read-only guidance in the MVP and are not individually persisted. An unlocked `LESSON` displays the manual completion action; a locked Node displays prerequisite guidance without learning details or completion controls. Completed lessons retain their completed status and completion timestamp.

**Manual Lesson Completion Rules:**
* Only authenticated users can update their own progress.
* Only `AVAILABLE` or `IN_PROGRESS` Nodes of type `LESSON` can be manually completed.
* Repeating the completion request is idempotent and does not create duplicate progress records or replace the original completion time.
* Completing a locked Node is rejected.
* `PRACTICE` and `PROJECT` Nodes require a passing deterministic GitHub submission. `QUIZ` and `CHALLENGE` node types remain unsupported by this workflow.

### 2.2 Deterministic GitHub Practice
Competency assessment is based on "Proof of Work" rather than theoretical multiple-choice tests.

**Challenge-based Learning:**
Each seeded `PRACTICE` or `PROJECT` Node has one required challenge with instructions, acceptance criteria, hints, expected artifacts, a public starter repository, a passing score, a timeout, and server-owned grader configuration. Locked Nodes do not expose the brief or submission controls.

**Submission & Automated Evaluation Process:**
Users push source code to a public GitHub repository and submit its HTTPS URL, branch, and full commit SHA. The API verifies the immutable revision, stores learner-owned attempt history, and sends only the submission ID through Redis Streams. A separate non-web grader checks out the exact SHA and runs challenge-owned deterministic checks inside an isolated, network-disabled, resource-limited container.

**Feedback & Decision:**
The lifecycle is `SUBMITTED`, `EVALUATING`, then `PASSED`, `NEEDS_CHANGES`, or infrastructure `FAILED`. Only a trusted `PASSED` result completes the submitting learner's assessment Node. A corrected SHA creates a new attempt; infrastructure failure retries the same attempt. AI feedback and AI pass/fail decisions are explicitly outside this implementation.

### 2.3 Milestone Communities
Avoids fragmenting the community by individual Nodes, which leads to disjointed interactions.

**Cluster-based Discussion Spaces:**
The community is divided by major chapters or milestones (e.g., Database Cluster, DevOps Cluster). Users within the same cluster share a common discussion room to address similar challenges.

**Peer-Review:**
An optional feature allowing advanced users (who have passed a Node) to review and comment on the source code of those behind them, complementing the AI Agent and enhancing professional exchange.

### 2.4 Gamification & Retention
Integrates gamification mechanisms to maintain user engagement.

**Tracking & Reminder System:**
A Heatmap chart (similar to GitHub Contributions) tracks consecutive active days (Streaks). The system automatically sends reminder emails if a user pauses their learning beyond a set threshold.

**Leaderboards:**
Dynamically ranks users by niche (Backend, Frontend, Security). Scores are calculated based on the number of Nodes passed, code quality (fewest AI revision requests), and frequency of Peer-Review contributions.

---

## 3. NON-FUNCTIONAL REQUIREMENTS & ARCHITECTURE

### 3.1 Proposed Tech Stack
* **Frontend:** Developed using **React.js with TypeScript** to ensure type safety for complex graph data structures. Utilizes **Tailwind CSS** to optimize UI/UX development time. Integrates the **React Flow** core library to handle rendering and smooth drag-and-drop, zoom-in/out interactions on the Skill Tree graph.
* **Backend:** Leverages **Java Spring Boot** as the core framework. This architecture ensures Enterprise-standard robustness, high load capacity, and strict Object-Oriented Programming (OOP) structure to handle complex business logic flows.
* **Database:** * *Primary DB:* Utilizes **PostgreSQL** to store complex cross-entity relationships (User - Node - Community) and leverages its flexible JSONB storage capabilities for Roadmap data.
    * *In-memory DB:* Uses **Redis** for caching static data (reducing DB queries) and high-speed processing for real-time features like Leaderboards and Streak Tracking.
* **Practice Evaluation System:**
    * Uses the **GitHub API** only to verify public repositories, branches, and exact commit SHAs. Private repositories, OAuth, and webhooks are not supported.
    * Uses **Redis Streams** between the public API and a separate grader process. The grader alone may launch restricted evaluation containers.
* **DevOps & Deployment Infrastructure:**
    * Uses **Docker** to containerize all Backend and Database services, ensuring consistency between development and production environments.
    * *Frontend Hosting:* Deployed directly on **Vercel** to leverage automated CI/CD and optimize rendering performance.
    * *Backend Hosting:* Deployed on an **AWS EC2** virtual server, providing full Linux system administration control and flexibility to tweak the Docker container runtime environment.

### 3.2 Performance & Security
* **Performance:** The API remains separate from untrusted evaluation work. Grader concurrency, leases, repository size, output, and runtime limits are configurable and conservative by default.
* **Security:** Applies OWASP web security standards. Due to the strict separation between Frontend (Vercel) and Backend (EC2), CORS and CSRF policies must be tightly controlled. Encrypts user tokens (JWT) and absolutely secures sensitive credentials (Gemini API Key, GitHub Tokens) via independent Environment Variables on the server.

---

## 4. ESTIMATED DEVELOPMENT ROADMAP
* **Phase 1:** Build the predefined Roadmap framework, learning graph UI, theoretical learning content, manual lesson completion, and prerequisite-based unlocking.
* **Phase 2:** Harden and roll out deterministic GitHub practice beyond the first enabled pilot.
* **Phase 3:** Expand community features (Clusters), Leaderboards, Heatmaps, and the Email Reminder system.
* **Phase 4:** Upgrade the context-aware dynamic Roadmap generation AI (Prompt-based) and expand the node library.
