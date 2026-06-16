## Context

The current `Dashboard.tsx` uses mock data for the user profile, rendering the same UI regardless of authentication state. The user has requested to make the Dashboard dynamically aware of the login state without modifying the core UI layout. Additionally, the `SignIn.tsx` page currently does not redirect the user to `/dashboard` upon a successful sign-in.

## Goals / Non-Goals

**Goals:**
- Make user-related components (like the sidebar avatar) reflect the actual login state (logged in vs. logged out).
- Retrieve and display the authenticated user's details on the Dashboard.
- Redirect the user from `/signin` to `/dashboard` upon a successful login.

**Non-Goals:**
- Do not redesign or alter the existing Dashboard layout.
- Do not implement new Backend API endpoints for this change (use existing ones or parse JWT).
- Do not implement route protection (blocking access to `/dashboard` for unauthenticated users) since the requirement specifies handling the "not logged in" state on the Dashboard itself.

## Decisions

1. **Authentication State Management:**
   - Use `localStorage` to check for the presence of the `access_token`.
   - Extract user information (like `username`) by decoding the JWT payload directly on the frontend. This avoids needing a separate `/api/v1/auth/me` call if the JWT already contains the necessary claims (the subject/sub claim is usually the username).
   - A custom React Hook (e.g., `useAuth`) will be created to encapsulate the token parsing and state check logic, making it reusable.

2. **Sign-In Redirect:**
   - In `SignIn.tsx`, upon a successful API response from the `signin` service, use React Router's `useNavigate` hook to programmatically route the user to `/dashboard`.

3. **Dashboard UI Conditonal Rendering:**
   - Introduce a state variable `user` from the `useAuth` hook in `Dashboard.tsx`.
   - When `user` is null, render default placeholders (e.g., "Guest", default avatar).
   - When `user` is present, render the actual `username` extracted from the token.

## Risks / Trade-offs

- **Risk:** Parsing JWT on the frontend could be vulnerable if the token signature is not validated. 
  → **Mitigation:** The token signature validation is done on the backend. The frontend only decodes it for display purposes. If the token is invalid or expired, subsequent backend API calls will fail (401), at which point the frontend can clear the token and revert to the "not logged in" state.
- **Risk:** The JWT might not contain the user's full name, only the username.
  → **Mitigation:** Based on the current backend `JwtService`, the subject (`Claims::getSubject`) contains the username. This is sufficient for display on the Dashboard.
