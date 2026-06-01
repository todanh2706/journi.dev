## 1. Signup Contract Alignment

- [x] 1.1 Replace the `name` field in `src/frontend/src/pages/Auth/SignUp.tsx` with a `username` field and update the visible label, placeholder, and disabled-state checks to match the backend request DTO.
- [x] 1.2 Add a typed signup API helper in `src/frontend/src/pages/Auth/services/` that posts `username`, `email`, and `password` to `/api/v1/auth/signup` through the shared Axios client.

## 2. Sign-Up Submission UX

- [x] 2.1 Update `SignUp.tsx` submit handling to call the signup service instead of logging to the console and to manage loading, success, and failure state.
- [x] 2.2 Surface duplicate username/email failures and generic request failures in the UI without storing an auth token or treating signup as a login.

## 3. Verification

- [x] 3.1 Verify the sign-up flow against the backend contract by testing successful account creation and duplicate-credential failure handling from the frontend.
- [x] 3.2 Update any related frontend auth-flow documentation or notes if implementation details change from the current placeholder behavior.
