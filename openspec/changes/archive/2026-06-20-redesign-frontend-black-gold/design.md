## Context

The frontend currently expresses its visual identity through page-local Tailwind values: `#0d0e1a`/`#141527` surfaces, purple-to-indigo CTAs, violet/cyan graph treatments, multicolor node types, radial glow decorations, and many slightly different border and focus styles. That makes the theme expensive to change, produces inconsistent hierarchy, and encourages repeated class strings across auth, dashboard, roadmap, profile, and error states.

The more important UX problem is structural. The authenticated dashboard foregrounds a random heatmap, fabricated leaderboard, fake ranks, unimplemented quick actions, and navigation to routes that do not exist. The public and auth screens repeat unverified community counts and promote peer review/code review even though the current MVP is a roadmap tracker. These choices obscure the only complete product story: authenticate, choose a predefined roadmap, inspect its nodes, and learn in sequence.

This is a frontend-only redesign using the installed React 19, React Router 7, Tailwind CSS 4, Axios, Lucide React, and React Flow stack. Existing API contracts, route paths, auth token behavior, domain types, and backend behavior remain constraints. The implementation must be incremental and must not rely on progress data or content that the backend does not expose.

## Goals / Non-Goals

**Goals:**

- Establish a coherent black-and-gold visual system with restrained depth, consistent component states, readable contrast, and semantic learning-state colors.
- Make every visible route support or clearly lead toward the MVP roadmap flow.
- Remove UI slop: fake metrics, random values, dead navigation, placeholder links that look functional, generic gradient-glow decoration, repetitive pill badges, and inconsistent accents.
- Preserve real authentication and roadmap behavior while improving feedback, keyboard use, responsive layout, and reduced-motion behavior.
- Create a small set of shared visual primitives/tokens so later screens can extend the same language without a new dependency or broad architecture rewrite.

**Non-Goals:**

- Adding user-progress, enrollment, password-reset, profile-editing, challenge, AI review, leaderboard, community, or social APIs.
- Implementing out-of-scope product modules or retaining fake data to preview them.
- Replacing React Flow, Axios, React Router, Tailwind, Inter, or the existing frontend folder structure.
- Changing backend DTOs, endpoints, database state, Docker services, ports, environment variables, or dependency versions.
- Rebranding the product name or creating a new illustration/marketing asset pipeline.

## Decisions

### 1. Define semantic theme tokens in the existing Tailwind 4 entry point

`src/frontend/src/index.css` will become the single source for the visual foundation. Tailwind 4 `@theme` values and a small set of CSS custom properties will define canvas, shell, surface, elevated surface, border, primary text, muted text, gold accent, accent hover, focus ring, and semantic success/warning/danger colors. Representative targets are near-black canvas/surfaces, warm off-white text, and an accessible gold in the `#E0B84F`–`#F1CC72` range. Primary gold-filled controls will use near-black text rather than white.

Page-local hard-coded purple/indigo/cyan classes and decorative gradients will be replaced by these tokens. Semantic green remains reserved for completed/success states, red for errors/destructive actions, and muted neutral treatment for locked/disabled states. Gold represents primary action, selection, available/current learning focus, and brand identity.

Alternative considered: mechanically replace every purple class with an amber class. Rejected because it preserves inconsistent surfaces, duplicate focus styles, and theme drift while making the interface louder rather than more coherent.

### 2. Use restrained surfaces instead of ambient glow decoration

The visual hierarchy will rely on surface elevation, thin borders, spacing, typography, and a limited shadow scale. Large blurred radial circles, purple mesh-like gradients, glow-heavy cards, gradient text, and decorative status dots will be removed. A subtle grid may remain only where it provides spatial orientation in the roadmap canvas; it must not compete with content.

Panels use `rounded-xl` or `rounded-2xl`; controls use `rounded-lg` or `rounded-xl`. Badges are limited to compact metadata and learning state, avoiding pill shapes for ordinary buttons or containers.

Alternative considered: retain the current effects and recolor them gold. Rejected because the perceived “AI template” quality comes from the effect density and generic composition, not only the hue.

### 3. Make the application shell roadmap-first

The authenticated sidebar will expose only implemented, useful destinations: Overview, Roadmaps, and the existing Profile entry through the identity block. Undefined routes for Skill Tree, Learning Space, Code Review, and Leaderboard will be removed from visible navigation until those capabilities exist.

On narrow viewports, `DashboardLayout` will provide a compact top bar and an accessible navigation drawer controlled by an explicit menu button. The desktop sidebar remains persistent at the current breakpoint. Drawer open state stays local to the layout; no global state library is introduced.

Alternative considered: render unavailable modules with “Coming soon” pages. Rejected because this increases MVP scope and still makes unfinished features compete with the working roadmap flow.

### 4. Replace the demo dashboard with honest API-backed orientation

`DashboardOverview` will stop rendering the random heatmap, fabricated streak/rank/leaderboard, and out-of-scope actions. It will use the existing auth context for the greeting and the existing roadmap service to show a clear next action:

- while loading, show a stable skeleton;
- when roadmaps exist, present the catalog/first available roadmap with a direct route to Roadmaps;
- when no roadmaps exist, explain the empty state without inventing progress;
- on request failure, present a retry action without reloading the entire browser page.

Until a user-progress API exists, the dashboard must not display completion percentages, current streaks, ranks, points, or “resume” claims that cannot be derived from real data.

Alternative considered: keep deterministic mock data instead of `Math.random()`. Rejected because stable fiction is still misleading and conflicts with the MVP-first product rule.

### 5. Simplify public and authentication surfaces around truthful product value

