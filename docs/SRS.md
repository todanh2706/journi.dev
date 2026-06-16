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
Each skill is represented as a "Node". Users are required to complete prerequisite Nodes before unlocking advanced ones. The initial static data structure is aggregated from standard open-source repositories; subsequently, users can use prompts for the AI to personalize their learning branch (Dynamic Generation).

**Integrated Learning Space:**
Each Node provides a self-contained learning environment. Lesson data is automatically extracted from official documentation and video platforms. An AI Virtual Assistant is integrated directly to answer theoretical questions closely related to the context of the current Node.

### 2.2 AI-Powered Code Review
Competency assessment is based on "Proof of Work" rather than theoretical multiple-choice tests.

**Challenge-based Learning:**
At the end of each Node, the system generates a practical challenge. For example, in a Backend Node, the requirement might be to initialize a complete RESTful API system meeting specific endpoint criteria.

**Submission & Automated Evaluation Process:**
Users push their source code to a personal GitHub repository and provide the link to the system. The AI Agent interacts directly with the GitHub API to pull individual source code files for Static Analysis. The system scores based on directory structure, clean code principles, design patterns, and basic security (e.g., XSS and SQL Injection prevention configurations).

**Feedback & Decision:**
The AI provides detailed feedback in natural language. The returned status is either "Pass" (Unlocks the next Node) or "Request Changes" (Requires refinement of sub-standard code segments).

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
* **AI Agent & Code Review System:**
    * Utilizes **GitHub Webhooks combined with the GitHub API** to automatically listen for submission events (Push/Pull Request) and directly retrieve source code.
    * Integrates the **Gemini API** to act as the Code Reviewer Agent, performing static code analysis, checking clean code and design patterns, and returning automated feedback.
* **DevOps & Deployment Infrastructure:**
    * Uses **Docker** to containerize all Backend and Database services, ensuring consistency between development and production environments.
    * *Frontend Hosting:* Deployed directly on **Vercel** to leverage automated CI/CD and optimize rendering performance.
    * *Backend Hosting:* Deployed on an **AWS EC2** virtual server, providing full Linux system administration control and flexibility to tweak the Docker container runtime environment.

### 3.2 Performance & Security
* **Performance:** The Backend system (Spring Boot on EC2) must have optimally configured Connection Pools with PostgreSQL and appropriate Heap memory management to handle sudden traffic spikes, especially during AI Agent webhook executions and code analysis.
* **Security:** Applies OWASP web security standards. Due to the strict separation between Frontend (Vercel) and Backend (EC2), CORS and CSRF policies must be tightly controlled. Encrypts user tokens (JWT) and absolutely secures sensitive credentials (Gemini API Key, GitHub Tokens) via independent Environment Variables on the server.

---

## 4. ESTIMATED DEVELOPMENT ROADMAP
* **Phase 1:** Build the static Roadmap framework, learning graph UI, and integrate theoretical documentation.
* **Phase 2:** Implement the AI Agent Code Review, connect the GitHub API, and finalize the submission flow (Proof of Work).
* **Phase 3:** Expand community features (Clusters), Leaderboards, Heatmaps, and the Email Reminder system.
* **Phase 4:** Upgrade the context-aware dynamic Roadmap generation AI (Prompt-based) and expand the node library.