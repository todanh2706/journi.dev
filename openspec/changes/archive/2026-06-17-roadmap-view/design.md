## Context

The current `Dashboard.tsx` file monolithically includes the sidebar navigation and the dashboard overview content. To support navigating to a new `/dashboard/roadmaps` page while preserving the sidebar and header structure, we need to extract a `DashboardLayout` component. The user has seeded roadmap data in the backend, and we now need to fetch and render that data cleanly on the frontend. The design should conform to the existing gamified dark theme style.

## Goals / Non-Goals

**Goals:**
- Refactor the frontend routing to support nested routes under `/dashboard`.
- Extract `DashboardLayout` without altering the visual appearance of the existing dashboard.
- Introduce `DashboardOverview` (the current dashboard content) and `RoadmapsPage`.
- Implement `RoadmapService` to fetch roadmaps from `/api/v1/roadmaps`.
- Design a grid of roadmap cards mimicking the existing dark theme (glassmorphism, indigo/purple accents).

**Non-Goals:**
- Modifying backend APIs or models.
- Implementing the detailed skill node view (just the high-level roadmap list for now).
- Changing any other application routes or features.

## Decisions

- **Routing Strategy**: Use React Router's `<Outlet />` within a new `DashboardLayout` component. The `/dashboard` route maps to `DashboardLayout`. Its index route maps to `DashboardOverview`, and `/dashboard/roadmaps` maps to `RoadmapsPage`. This is the standard idiomatic React Router approach and scales well for future dashboard tabs.

### Component Changes
- `App.tsx`: Incorporates nested routing for the dashboard.
- `Dashboard.tsx` (Current): Split into `DashboardLayout.tsx` (sidebar + layout wrapping) and `DashboardOverview.tsx` (dashboard content).
- `RoadmapsPage.tsx`: New page displaying the list of roadmaps.
- `NavItem.tsx`: Update to support `react-router-dom` `Link` usage seamlessly and evaluate `active` state using `useLocation`.

### Backend Changes
- `LearningRoadmapController`: Add `GET /api/v1/roadmaps` returning a list of roadmaps.
- `LearningRoadmapService`: Add `getAllRoadmaps` to retrieve all roadmaps via `LearningRoadmapRepository` and map to `LearningRoadmapResponse` via `LearningRoadmapMapper`.

## Implementation Order

- **Component Structure**:
  - `src/frontend/src/pages/Feed/DashboardLayout.tsx` will house the Sidebar and the `<main>` container with the header and an `<Outlet />`.
  - `src/frontend/src/pages/Feed/DashboardOverview.tsx` will house the current heatmap, milestones, and leaderboard.
  - `src/frontend/src/pages/Roadmaps/RoadmapsPage.tsx` will house the new roadmap list view.
- **Data Fetching Strategy**: We will create `src/frontend/src/services/roadmap.service.ts` using the existing configured `axios` instance to call `GET /api/v1/roadmaps`. The component will manage its own loading and error states using standard React hooks (`useState`, `useEffect`) without introducing external caching libraries, staying within the MVP scope constraints.

## Risks / Trade-offs

- **Risk: Breaking existing Dashboard styles** → Mitigation: Carefully extract the `<aside>` and `<main>` wrappers into the Layout component. Ensure CSS grid/flex properties remain intact on the wrapper elements.
- **Risk: Nested routing transition issues** → Mitigation: Ensure `AnimatedRoutes` and `SlideUpTransition` in `App.tsx` handle the nested layout smoothly. The `key={location.pathname}` might cause the entire layout (including the sidebar) to unmount/remount on tab switch. If this feels janky, we may need to adjust the `key` to only trigger on top-level route changes, or let the `Outlet` transition internally. For now, we will retain existing behavior.
