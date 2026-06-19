## Context

The previous frontend mixed route-level screens, domain services, reusable UI, and feature-specific components across several folders. Examples included auth services and icon components inside `pages/Auth`, dashboard widgets inside `pages/Feed/components`, global roadmap services/types at the top level, and the roadmap canvas under `features/roadmap-canvas`.

This change is a structural refactor only. It must preserve all routes, API calls, styling behavior, and MVP roadmap flow while making future frontend work easier to place.

## Goals / Non-Goals

**Goals:**

- Establish a predictable frontend structure based on route pages, domain features, and shared primitives.
- Move domain-owned code into `features/<domain>/` folders without changing runtime behavior.
- Keep route-level files in `pages/` focused on routing, layout, and page composition.
- Keep cross-feature utilities and UI in shared top-level folders only when they are genuinely reused.
- Use small import updates and barrel exports to make migration understandable and reviewable.
- Maintain TypeScript and Vite build health after each migration group.

**Non-Goals:**

- No backend changes.
- No route path changes.
- No API contract, environment variable, Docker port, or dependency changes.
- No UI redesign or product behavior changes.
- No migration to Redux, Zustand, TanStack Query, a UI framework, or another routing framework.
- No broad cleanup of unrelated code quality issues beyond what is needed to move files safely.

## Decisions

- Use `features/<domain>/` as the owner for domain-specific frontend code.
  - Rationale: Auth, dashboard, and roadmaps already behave like domains and need nearby components, services, hooks, and types.
  - Alternative considered: Keep everything under `pages/`. Rejected because page folders become dumping grounds for reusable and domain logic.

- Keep `pages/` as the route boundary.
  - Rationale: React Router route files should remain easy to find, and pages can compose feature components without owning all implementation details.
  - Alternative considered: Move route files into feature folders. Rejected for this repo because routes are already centralized around `pages/` and a smaller migration is safer.

- Keep shared top-level folders narrow.
  - Rationale: `components/`, `services/`, `utils/`, and `assets/` are useful when code is reused across domains or supports the whole app, but they should not become catch-all locations for domain code.
  - Alternative considered: Move all shared folders under a new `shared/` directory. Rejected for now because it creates extra churn and conflicts with the existing project conventions.

- Migrate incrementally by domain.
  - Rationale: Auth, dashboard/feed, and roadmaps can be moved in separate reviewable steps while preserving import compatibility.
  - Alternative considered: One large rename-only sweep. Rejected because it increases merge conflict risk and makes regressions harder to isolate.

## Risks / Trade-offs

- [Risk] File moves can create noisy diffs and break imports. → Mitigation: Move one domain at a time, update imports immediately, and run `npm run build`.
- [Risk] Barrel exports can hide ownership if overused. → Mitigation: Use barrels at feature boundaries only, not for every internal folder.
- [Risk] Route behavior can accidentally change during moves. → Mitigation: Keep `pages/` route files in place and treat moved feature code as implementation details.
- [Risk] Structural refactor can drift into redesign. → Mitigation: Do not change UI text, layout, API behavior, or state flow unless required for imports.
