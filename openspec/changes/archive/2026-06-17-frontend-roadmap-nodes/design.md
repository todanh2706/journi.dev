## Context

The system currently lists learning roadmaps in the Dashboard, but clicking on a roadmap has no effect. The backend has already been implemented to return a specific Roadmap along with its associated Skill Nodes. We need to build the frontend UI so users can click on a roadmap, navigate to a new route (`/dashboard/roadmaps/:roadmapId`), and see a visual representation of their learning path (skill nodes in order).

## Goals / Non-Goals

**Goals:**
- Implement a `RoadmapDetailPage` that fetches and displays the skill nodes for a selected roadmap.
- Update `RoadmapsPage` to navigate to the new page when a roadmap is clicked.
- Create an API service method in `roadmap.service.ts` to fetch roadmap details and nodes.
- Maintain the current design language: dark mode, glassmorphism, indigo accents, and clean layout.
- Ensure the UI conveys the sequence of the skill nodes clearly.

**Non-Goals:**
- Modifying backend endpoints (they already exist).
- Building complex interactive React Flow graphs (a simple vertical timeline is required for MVP as per AGENTS.md rules).
- Implementing the detailed inside view of a single "Skill Node" content (that is a separate capability).

## Decisions

- **Routing**: Use React Router's dynamic routing (`/dashboard/roadmaps/:roadmapId`) to render `RoadmapDetailPage`. This keeps URLs shareable and maps naturally to the backend REST API `/api/v1/roadmaps/{roadmapId}`.
- **UI Layout**: Use a vertical timeline-style layout to represent the path. This provides a clear top-down progression for the user. We will use Lucide React icons (`CheckCircle`, `Circle`, `Lock`) to indicate node states if applicable.
- **Data Fetching**: The component will fetch the roadmap by ID on mount using the `roadmap.service.ts` and Axios.
- **Types**: We will define `SkillNode` types matching the backend DTOs to ensure type safety.

## Risks / Trade-offs

- [Risk] The API response structure might not exactly match the frontend's expected DTO if the backend is changed or updated. → Mitigation: Define strict TypeScript interfaces `RoadmapWithNodes` and `SkillNode` matching the current backend contract. Implement robust error handling.
