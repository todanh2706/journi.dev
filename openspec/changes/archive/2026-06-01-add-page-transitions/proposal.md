## Why

We need to implement a smooth and modern page transition effect (fade-in) when navigating between pages, specifically starting with the transition from the "Home" page to the "Sign In" page. This enhances the perceived performance and creates a premium, dynamic feel that aligns with modern web design standards.

## What Changes

- Add a reusable transition component/utility in `src/frontend/src/utils/transitions/`.
- Configure the frontend routing to apply this fade-in transition (300-500ms duration, ease-in-out easing) when navigating from Home to Sign In.
- Ensure the transition logic is not hardcoded into individual page files, but centralized for reusability across the application.

## Capabilities

### New Capabilities
- `page-transitions`: Centralized logic for handling routing animations (e.g., fade-in) across the frontend application.

### Modified Capabilities
- `frontend-experience`: Update routing configuration to incorporate page transition animations.

## Impact

- **Code**: Adds new files in `src/frontend/src/utils/transitions/` and updates React Router configuration.
- **UX**: Navigations will have a smooth fade-in effect instead of abrupt page loads.
