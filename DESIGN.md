# DESIGN.md

## Purpose

This file is the frontend design and structure source of truth for Journi.dev.

Agents must read this file before changing frontend pages, components, layout, styling, or feature structure. The goal is to keep the current UI language intact while continuing the MVP roadmap flow:

```text
login -> choose roadmap -> view nodes -> open node -> complete node -> unlock next node
```

## Authority Order

When frontend UI guidance conflicts, follow this order:

1. `AGENTS.md` for product scope, MVP priority, API rules, and repo-wide constraints.
2. `DESIGN.md` for Journi.dev frontend UI, layout, component placement, and visual decisions.
3. `.agents/skills/design-taste-frontend/SKILL.md` for general taste and anti-slop checks.
4. Existing frontend implementation for exact local patterns.

For Journi-specific UI decisions, this file overrides generic Taste Skill defaults.

## Current Product UI

Journi.dev is a developer learning platform, roadmap tracker, and skill-tree LMS. The main experience is an authenticated product workspace, not a marketing landing page.

The UI should feel like a premium dark learning OS: calm, technical, focused, compact, and credible.

Keep the current visual language:

- Dark warm surfaces.
- Thin warm borders.
- Subtle depth.
- Gold as the brand accent and primary action color.
- Green for completed or success states.
- Red for error or destructive states.
- Warning amber only for warning states.
- Neutral gray and muted surfaces for locked, disabled, inactive, and secondary states.

Do not reintroduce purple, indigo, violet, cyan, neon gradients, decorative mesh blobs, or generic AI dashboard decoration as competing brand accents.

## Stack And Dependencies

The current frontend stack is defined in `src/frontend/package.json`:

- React
- React Router DOM
- Vite
- TypeScript
- TailwindCSS v4
- Axios
- Lucide React
- `@xyflow/react` for the existing roadmap canvas

Do not add a new UI framework, state library, graph library, animation library, icon family, or data-fetching library unless the user explicitly asks.

Use the shared Axios setup in `src/frontend/src/services/axios.tsx`. Do not create new Axios instances inside components.

## Design Tokens

The theme tokens live in `src/frontend/src/index.css`. Prefer these tokens and utility classes over hard-coded hex values:

```text
canvas            #080806
shell             #0d0c09
surface           #14120d
surface-elevated  #1a1710
line              #302a1d
line-strong       #4a3c20
ink               #f5f1e8
muted             #aaa397
subtle            #7c756a
gold              #e6b94f
gold-strong       #f1cc72
gold-soft         #8f712f
gold-ink          #171205
success           #63c38a
danger            #ef7b77
warning           #dcae4b
```

Hard-coded hex is acceptable only when a third-party API requires raw color values, such as `@xyflow/react` MiniMap props. Even then, match the existing tokens.

## Shared Component Classes

Reuse these classes from `src/frontend/src/index.css` before inventing new Tailwind combinations:

- `.app-panel` for standard panels and cards.
- `.app-panel-elevated` for stronger panels.
- `.primary-button` for gold primary actions.
- `.secondary-button` for neutral secondary actions.
- `.icon-button` for square icon-only controls.
- `.app-input` for form and search inputs.
- `.eyebrow` for sparse uppercase section labels.

If a new repeated pattern appears in at least two places, add a small reusable class or component instead of copying a long class string repeatedly.

Do not extend `src/frontend/src/App.css` for product UI. It contains legacy starter styles and is not the current design source.

## Layout Patterns

Use the current product-shell structure:

- Dashboard routes live inside `DashboardLayout`.
- Desktop navigation uses the fixed left `Sidebar` with `bg-shell`, `border-line`, and width `264px`.
- Mobile navigation uses a sticky top header, overlay scrim, and slide-in sidebar panel.
- Page content sits on `bg-canvas` with constrained widths such as `max-w-6xl` or `max-w-[1500px]`.
- Use compact vertical spacing for app surfaces. This is a tool, not a landing page.

Avoid oversized hero sections, marketing-style feature bands, decorative screenshots, and generic three-card sections unless the user explicitly asks for a landing page.

## Typography

Keep Inter via the current `--font-sans` token unless the user explicitly approves a new type system.

Use the existing hierarchy:

