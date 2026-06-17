## Why

Currently, the "Go to Homepage" button on the 404 Not Found page always navigates users back to `/`. However, if the user arrives at the 404 page by clicking a broken link while navigating inside the dashboard (where they are logged in), returning to the homepage (`/`) often lands them on the landing page or a disconnected context. It is a much better UX to provide a "Return Back" button that acts as a browser back navigation (`history.back()`). For edge cases where there is no browser history (e.g., typing a wrong URL directly), it should fallback to "Go to Homepage".

## What Changes

- **Smart Navigation Logic**: Implement a check using `window.history.length` or React Router's navigation state to determine if the user has a prior history stack.
- **Dynamic Button State**: Change the 404 page button text from "Go to Homepage" to "Go Back" when history is available.
- **Dynamic Button Action**: Change the button behavior to execute a back navigation (`navigate(-1)`) if history exists, otherwise navigate to `/`.

## Capabilities

### New Capabilities

### Modified Capabilities

- `frontend-experience`: The Not Found UI flow will be updated to conditionally return the user to the previous page instead of hardcoding the homepage destination.

## Impact

- **Frontend Pages**: Modifications to `src/frontend/src/pages/NotFound.tsx`.
- **UX/Routing**: Improves the logical flow when encountering dead links within the application. No backend or architectural impact.
