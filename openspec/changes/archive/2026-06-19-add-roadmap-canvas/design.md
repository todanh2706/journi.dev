## Context

Roadmap detail currently renders `RoadmapWithNodes.nodes` as a static vertical timeline inside `RoadmapDetailPage`. That makes the MVP flow readable, but it does not give learners the feeling of navigating a structured career path or inspecting nodes in place. The backend already supplies the node fields needed for a first canvas view: identity, title, slug, order, node type, progress status, and locked state.

This change is frontend-only. It must keep the existing `/dashboard/roadmaps/:roadmapId` route, service contract, loading/error/empty states, and MVP progression semantics.

## Goals / Non-Goals

**Goals:**

- Render roadmap detail nodes as an interactive graph/canvas with pan, zoom, minimap, background, controls, and fit view.
- Use a Journi.dev-specific dark premium visual style with gradient/glass node cards, status badges, smooth edges, and a side drawer.
- Keep graph construction deterministic for current data by sorting nodes by `orderIndex`.
- Provide sequential fallback edges from each node to the next until backend prerequisite edge data exists.
- Keep React Flow in read-only learning mode rather than editor mode.
- Keep implementation isolated under `src/frontend/src/features/roadmap-canvas/`.

**Non-Goals:**

- No backend changes, database changes, or API contract changes.
- No dynamic AI-generated roadmap logic.
- No React Flow editor features such as dragging nodes, creating edges, deleting nodes, or saving layout.
- No pixel-perfect cloning of roadmap.sh visuals.
- No complex prerequisite/DAG algorithm beyond current sequential fallback.

## Decisions

- Use `@xyflow/react` for the canvas runtime.
  - Rationale: It provides stable React graph rendering, controls, minimap, background, panels, node types, and edge behavior without building a canvas system from scratch.
  - Alternative considered: Custom HTML/SVG layout. Rejected because zoom/pan/fit/minimap would create more bespoke UI code than the MVP needs.

- Use `@dagrejs/dagre` for top-to-bottom auto-layout.
  - Rationale: Dagre handles 10-50 ordered nodes predictably and can later support prerequisite edges if the backend exposes them.
  - Alternative considered: Manual vertical positioning. Rejected because it would be brittle once branches or prerequisite edges are introduced.

- Keep graph data derived from existing `SkillNode[]`.
  - Rationale: The current MVP contract already includes enough fields to render a learning path and avoids backend scope expansion.
  - Alternative considered: Add prerequisite fields to the API now. Rejected because this task is explicitly frontend-only.

- Create a dedicated feature folder.
  - Rationale: Canvas graph construction, layout, node UI, toolbar, and drawer are cohesive feature concerns and should not bloat `RoadmapDetailPage`.
  - Alternative considered: Inline all code in the page. Rejected because the page would mix data fetching, graph rules, and UI components.

- Make React Flow read-only.
  - Rationale: Learners are viewing progress, not editing roadmap structure. Editing affordances would imply unsupported persistence.
  - Alternative considered: Enable draggable nodes for exploration. Rejected because moved layouts cannot be saved and may confuse the learning flow.

## Risks / Trade-offs

- React Flow CSS may affect default canvas controls globally. Mitigation: Import the package CSS once in `index.css` and keep custom styling scoped to the feature components.
- Sequential fallback edges may not represent real prerequisites. Mitigation: Keep graph building isolated so prerequisite-based edges can replace fallback logic later.
- Large roadmaps could feel tall on small screens. Mitigation: Use fit view, zoom/pan, minimap, and a responsive drawer that does not fully cover mobile.
- New dependencies increase frontend bundle size. Mitigation: Limit usage to the roadmap detail route feature and avoid additional UI libraries.

## Documentation Amendment (2026-06-23)

“Read-only” applies to graph structure: learners cannot drag nodes, edit edges, or persist layout changes. It does not make the learning surface progress-read-only. The drawer for an unlocked `LESSON` is expected to expose **Mark as complete**, call the authenticated progress endpoint, and refresh roadmap state after success. Locked nodes and assessment-oriented node types do not expose that manual action.