- Page titles: `text-3xl sm:text-4xl`, `font-semibold`, tight tracking.
- Panel headings: `text-base` to `text-xl`, `font-semibold`.
- Body text: `text-sm` or `text-base`, `leading-6`, `text-muted`.
- Metadata and labels: small uppercase text with restrained tracking.

Do not scale typography with viewport width. Do not use random display fonts or serif accents for product screens.

## Controls

Buttons and inputs should feel consistent across the app:

- Primary actions use gold background and `text-gold-ink`.
- Secondary actions use `border-line`, `bg-surface`, and `text-ink`.
- Icon controls use square `h-11 w-11` or smaller variants for tight dialogs.
- Disabled controls use opacity and preserve layout.
- Destructive actions use `danger`, not gold.
- Link-like navigation uses muted text with gold or ink hover states.

Every interactive control needs a visible focus state. The global `:focus-visible` rule already provides the gold outline.

## Panels, Cards, And Surfaces

Use panels only when they frame real product information or an action area.

Current surface pattern:

- Standard panels: `rounded-2xl border border-line bg-surface`.
- Elevated panels: `rounded-2xl border border-line bg-surface-elevated`.
- Nested compact items: `rounded-lg` or `rounded-xl`, `border-line`, `bg-canvas/45` or `bg-surface`.
- Shadows should be restrained and black with low opacity.
- Avoid cards inside cards unless the nested item is an actual row, checklist item, stat, or resource link.

Do not add random glass panels. Use `backdrop-blur` only for overlays, sticky headers, and toolbar surfaces where it improves readability.

## Roadmap UI

Roadmap work must preserve the existing canvas-first product experience:

- `RoadmapCanvas` owns the graph workspace.
- `RoadmapSkillNode` owns individual node cards.
- `RoadmapToolbar` owns progress, search, and fit-view controls.
- `RoadmapNodeDrawer` owns node detail, learning resources, checklist, and completion actions.

Node states:

- Locked: muted text, shell or surface background, neutral border.
- Available or in progress: gold border and gold-tinted badge.
- Completed: success border and success-tinted badge.
- Selected: gold border plus subtle gold ring.
- Search match: warning outline.
- Search dimmed: reduced opacity.

Do not replace the current roadmap canvas with a new graph visualization, a complex React Flow redesign, or decorative diagrams unless explicitly requested. Improve the current MVP interaction first.

## Drawers, Dialogs, And Popups

Follow the existing modal patterns:

- Use a dark scrim such as `bg-black/55` or `bg-black/75`.
- Use `role="dialog"`, `aria-modal="true"`, and labelled headings.
- Focus the close or cancel button when opened.
- Trap focus inside the dialog.
- Close with Escape when it is safe.
- Restore focus when the dialog closes when practical.
- Use `rounded-2xl`, `border-line`, `bg-shell`, and restrained shadows.
- On mobile, drawers may use `rounded-t-2xl` and attach to the bottom.

Do not ship popups that are mouse-only or visually disconnected from the current shell.

## Forms And Auth UI

Auth screens use `AuthShell`:

- Desktop has a left brand/context panel and right form panel.
- Mobile keeps the logo and form focused.
- Forms use `.app-panel`, `.app-input`, `.primary-button`, and `.secondary-button`.
- Labels belong above inputs. Do not use placeholder text as the only label.
- Inline errors use `danger` borders or backgrounds and `role="alert"` where appropriate.

Keep auth API calls inside auth service/context code, not inside unrelated UI components.

## Loading, Empty, Error, And Success States

Every frontend feature should include practical states:

- Loading: existing spinner style with `LoaderCircle`, gold color, and `motion-reduce:animate-none`.
- Empty: centered icon tile, clear heading, muted body copy.
- Error: `danger/10` background, `danger/30` border, concise recovery action.
- Success or completed: `success/10` background, success icon or badge, short confirmation.
- Locked: neutral muted copy explaining the prerequisite.

Avoid broad silent fallbacks. If data cannot load, show a useful state.

## Icons

Use `lucide-react`, because it is already installed and used across the app.

Keep icon use consistent:

- Standard control icons are usually `16px` to `20px`.
- Larger state icons are usually `21px` to `27px`.
- Use `strokeWidth={1.8}` or the existing local value when a component already establishes it.
- Do not mix icon libraries.
- Do not hand-roll SVG icons for common actions.

## Motion

Motion should be subtle and purposeful:

