## 1. Frontend Layout Refactoring

- [x] 1.1 Create `DashboardLayout.tsx` by extracting the sidebar and layout wrappers from `Dashboard.tsx`.
- [x] 1.2 Rename `Dashboard.tsx` to `DashboardOverview.tsx` and remove the sidebar components from it, keeping only the main content area.
- [x] 1.3 Update `App.tsx` routing to use `DashboardLayout` for `/dashboard` and `DashboardOverview` for the index route.

## 2. Backend API Implementation

- [x] 2.1 Update `LearningRoadmapMapper.java` to support entity mapping logic.
- [x] 2.2 Add `getAllRoadmaps` method in `LearningRoadmapService.java` to retrieve all roadmaps.
- [x] 2.3 Implement `GET /api/v1/roadmaps` endpoint in `LearningRoadmapController.java`.

## 3. Roadmap API Integration

- [x] 3.1 Define `Roadmap` and `RoadmapResponse` types in `src/frontend/src/types/roadmap.ts` (or equivalent location).
- [x] 3.2 Create `src/frontend/src/services/roadmap.service.ts` with a `getRoadmaps` function using the existing Axios instance to fetch from `/api/v1/roadmaps`.

## 4. Roadmap UI Implementation

- [x] 4.1 Create `RoadmapsPage.tsx` under `src/frontend/src/pages/Feed/` or `src/frontend/src/pages/Roadmap/`.
- [x] 4.2 Add the new route `/dashboard/roadmaps` mapping to `RoadmapsPage` in `App.tsx`.
- [x] 4.3 Implement data fetching inside `RoadmapsPage` using `useEffect` and `roadmap.service.ts`, handling loading and error states.
- [x] 4.4 Design and implement the UI in `RoadmapsPage` to show roadmaps in a grid format, using existing dark-theme styles (glassmorphism, indigo accents).

## 5. Verification

- [x] 5.1 Verify `/dashboard` route still loads the overview correctly with the sidebar intact.
- [x] 5.2 Verify clicking "Roadmap" in the sidebar navigates to `/dashboard/roadmaps` and highlights the tab.
- [x] 5.3 Verify the API call is made and roadmaps are displayed beautifully.
- [x] 5.4 Run `npm run build` to ensure no TypeScript or build errors were introduced.
