## Why

Users need a way to view and select predefined learning roadmaps from the frontend application, following our MVP flow. Currently, backend data exists (seeders) for roadmaps, but the frontend lacks a view to display them. Integrating this feature into the dashboard ensures users have a seamless, structured entry point to track their learning journey.

## What Changes

- **Extract Dashboard Layout**: Separate the current `Dashboard.tsx` into a reusable `DashboardLayout` component (containing the Sidebar) and an `Outlet` for nested routes. The existing overview will become the index route for the dashboard.
- **Implement `/dashboard/roadmaps` Route**: Add a new React Router route nested under the dashboard layout.
- **Create Roadmap List Page**: Implement a new frontend page displaying a list of available roadmaps fetching data from the backend.
- **Enhance UI/UX**: Design the roadmap view using the existing dark mode styling (indigo/purple gradients, glassmorphism, responsive cards).
- **Backend API Update**: Implement `getAllRoadmaps` in `LearningRoadmapService` and expose `GET /api/v1/roadmaps` endpoint in `LearningRoadmapController` to serve the roadmap list.

## Capabilities

### New Capabilities
- `roadmap-view`: Display the list of available roadmaps within the dashboard interface, interacting with existing backend REST APIs.

### Modified Capabilities

## Impact

- **Frontend Routing**: Changes to `App.tsx` to accommodate nested dashboard routing.
- **Frontend Components**: Modifies `Dashboard.tsx` and extracts sidebar logic. Introduces new components for the roadmap list and roadmap cards.
- **Frontend Services**: Adds a new frontend API service to fetch roadmaps from the existing backend endpoints (`/api/v1/roadmaps`).
