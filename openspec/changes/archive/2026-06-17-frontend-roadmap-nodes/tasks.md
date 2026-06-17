## 1. Types and API Services

- [x] 1.1 Add `SkillNode` and `RoadmapWithNodes` (or extend existing) interfaces to `src/frontend/src/types/roadmap.ts`.
- [x] 1.2 Implement `getRoadmapById(id: string)` in `src/frontend/src/services/roadmap.service.ts` to call the backend endpoint (`GET /api/v1/roadmaps/{roadmapId}` or similar).

## 2. UI Components

- [x] 2.1 Create `src/frontend/src/pages/Roadmaps/RoadmapDetailPage.tsx` with base states (loading, error, empty).
- [x] 2.2 Implement the vertical timeline layout in `RoadmapDetailPage` to map over the fetched skill nodes.
- [x] 2.3 Style the skill node items with glassmorphism, Lucide icons (e.g., `Circle`, `CheckCircle`), and indigo accents to match existing design language.

## 3. Routing Integration

- [x] 3.1 Update `src/frontend/src/App.tsx` to add the new route: `<Route path="roadmaps/:roadmapId" element={<RoadmapDetailPage />} />`.
- [x] 3.2 Update `src/frontend/src/pages/Roadmaps/RoadmapsPage.tsx` to add `onClick` navigation (via `useNavigate`) or wrap the card in a `<Link>` component to navigate to the roadmap details.

## 4. Verification

- [x] 4.1 Run `npm run lint` and `npm run build` in `src/frontend` to ensure type safety and build validity.
- [x] 4.2 Validate that the local development environment works (frontend connects to backend and renders nodes successfully).
