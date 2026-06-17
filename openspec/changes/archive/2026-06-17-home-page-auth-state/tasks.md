## 1. UI Updates

- [x] 1.1 Locate the Home/Landing page component (e.g., `src/frontend/src/pages/Landing/LandingPage.tsx` or similar) and import the `useAuth` hook from `src/frontend/src/hooks/useAuth.ts`.
- [x] 1.2 Update the navigation bar/header of the Home page to check the `user` state. If `user` exists, render a "Dashboard" link and/or User Avatar. If not, render "Sign In" and "Sign Up" links.
- [x] 1.3 Update the main Hero section Call-To-Action (CTA) buttons to check the `user` state. If `user` exists, direct them to the Dashboard.

## 2. Verification

- [x] 2.1 Test the Home page while logged out: ensure "Sign In" and "Sign Up" buttons are visible and functional.
- [x] 2.2 Test the Home page while logged in: ensure "Sign In" and "Sign Up" are hidden, and a "Dashboard" link is visible.
- [x] 2.3 Run `npm run lint` and `npm run build` in `src/frontend` to ensure code quality.
