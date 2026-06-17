## Context

Currently, the Dashboard sidebar is embedded in `DashboardLayout.tsx`. In `App.tsx`, the entire `<Routes>` component is wrapped in a `<SlideUpTransition key={location.pathname}>`. This means that on every route change (even between dashboard sub-pages like `/dashboard` to `/dashboard/roadmaps`), the entire page (including the layout and sidebar) unmounts, remounts, and triggers the slide-up animation.

The user has requested to:
1. Extract the sidebar into its own standalone component (`Sidebar.tsx`).
2. Add a sliding indicator effect for the active navigation item in the sidebar.
3. Keep the sidebar stationary during navigation between dashboard routes, while the main content continues to use the slide-up transition.

## Goals / Non-Goals

**Goals:**
- Extract `Sidebar` from `DashboardLayout.tsx`.
- Implement a CSS transition on an absolute positioned indicator within the sidebar that slides vertically to the active option.
- Refactor the routing or transition placement so the sidebar does not re-mount on every sub-route change, while the `Outlet` content retains the `SlideUpTransition`.

**Non-Goals:**
- Completely rewriting the routing library or layout system.
- Introducing heavy animation libraries (like Framer Motion) when simple CSS transitions suffice.

## Decisions

1. **Sidebar Extraction:**
   - Create `src/frontend/src/pages/Feed/components/Sidebar.tsx`.
   - Maintain the existing visual styling and user profile display.

2. **Sliding Indicator:**
   - Define the list of navigation routes as a constant array within `Sidebar.tsx`.
   - Use `useLocation()` to determine the `activeIndex`.
   - Render an absolute-positioned `div` behind the links with a CSS `transition: transform 0.3s ease` and `transform: translateY(...)` based on the `activeIndex`.

3. **Transition Placement Refactoring:**
   - In `App.tsx`, remove the `SlideUpTransition` wrapper around the entire `<Routes>` component (or remove the `key={location.pathname}` so it doesn't unmount on every route change).
   - Alternatively, apply `SlideUpTransition` at the individual page component level (e.g., inside `DashboardOverview`, `RoadmapsPage`) OR wrap the `Outlet` in `DashboardLayout` with `<SlideUpTransition key={location.pathname}>`.
   - Wrapping the `Outlet` in `DashboardLayout` is preferred as it keeps the transition logic centralized for dashboard routes and ensures the layout (`Sidebar`) remains stationary.

## Risks / Trade-offs

- **Risk:** Changing transition logic in `App.tsx` might affect non-dashboard routes (like `/signin` or `/signup`).
  - **Mitigation:** We will wrap the element inside the non-dashboard routes with `SlideUpTransition` directly, or keep a root-level layout for them if necessary. Actually, the easiest way is to let each main page component handle its own transition wrap, or just change `App.tsx` so that `SlideUpTransition` only wraps leaf routes.
- **Risk:** The sliding indicator position might be inaccurate if item heights vary or margins change.
  - **Mitigation:** Ensure all `NavItem` entries have a fixed height (e.g., `h-10` or `40px`) and consistent margins/padding so the `translateY` offset can be cleanly calculated (e.g., `activeIndex * 48px`).
