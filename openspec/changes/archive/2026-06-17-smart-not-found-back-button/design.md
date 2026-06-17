## Context

The 404 Not Found page currently has a static button linking to the root path (`/`). This can be disruptive when users are navigating deep within the app and click a broken link, as returning to the homepage (`/`) pulls them out of their context. Providing a browser history back navigation is a standard UX best practice for 404 pages.

## Goals / Non-Goals

**Goals:**

- Provide a context-aware back button on the 404 page.
- Maintain existing styles and aesthetics for the button.
- Support fallback to the homepage if there is no browser history.

**Non-Goals:**

- Modifying other error pages (e.g. 500 Server Error).
- Adding complex routing state management to track breadcrumbs.

## Decisions

- **Detecting History**: Use `window.history.length > 1` (or `> 2` depending on browser behavior) to determine if the user has navigated from a previous page within the same session. A robust approach in React Router 7 is to use the `useNavigate` hook for `navigate(-1)`. We can check if `window.history.length > 2` to safely decide whether to show "Go Back" instead of "Go to Homepage".
- **Implementation Mechanism**:
    - Introduce a state or simple variable: `const hasHistory = window.history.length > 2;`.
    - Use a single `<button>` element that dynamically changes its `onClick` action (`navigate(-1)` vs `navigate("/")`) and its text ("Go Back" vs "Go to Homepage") depending on `hasHistory`.

## Risks / Trade-offs

- **Risk: Inconsistent `window.history.length` across browsers** → Mitigation: Modern browsers reliably implement history length. If it evaluates to false, the user safely falls back to the "Go to Homepage" link.
- **Risk: Navigating back to an external site** → Mitigation: If `history.length` includes the external referer, `navigate(-1)` might take them off the site. However, usually, Single Page Apps reset history when loading from a fresh URL. Using standard React Router constructs provides reasonable safety.
