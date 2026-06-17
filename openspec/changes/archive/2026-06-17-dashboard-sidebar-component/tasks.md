## 1. Sidebar Component Extraction

- [x] 1.1 Create `Sidebar.tsx` in `src/frontend/src/pages/Feed/components/`.
- [x] 1.2 Move the `<aside>` JSX from `DashboardLayout.tsx` into `Sidebar.tsx`.
- [x] 1.3 Move the `user` profile loading logic (`useAuth`) and `Logo` import into `Sidebar.tsx`.

## 2. Sliding Indicator Implementation

- [x] 2.1 Refactor the navigation links in `Sidebar.tsx` into an array of objects to compute the `activeIndex`.
- [x] 2.2 Add an absolute-positioned `div` to `Sidebar.tsx` that uses `transform: translateY(...)` and `transition` for the sliding effect.
- [x] 2.3 Adjust `NavItem` styling (if necessary) to ensure z-index correctness and transparent background so the slider shows underneath.

## 3. Transition Refactoring

- [x] 3.1 Modify `App.tsx` to stop wrapping the entire `<Routes>` in `<SlideUpTransition key={location.pathname}>`.
- [x] 3.2 Update `DashboardLayout.tsx` to apply `<SlideUpTransition key={location.pathname}>` exclusively around the `<Outlet />` component to retain main content animation.
- [x] 3.3 Ensure other top-level routes (`/`, `/signin`, `/signup`) continue to have transition effects if necessary (by wrapping them individually or keeping them inside a separate root layout).

## 4. Verification

- [x] 4.1 Run the frontend dev server (`npm run dev`) and test navigation within the dashboard.
- [x] 4.2 Verify the sidebar does not re-mount when clicking dashboard links.
- [x] 4.3 Verify the active indicator slides smoothly to the newly selected item.
- [x] 4.4 Verify that the main content area continues to animate with the slide-up transition.
