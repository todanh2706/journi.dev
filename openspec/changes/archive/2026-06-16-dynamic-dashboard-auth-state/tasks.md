## 1. Authentication State Management

- [x] 1.1 Create a custom hook (e.g., `useAuth.tsx` in `src/frontend/src/hooks/`) to check for the `access_token` in `localStorage` and decode the JWT payload.
- [x] 1.2 Implement logic in the hook to return the current user (e.g., extracting the subject/username from the decoded token) or `null` if the token is missing/invalid.

## 2. Sign-In Redirect

- [x] 2.1 Update `SignIn.tsx` to use the `useNavigate` hook from `react-router-dom`.
- [x] 2.2 Modify the successful sign-in handler in `SignIn.tsx` to programmatically navigate the user to `/dashboard` after the `access_token` is stored.

## 3. Dashboard UI Update

- [x] 3.1 Update `Dashboard.tsx` (and related components like Sidebar) to consume the `useAuth` hook.
- [x] 3.2 Conditionally render the user avatar and name based on the authenticated user. If the user is `null`, display a "Guest" or "Not logged in" placeholder.
- [x] 3.3 Ensure no existing layout or design on the Dashboard breaks due to these changes.

## 4. Verification

- [x] 4.1 Verify that visiting `/dashboard` while logged out displays the guest/placeholder view.
- [x] 4.2 Verify that signing in successfully redirects the user to `/dashboard`.
- [x] 4.3 Verify that the Dashboard correctly displays the real username extracted from the JWT token when logged in.
