## Context

The Home page currently displays "Sign In" and "Sign Up" call-to-action buttons for all users, regardless of whether they are logged in or not. For logged-in users, this creates a confusing UX as they may believe they have been logged out or their session has expired.

## Goals / Non-Goals

**Goals:**
- Determine the user's authentication state on the Home page.
- Conditionally render the CTA buttons:
  - Unauthenticated: Show "Sign In" and "Sign Up"
  - Authenticated: Show "Go to Dashboard" and/or User Avatar.

**Non-Goals:**
- Completely redesigning the Home page.
- Modifying backend authentication logic.

## Decisions

- **Use existing Auth Context**: The frontend already has a `useAuth` hook or equivalent auth context. We will consume this hook in the `HomePage` component to get the `user` or `isAuthenticated` state.
- **Component Updates**:
  - `Navbar` or `Header` component in the Home page.
  - Hero section CTA buttons.

## Risks / Trade-offs

- **Flash of Unauthenticated State**: If auth state is determined asynchronously on the client-side, there might be a brief flicker where "Sign In" shows before the state resolves to authenticated. We should ensure the `useAuth` hook exposes a loading state to handle this gracefully if necessary.