The landing page becomes a concise entry surface: Journi.dev identity, roadmap-tracker value proposition, sign-up/sign-in or dashboard CTA based on auth state, and a small product explanation tied to the implemented flow. Floating teaser cards, community avatar stacks, “10,000+ developers,” peer code review promotion, and other unverifiable claims are removed.

Sign-in and sign-up retain their existing backend contracts and state behavior. Their duplicated decorative code panels will be consolidated into an auth-specific shared shell because it has two concrete consumers. Forms will use consistent labels, errors, disabled/loading states, autocomplete attributes, and gold focus/primary styles. Lucide icons will replace local general-purpose icon components where practical; brand icons may remain local.

Links with `href="#"` will not remain interactive-looking placeholders. Unsupported destinations such as password recovery, Terms, Privacy, or Help will either use a real route/URL if one exists or render as clearly unavailable text without hijacking the current page.

Alternative considered: retain the large split-screen developer-code illustration. Rejected because the duplicated decoration dominates the task, makes auth pages denser on laptop widths, and repeats generic developer-product tropes without improving completion.

### 6. Clarify roadmap states without turning the graph into a color carnival

Roadmap catalog items will use semantic links rather than clickable `div` elements. Catalog cards prioritize title, concise description, available metadata, and one clear action. Loading, empty, and error states share the same language and retry pattern as the dashboard.

The graph retains React Flow and the current layout algorithm. Node type becomes quiet neutral metadata; node state owns the visual emphasis:

- completed: green check and completed label;
- current/available/in progress: gold border/focus treatment and explicit text label;
- locked: muted neutral surface, lock icon, and reduced contrast that remains readable.

Search match, selection, and keyboard focus must be distinct without relying on color alone. React Flow nodes will be focusable and expose meaningful accessible names; Enter/Space inspection must produce the same drawer result as pointer selection. The toolbar will report progress and search-match feedback without covering essential graph content.

Alternative considered: preserve a different accent for every node type. Rejected because type colors compete with progress state, which is the information users need to decide what to do next.

### 7. Treat the node drawer as the primary learning-detail surface

The drawer keeps the currently available node title, summary, type, status, lock state, resource placeholder, and checklist placeholder. On desktop it remains a right-side panel. On narrow viewports it becomes a bottom sheet or full-width modal surface with a visible close action, contained scrolling, focus management, Escape dismissal, and an overlay that separates it from the graph.

No completion CTA is added until the corresponding progress endpoint and frontend contract exist. Locked content remains inspectable for context but must be clearly identified as unavailable.

Alternative considered: navigate to a new node-detail route. Rejected for this change because it alters the current route model and would broaden the proposal beyond a visual/UX redesign.

### 8. Accessibility and motion are system behavior, not page polish

All interactive elements will have visible `:focus-visible` treatment, a minimum practical touch target, descriptive accessible names, and disabled/pending semantics where applicable. Information states use icons/text in addition to color. Text and controls will be checked for WCAG AA contrast against their final surface.

Existing page transitions and hover movement will honor `prefers-reduced-motion: reduce`; nonessential floating/pulsing animation will be removed. Error messaging will use `role="alert"` or an equivalent live region when introduced after form submission or failed data loading.

Alternative considered: defer accessibility until after the visual pass. Rejected because focus, contrast, layout, and motion choices are foundational and expensive to retrofit.

## Risks / Trade-offs

- [Gold can become muddy or low-contrast on dark surfaces] → Use gold primarily for controls/borders/icons, pair gold fills with near-black text, and verify key combinations against WCAG AA before spreading tokens.
- [Removing demo modules can make the dashboard appear sparse] → Use deliberate typography, roadmap context, and useful empty/loading states; do not fill space with invented metrics.
- [The current roadmap API may provide limited metadata] → Render only fields in existing types and omit unavailable labels rather than fabricating them.
- [Responsive shell and graph changes can introduce overflow or focus traps] → Verify desktop, tablet, and mobile widths; test keyboard entry/exit from navigation and drawer; keep graph controls clear of overlays.
- [Theme migration touches many class strings and can create inconsistent intermediate states] → Implement foundation and shared shell first, then migrate routes in bounded slices with build/lint and visual checks after each slice.
- [Existing OpenSpec frontend requirements describe obsolete presentation-first auth/dashboard behavior] → Deliver explicit delta specs in this change so implementation and source-of-truth behavior converge.
- [The embedded browser was unavailable during proposal creation] → Treat screenshot-based desktop/mobile visual regression review as a required apply-phase task rather than claiming visual verification in this proposal.

## Migration Plan

1. Add the token foundation and global accessibility/reduced-motion rules without removing existing classes.
2. Migrate shared logo, controls, page states, and dashboard shell/navigation to the tokens.
3. Redesign public and auth routes, preserving their existing submission and redirect contracts.
4. Replace the demo dashboard with roadmap-service-driven orientation and delete now-unused demo-only components/exports.
5. Migrate roadmap catalog, canvas, nodes, toolbar, and drawer; then migrate profile and not-found surfaces.
6. Remove obsolete purple/indigo/cyan hard-coded styles, dead placeholder links, unused icons/components, and debug-only logging encountered in the touched files.
7. Run frontend lint/build and browser-based keyboard/responsive/visual checks. Roll back by reverting the frontend commits; no data or API migration is involved.

## Open Questions

- The exact production gold token may be tuned during visual QA within the specified contrast and semantic constraints; this does not change the black-and-gold direction.
- A future progress API should determine how “resume learning” and completion summaries are introduced. This redesign intentionally leaves those values absent rather than defining a fake interim contract.

