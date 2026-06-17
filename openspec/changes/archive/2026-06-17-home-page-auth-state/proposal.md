## Why

Currently, when a logged-in user visits the Home page, they still see the "Sign In" and "Sign Up" buttons. This creates a confusing and poor user experience, as it gives the impression that they are not authenticated. We need to conditionally render these actions based on the user's authentication state to provide a seamless transition back to the application.

## What Changes

- Update the Home page navigation bar and hero section to check the user's authentication state.
- If the user is **unauthenticated**, display the existing "Sign In" and "Sign Up" buttons.
- If the user is **authenticated**, hide the "Sign In" and "Sign Up" buttons and replace them with a "Go to Dashboard" button and/or the User's avatar.

## Capabilities

### New Capabilities

- `home-page-auth-state`: The home page dynamically adapts its call-to-action buttons based on the user's authentication status.

### Modified Capabilities

- 

## Impact

- **Frontend**: The `HomePage` component and its navigation/header components will be updated. They will need to consume the `useAuth` hook to determine the current user state.
- **Backend**: No impact. This is purely a frontend UI/UX improvement.
