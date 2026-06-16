## Why

The current Dashboard page uses mock data for user information, regardless of whether the user is logged in or not. To provide a personalized and secure experience, the dashboard needs to dynamically reflect the user's actual authentication state, displaying their real name when logged in and a distinct "not logged in" state when they are not. Additionally, the sign-in flow should seamlessly redirect users to the dashboard upon successful authentication.

## What Changes

- Modify the Dashboard's user-related components (like the Sidebar avatar) to depend on the global authentication state instead of static mock data.
- Introduce a "not logged in" visual state for user-related components on the Dashboard when no valid session/token exists.
- Fetch and display the authenticated user's real data (e.g., username) on the Dashboard when logged in.
- Update the Sign In page logic to redirect the user to `/dashboard` immediately after a successful login.
- Ensure the existing overall UI layout and design of the Dashboard remain unchanged.

## Capabilities

### New Capabilities

- `dashboard-auth-state`: Dynamically rendering user information and states on the Dashboard based on authentication status.

### Modified Capabilities

- `authentication-and-user-management`: Modifying the sign-in flow to include a post-login redirect to the dashboard.

## Impact

- **Frontend Routing:** Update `SignIn.tsx` or related auth service to navigate to `/dashboard` upon success.
- **Frontend State Management:** Utilize an existing mechanism (e.g., Context, Zustand, or `localStorage` checks) to determine auth state and user profile data.
- **Frontend Components:** Modify `Dashboard.tsx` and its child components (like Sidebar) to conditionally render a "not logged in" UI or actual user data.
- **API Integration:** Parse the JWT token or utilize the login response to retrieve the logged-in user's details.
