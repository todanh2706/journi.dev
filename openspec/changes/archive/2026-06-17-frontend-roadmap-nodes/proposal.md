## Why

Currently, users can view available learning roadmaps in the dashboard, but clicking on them does not show the actual learning path. Since the backend now supports fetching the skill nodes for a given roadmap, we need to implement the frontend UI to display these nodes when a user selects a roadmap. This is a critical piece of the core MVP flow (Login → choose roadmap → view nodes).

## What Changes

- Add a new frontend route `/dashboard/roadmaps/:roadmapId` to display the details of a specific roadmap.
- Update the existing roadmap cards in `RoadmapsPage` to link to the new route.
- Implement API service methods in `roadmap.service.ts` to fetch a specific roadmap and its skill nodes.
- Create a new `RoadmapDetailPage` component to organize and visualize the skill nodes in an intuitive, vertically progressing path.
- Add necessary TypeScript types for skill nodes.
- Ensure the new UI adheres to the existing visual design (dark theme, glassmorphism, indigo accents, animations) and leverages Lucide React icons.

## Capabilities

### New Capabilities
- `roadmap-nodes-ui`: The user interface for viewing the detailed path and skill nodes of a specific learning roadmap.

### Modified Capabilities

## Impact

- **Frontend Routing**: Modifies `src/frontend/src/App.tsx` to include the new dynamic route.
- **Frontend Services**: Modifies `roadmap.service.ts` to connect to the backend roadmap node endpoints.
- **Frontend Pages**: Adds `src/frontend/src/pages/Roadmaps/RoadmapDetailPage.tsx` and related components.
- **Frontend Types**: Extends `roadmap.ts` or creates `skillNode.ts` in the types folder.