- Use `transition-colors` for hover state changes.
- Use small route or content transitions through existing utilities in `src/frontend/src/utils/transitions`.
- Use spinner animation only for loading states and always include `motion-reduce:animate-none`.
- Avoid bounce, cinematic scroll effects, parallax, animated blobs, and new animation libraries.

Animations must communicate feedback, hierarchy, or state change.

## Frontend Structure

Follow the current folder responsibilities:

```text
src/frontend/src
├── pages/       route-level screens and route wrappers
├── features/    domain-owned UI, services, hooks, types, and utilities
├── components/  cross-feature reusable UI components
├── services/    app-wide service infrastructure
├── utils/       cross-feature pure helpers and transitions
└── assets/      static assets
```

Placement rules:

- Put route-level screens in `pages/<Domain>/`.
- Put domain components inside `features/<domain>/components/` or a domain subfolder such as `features/roadmaps/roadmap-canvas/components/`.
- Put domain API calls in `features/<domain>/services/`.
- Put domain types in `features/<domain>/types/`.
- Put domain helpers in `features/<domain>/utils/`.
- Put cross-feature components only in `components/`.
- Put app-wide service infrastructure only in `services/`.
- Put cross-feature pure helpers only in `utils/`.

Do not create new top-level folders. Do not move files unless the task is explicitly about restructuring.

## Domain Placement Examples

Authentication:

- Pages: `src/frontend/src/pages/Auth/`
- Components: `src/frontend/src/features/auth/components/`
- Hooks: `src/frontend/src/features/auth/hooks/`
- Services: `src/frontend/src/features/auth/services/`
- Context: `src/frontend/src/features/auth/AuthContext.ts`

Roadmaps and progress:

- Pages: `src/frontend/src/pages/Roadmaps/`
- Services: `src/frontend/src/features/roadmaps/services/`
- Types: `src/frontend/src/features/roadmaps/types/`
- Canvas components: `src/frontend/src/features/roadmaps/roadmap-canvas/components/`
- Canvas utilities: `src/frontend/src/features/roadmaps/roadmap-canvas/utils/`

Dashboard and profile:

- Layout page: `src/frontend/src/pages/Dashboard/DashboardLayout.tsx`
- Dashboard shell components: `src/frontend/src/features/dashboard/components/`
- Profile components: `src/frontend/src/features/profile/components/`

## API And State Placement

Pages may orchestrate route-level data loading when that is the existing pattern.

Feature services own API calls. Components should receive data and callbacks as props unless they are already domain containers.

Do not:

- Hard-code API base URLs in components.
- Create Axios clients in components.
- Duplicate roadmap unlocking or node-state business rules across unrelated components.
- Add global state before local state, props, or existing auth context are insufficient.
- Weaken TypeScript types with `any` to silence errors.

## Before Editing Frontend UI

Use this checklist:

1. Read `src/frontend/package.json` before using framework or dependency APIs.
2. Read `src/frontend/src/index.css` for tokens and shared classes.
3. Read the relevant page, feature component, service, type, and utility.
4. Copy the nearby pattern first, then adjust only what the task requires.
5. Add loading, error, empty, locked, and completed states when the flow can reach them.
6. Keep the MVP learning roadmap flow working.
7. Run the smallest relevant frontend check, usually `npm run build` after TypeScript, route, or API contract changes.

## Anti-Patterns

Do not ship these in Journi.dev frontend work:

- Purple, indigo, violet, cyan, neon, or mesh-gradient accents.
- Random gradients or decorative glow blobs.
- Marketing landing-page composition for authenticated product screens.
- Fake dashboard screenshots or placeholder product mockups.
- New UI frameworks or component libraries for visual polish.
- New icon families.
- Component-local Axios instances.
- Duplicated API base URLs.
- Components placed in `components/` when they are domain-specific.
- Large page files that mix unrelated UI, API, and business rules.
- Broad catch blocks that hide user-facing errors.
- Formatting-only rewrites.

## Final UI Review

Before finishing frontend UI work, verify:

- The page still looks like the existing dark gold Journi.dev app.
- The route fits inside the dashboard shell where appropriate.
- Panels, buttons, inputs, drawers, and dialogs reuse current classes or clearly match them.
- Text does not overflow buttons, badges, cards, drawers, or mobile layouts.
- Focus, hover, disabled, loading, error, empty, locked, available, and completed states are handled.
- New files are in the correct folder for their domain.
- The MVP flow remains intact.
