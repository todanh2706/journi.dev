## Why

The current roadmap detail screen presents skill nodes as a vertical list, which works for MVP data but does not feel like an interactive learning path. Journi.dev needs a richer roadmap view that helps learners understand sequence, progress, locked states, and node detail without expanding backend scope.

## What Changes

- Replace the roadmap detail vertical timeline with a React Flow powered graph/canvas.
- Add a dedicated `roadmap-canvas` frontend feature folder for graph construction, auto-layout, custom nodes, toolbar, and node drawer.
- Use existing `RoadmapWithNodes` and `SkillNode` data from the frontend service without backend changes.
- Add sequential fallback edges based on `orderIndex` until backend prerequisite edges are available.
- Add React Flow and dagre dependencies for rendering, zoom/pan controls, minimap, background, and stable top-to-bottom layout.
- Preserve loading, error, and empty states on the roadmap detail page.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `roadmap-view`: Roadmap detail shall render skill nodes as an interactive learning canvas with progress-aware nodes, smooth edges, search/highlight, fit view, and node detail drawer.

## Impact

- Frontend code under `src/frontend/src/pages/Roadmaps/RoadmapDetailPage.tsx`.
- New frontend feature files under `src/frontend/src/features/roadmap-canvas/`.
- Frontend dependencies: `@xyflow/react` and `@dagrejs/dagre`.
- Frontend global CSS import for React Flow styles in `src/frontend/src/index.css`.
- No backend API, database, authentication, route, Docker, or environment variable changes.
