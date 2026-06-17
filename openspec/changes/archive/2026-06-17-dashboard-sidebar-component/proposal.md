## Why

The current Dashboard sidebar is embedded directly within the layout code, making it difficult to maintain and animate independently. Extracting it into a standalone component allows us to implement a smooth sliding indicator effect for the selected option, while keeping the sidebar itself stationary during page transitions (where only the main content slides up).

## What Changes

- Extract the dashboard sidebar UI from the Dashboard Layout into a new `Sidebar` component.
- Implement a sliding highlight/indicator effect for the active sidebar option.
- Ensure the sidebar component remains stationary during route transitions, while the main content area continues to use the existing slide-up transition.

## Capabilities

### New Capabilities
- None. This is an architectural UI refactor and animation enhancement.

### Modified Capabilities
- `frontend-experience`: Refine the dashboard layout animations (stationary sidebar, sliding active indicator) to improve the overall UX.

## Impact

- `src/frontend/src/pages/Feed/DashboardLayout.tsx`: Will be refactored to use the new `Sidebar` component.
- `src/frontend/src/pages/Feed/components/Sidebar.tsx` (new): Will house the sidebar logic and sliding indicator animation.
- Routing transitions in `App.tsx` or `DashboardLayout.tsx` might need minor adjustments to ensure the sidebar isn't unmounted/remounted during sub-route navigation.
