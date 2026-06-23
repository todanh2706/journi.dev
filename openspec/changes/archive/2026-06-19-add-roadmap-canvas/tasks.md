## 1. Dependencies and Styles

- [x] 1.1 Add `@xyflow/react` and `@dagrejs/dagre` to the frontend dependencies.
- [x] 1.2 Import React Flow CSS in `src/frontend/src/index.css` after the Tailwind import.

## 2. Roadmap Canvas Feature Structure

- [x] 2.1 Create `src/frontend/src/features/roadmap-canvas/` with component, utility, type, and barrel export files.
- [x] 2.2 Define typed canvas node data and component prop contracts without broad `any` usage.

## 3. Graph Data and Layout

- [x] 3.1 Implement `buildRoadmapGraph` to sort skill nodes by `orderIndex`, create `roadmapSkillNode` nodes, and create sequential smooth fallback edges.
- [x] 3.2 Implement `layoutRoadmapGraph` using dagre top-to-bottom layout with stable node dimensions and spacing.

## 4. Canvas Components

- [x] 4.1 Implement `RoadmapSkillNode` with progress, lock, node type badge, hidden/small handles, and Journi.dev dark premium styling.
- [x] 4.2 Implement `RoadmapToolbar` with completed/total progress, search input, and fit view control.
- [x] 4.3 Implement `RoadmapNodeDrawer` with node details, close action, and checklist/resource placeholders.
- [x] 4.4 Implement `RoadmapCanvas` with React Flow, controls, minimap, background, panel toolbar, read-only settings, fit view, search highlight, and node click drawer behavior.

## 5. Roadmap Detail Integration

- [x] 5.1 Replace the populated vertical timeline in `RoadmapDetailPage` with `<RoadmapCanvas roadmap={data} />`.
- [x] 5.2 Preserve the existing route, header, loading state, error state, and empty state.

## 6. Verification

- [x] 6.1 Run `npm install` in `src/frontend`.
- [x] 6.2 Run `npm run build` in `src/frontend` and fix any TypeScript or build issues.

## Documentation Amendment (2026-06-23)

The archived “read-only canvas” task refers only to graph editing. The target product behavior now includes an authenticated **Mark as complete** action in the drawer for unlocked `LESSON` nodes; implementation is tracked separately and is not retroactively claimed by these completed tasks.
