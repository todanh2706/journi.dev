## Context

The dashboard already uses a persistent `DashboardLayout`, nested React Router routes, and a feature-owned `Sidebar`. The sidebar reads a username from the JWT through `useAuth`, but its avatar block is not a link. The auth hook already contains a synchronous local-only logout helper, while the backend exposes only signup/login under `/api/v1/auth/**` and treats every auth endpoint as public. Authentication is stateless and uses a locally stored access token without a server-side session store.

The backend user model currently stores username and email but not a display name or other personal-profile fields. It exposes ID-based user CRUD endpoints, but those endpoints are not an appropriate self-service contract because the JWT contains only the username and the controller does not enforce ownership by user ID. This change therefore separates the usable logout slice from profile-editing UI scaffolding rather than broadening the backend user domain.

## Goals / Non-Goals

**Goals:**

- Add an authenticated dashboard profile destination reached from the sidebar avatar block.
- Present account identity, personal-information, username/email, password, and session controls in the established dark dashboard style.
- Make unsupported edit controls honest boilerplate that cannot accidentally call administrative ID-based APIs.
- Require explicit confirmation before logout and provide visible pending and failure states.
- Add an authenticated logout boundary that lets the frontend end its local stateless JWT session safely.
- Keep JWT validation independent of Redis availability so roadmap and other protected APIs remain available when cache infrastructure is unavailable.
- Preserve existing dependency versions, route layout, API base URL handling, and frontend/backend folder conventions.

**Non-Goals:**

- Persisting profile, username, email, display-name, or password changes in this change.
- Adding a display-name column, avatar uploads, account deletion, refresh tokens, device/session management, or logout-all-devices.
- Reworking global authentication state, protecting every dashboard route, or replacing JWT authentication.
- Changing the roadmap learning flow or introducing a new frontend state-management or modal library.

## Decisions

1. **Use `/dashboard/profile` as a nested dashboard route.**
   - `App.tsx` will add `profile` under the existing `/dashboard` parent so the sidebar stays mounted and the current outlet transition continues to apply.
   - The route-level screen will live under `pages/Dashboard`, while profile-owned UI stays under `features/profile`. This follows the repository's page-to-feature dependency direction.
   - The authenticated sidebar identity block will become a React Router link. A guest identity block will not expose the profile route, and a direct unauthenticated profile visit will redirect to `/signin`.

2. **Keep profile editing as explicitly unavailable UI scaffolding.**
   - The page will show the existing JWT-derived username plus grouped controls for personal details, account identifiers, and password changes.
   - Fields whose values cannot be sourced safely will remain empty or explanatory; the UI will not invent email/display-name values.
   - Save actions will be disabled or accompanied by a clear unavailable notice and will not call `PUT /api/v1/users/{id}`. Adding self-service `GET/PATCH /api/v1/users/me` and password verification is deferred to a separate vertical slice.
   - Alternative considered: reuse the current user CRUD endpoint. This was rejected because the client has no trusted user ID and the endpoint is not ownership-scoped.

3. **Use a profile-owned confirmation dialog for logout.**
   - The logout action is visually isolated as a destructive session control. Activating it opens an accessible modal with confirm/cancel actions; Escape and cancellation close it without changing auth state.
   - Confirmation calls one async logout operation from the auth feature. While pending, duplicate submissions are blocked. On success, the access token is removed and React Router navigates to `/signin` with history replacement. On failure, the dialog remains available with a retryable error and the local token is retained.
   - The backend endpoint provides the requested authenticated API boundary while the frontend remains responsible for ending the stateless client session.

4. **Keep logout stateless and cache-independent.**
   - `POST /api/v1/auth/logout` will require a valid bearer token and return `204 No Content` without creating server-side session or denylist state.
   - After the successful response, the frontend removes `access_token`; subsequent app requests no longer carry the JWT.
   - `JwtFilter` will continue to validate signed JWTs using only the existing user lookup and will not contact Redis. This prevents optional cache infrastructure from becoming a single point of failure for roadmap and progress APIs.
   - Alternative considered: a Redis token denylist. This was rejected for the MVP because it couples every protected request to cache availability. Strong server-side invalidation should be proposed with refresh tokens or a first-class session model.

5. **Narrow public authentication routes.**
   - Security configuration will permit only signup/login (and preflight handling as already configured) rather than all `/api/v1/auth/**`, ensuring logout is authenticated.
   - The controller remains thin and returns `204 No Content`; Spring Security performs authentication before the method is invoked.

6. **Verify at the contract boundaries.**
   - Backend tests will cover a successful logout response, unauthenticated rejection, and active JWT authentication without a cache dependency.
   - Existing authentication tests will be updated for changed constructor dependencies or security matchers.
   - Frontend verification will use the existing TypeScript build and ESLint scripts because no frontend test framework is installed.

## Risks / Trade-offs

- **[Risk] A copied JWT remains cryptographically valid until its normal expiration after client logout.** → Keep the current access-token lifetime bounded, never expose the token, and treat server-side invalidation as a future session-management capability rather than coupling all MVP requests to Redis.
- **[Risk] A failed logout request leaves the local session active.** → Keep the dialog open with a concise error and retry path; do not falsely report success or discard the token before the authenticated logout boundary responds.
- **[Risk] Boilerplate edit controls may look functional.** → Label unavailable persistence clearly and disable unsupported save actions; keep logout as the only fully enabled account mutation in this change.
- **[Trade-off] Logout is client-scoped.** → Other tokens for the same account remain active; logout-all-devices and immediate server-side invalidation are intentionally deferred.

## Migration Plan

1. Deploy the narrowed security matcher, cache-independent logout endpoint, and regression tests.
2. Deploy the frontend profile route, sidebar link, profile scaffolding, confirmation dialog, and async auth logout integration.
3. There is no data migration, Redis state, or cleanup job.
4. Rollback removes the frontend route/action and backend logout endpoint.

## Open Questions

- None for this change. Persisted profile edits, password changes, refresh tokens, and server-side session invalidation should be proposed separately when their validation rules are defined.
