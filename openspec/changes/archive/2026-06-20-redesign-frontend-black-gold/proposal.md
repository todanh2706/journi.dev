## Why

Journi.dev's current frontend looks polished in isolated sections, but its purple/indigo gradient language, decorative glows, fabricated social proof, and static non-MVP dashboard modules make the product feel like a generic concept demo rather than a credible roadmap tracker. The redesign should establish a restrained black-and-gold learning interface and make the core journey—sign in, choose a roadmap, inspect nodes, and progress through the path—the clearest experience on every screen.

## What Changes

- Introduce a shared black-and-gold visual system with off-black surfaces, warm neutral text, gold as the single primary accent, semantic-only green/amber/red states, consistent radii, borders, focus rings, spacing, and restrained motion.
- Replace scattered purple, violet, indigo, cyan, hard-coded gradients, oversized glows, and one-off surface colors across public, authentication, dashboard, roadmap, profile, and not-found screens.
- Reframe public and authentication copy around the roadmap-tracking MVP and remove unverified social proof, fake product activity, placeholder links, and promotion of code review/community features that are not part of the current MVP.
- Simplify authenticated navigation around implemented MVP destinations and prevent visible navigation controls from leading to undefined dashboard routes.
- Replace the dashboard's random heatmap, fabricated leaderboard, and out-of-scope quick actions with an honest roadmap-first overview built from available auth/roadmap state, with useful loading, empty, and error states.
- Improve roadmap catalog cards and roadmap canvas hierarchy so titles, progress, node availability, completion, locking, search, graph controls, and the node-detail drawer are easier to scan and operate.
- Improve responsive behavior for the sidebar, page gutters, dashboard header/actions, roadmap canvas controls, and node drawer so the MVP remains usable on mobile and narrow viewports.
- Standardize accessible interaction states: keyboard-visible focus, meaningful labels, proper buttons/links, adequate contrast, reduced-motion support, and non-color cues for learning states.
- Preserve existing frontend architecture, API contracts, dependency versions, auth flow, route paths, and roadmap graph library; this proposal does not add backend, database, or dependency changes.

## Capabilities

### New Capabilities

- `frontend-design-system`: Defines the shared black-and-gold tokens, component styling rules, responsive behavior, accessibility states, semantic colors, and motion constraints used across the frontend.

### Modified Capabilities

- `frontend-experience`: Changes the public, authentication, dashboard, navigation, loading, empty, error, and not-found experiences from a presentation-first demo to an honest, roadmap-first MVP interface.
- `roadmap-view`: Changes the visual and interaction requirements for roadmap catalog cards, graph nodes, progress hierarchy, toolbar controls, and the responsive node-detail drawer.

## Impact

- Frontend-only changes under `src/frontend/src`, primarily global styles, shared logo/navigation primitives, public/auth pages, dashboard components, roadmap pages/canvas components, profile panels, and the not-found screen.
- Existing `GET /api/v1/roadmaps`, roadmap-detail, signup, login, and logout contracts remain unchanged; unavailable progress APIs must not be simulated with fabricated data.
- No new runtime dependency, framework, backend endpoint, persistence migration, port, or environment-variable change is required.
- Frontend OpenSpec requirements will be updated to remove demo-only expectations that conflict with the MVP priority and add a reusable visual-system contract.
