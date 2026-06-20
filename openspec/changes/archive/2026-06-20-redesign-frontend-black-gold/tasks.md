## 1. Visual Foundation

- [x] 1.1 Add named black, warm-neutral, gold, border, focus, and semantic state tokens to `src/frontend/src/index.css` using the existing Tailwind CSS 4 setup.
- [x] 1.2 Add shared base styles for body selection, keyboard-visible focus, disabled controls, reduced motion, and accessible high-contrast text without changing installed dependencies.
- [x] 1.3 Restyle the React Flow minimap, canvas background, and controls to use the new tokens and remove purple, violet, indigo, and cyan brand treatments.
- [x] 1.4 Update the shared `Logo` component with accessible image text and the black-and-gold brand treatment.

## 2. Responsive Application Shell

- [x] 2.1 Reduce dashboard navigation to implemented Overview and Roadmaps destinations while preserving the Profile identity entry.
- [x] 2.2 Refactor `DashboardLayout` and `Sidebar` to provide persistent desktop navigation plus a local-state mobile header/drawer with menu, close, Escape, and route-change behavior.
- [x] 2.3 Replace fixed desktop-only page gutters, header alignment, shell radius, and overflow rules with responsive values that avoid horizontal scrolling.
- [ ] 2.4 Verify active, hover, focus-visible, and mobile-drawer states for every remaining navigation destination.

## 3. Public and Authentication Experience

- [x] 3.1 Redesign `Welcome` as a concise roadmap-tracker entry page with truthful MVP copy and auth-aware sign-up, sign-in, or dashboard actions.
- [x] 3.2 Remove floating demo cards, fabricated community statistics, unverified claims, and promotion of unavailable peer-review/social features from the public experience.
- [x] 3.3 Create an auth-specific shared shell used by both sign-in and sign-up to centralize layout, logo placement, form surface, and responsive behavior.
- [x] 3.4 Migrate sign-in controls to the shared gold interaction styles, add pending/duplicate-submit protection and accessible error feedback, and preserve the existing username/password login and redirect contract.
- [x] 3.5 Migrate sign-up controls to the shared gold interaction styles, preserve the existing signup contract and success behavior, and keep unavailable legal/help destinations non-interactive unless a real target exists.
- [x] 3.6 Replace duplicated general-purpose auth icons with installed Lucide icons where practical and remove any now-unused local icon files or exports without changing brand icons.

## 4. Roadmap-First Dashboard

- [x] 4.1 Replace the random heatmap, fabricated leaderboard, milestone cards, streak, points, and out-of-scope quick actions in `DashboardOverview` with auth context plus roadmap-service data.
- [x] 4.2 Implement dashboard loading, roadmap-available, empty, and retryable error states without fabricated progress or full-page reload recovery.
- [x] 4.3 Add clear dashboard actions to browse roadmaps or open an available roadmap using existing route paths and response types.
- [x] 4.4 Delete dashboard demo components and barrel exports that have no remaining consumer after the roadmap-first overview and landing redesign.

## 5. Roadmap Catalog and Request States

- [x] 5.1 Restyle `RoadmapsPage` and `RoadmapDetailPage` with responsive headers, black-and-gold surfaces, restrained typography, and consistent loading/empty/error states.
- [x] 5.2 Convert roadmap catalog cards from clickable containers to semantic keyboard-operable links while preserving the existing roadmap-detail URL.
- [x] 5.3 Replace `window.location.reload()` recovery with scoped request retry functions on the roadmap catalog and detail pages.
- [x] 5.4 Remove casual or generic error copy, decorative glow blobs, and invented fallback claims from roadmap request states.

## 6. Roadmap Canvas and Node Details

- [x] 6.1 Restyle roadmap skill nodes so node type is neutral metadata and completed, current/in-progress, and locked states use explicit icons/text plus the shared semantic colors.
- [x] 6.2 Make roadmap nodes keyboard focusable with meaningful accessible names and equivalent Enter/Space detail activation while retaining the read-only graph contract.
- [x] 6.3 Update the roadmap toolbar to show responsive progress, fit-view focus states, search match count or no-results feedback, and unobstructed graph placement.
- [x] 6.4 Refactor the node drawer into a desktop side panel and narrow-screen bottom sheet/modal with overlay, contained scrolling, initial focus, Escape close, and focus return.
- [x] 6.5 Keep checklist/resources placeholders concise and non-interactive, and ensure locked nodes do not expose enabled start or completion actions.
- [ ] 6.6 Verify graph pan, zoom, fit view, minimap, search dimming, selection, and sequential edge behavior remain intact after the visual and accessibility changes.

## 7. Remaining Route Consistency and Cleanup

- [x] 7.1 Migrate the profile page, profile panels, logout dialog, and session controls to the shared surfaces, gold focus treatment, and responsive spacing while preserving logout behavior.
- [x] 7.2 Redesign the not-found screen with the shared visual system while preserving its history-aware Go Back or Homepage recovery behavior.
- [x] 7.3 Remove remaining purple, violet, indigo, and cyan brand classes, oversized glow/gradient decoration, dead `href="#"` links, unused imports, and touched-file debug logs.
- [x] 7.4 Confirm no API base URL, endpoint contract, route path, environment variable, dependency, or backend file changed as part of the redesign.

## 8. Verification

- [x] 8.1 Run `npm run lint` in `src/frontend` and resolve all issues introduced by this change.
- [x] 8.2 Run `npm run build` in `src/frontend` and confirm TypeScript and Vite production builds succeed.
- [ ] 8.3 Perform browser visual checks at representative mobile, tablet, laptop, and desktop widths for `/`, `/signin`, `/signup`, `/dashboard`, `/dashboard/roadmaps`, a populated roadmap detail route, `/dashboard/profile`, and an unknown route.
- [ ] 8.4 Perform keyboard-only checks for auth forms, responsive navigation, roadmap cards, graph nodes, toolbar, node drawer, logout dialog, and not-found recovery.
- [x] 8.5 Verify essential text/control contrast and `prefers-reduced-motion` behavior, then search the frontend for prohibited legacy accent classes and placeholder links.
- [x] 8.6 Run `openspec validate redesign-frontend-black-gold --strict` and confirm all proposal artifacts remain valid after implementation updates.
