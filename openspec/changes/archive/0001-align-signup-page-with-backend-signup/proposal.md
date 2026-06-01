## Why

The current `/signup` page is still a presentation-only form that logs local state and uses a `name` field that does not match the backend signup contract. This leaves the product with a visible registration journey that cannot create accounts and does not reflect the backend's duplicate-validation and sanitized-response behavior.

## What Changes

- Replace the sign-up page's placeholder submit flow with a real call to `POST /api/v1/auth/signup`.
- Align the form payload with the backend request contract by collecting `username`, `email`, and `password`.
- Add client-side submit states for loading, success, and backend validation failures so duplicate username and duplicate email errors can be surfaced to the user.
- Define the post-signup UX around the current backend response shape, which returns a sanitized `UserResponse` rather than a login token.
- Keep sign-in, social buttons, and backend signup semantics unchanged in this change.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `authentication-and-user-management`: Extend the documented authentication flow so the frontend sign-up journey submits the backend-owned signup contract and handles duplicate-validation responses.
- `frontend-experience`: Change the sign-up screen from a local placeholder form into a backend-connected account-creation flow that uses `username`, preserves terms gating, and communicates request outcomes.

## Impact

- Affected frontend code in `src/frontend/src/pages/Auth/SignUp.tsx` and `src/frontend/src/pages/Auth/services/`.
- Uses the existing Axios client and backend endpoint `POST /api/v1/auth/signup`.
- No database schema or backend endpoint changes are required for this proposal.
