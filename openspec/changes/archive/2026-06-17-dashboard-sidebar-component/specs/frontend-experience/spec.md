## ADDED Requirements

### Requirement: Dashboard Layout and Navigation Transitions
The Dashboard layout SHALL isolate route transitions such that the sidebar remains stationary while the main content area animates. The sidebar SHALL include a visual indicator that smoothly slides to the currently active navigation item when the user changes routes within the dashboard.

#### Scenario: Navigating between dashboard routes
- **WHEN** a user navigates from one dashboard sub-route to another (e.g., from `/dashboard` to `/dashboard/roadmaps`)
- **THEN** the sidebar remains stationary without unmounting or re-animating
- **THEN** the active item indicator in the sidebar slides smoothly to the new active item
- **THEN** the main content area unmounts the old view and animates the new view using the slide-up transition
