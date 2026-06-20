## Why

Authenticated users can see their avatar in the dashboard sidebar, but it is not interactive and there is no account-management destination or safe, confirmed way to end a session. Adding a focused profile surface completes a basic account workflow without expanding beyond the MVP learning experience.

## What Changes

- Make the authenticated-user block at the bottom of the dashboard sidebar navigate to the nested `/dashboard/profile` route.
- Add a profile page that matches the existing dark dashboard UI and presents account identity, personal-information, username/email, and password-editing sections.
- Keep profile editing as explicit frontend boilerplate where the current backend has no self-service profile API; unsupported save actions will not call the existing ID-based administrative user endpoints or imply that data was persisted.
- Add a destructive logout action with an accessible confirmation dialog, cancellation path, loading/error states, and redirect to `/signin` only after logout succeeds.
- Add an authenticated `POST /api/v1/auth/logout` endpoint that confirms the session boundary before the frontend removes its locally stored JWT.
- Centralize frontend logout API and token-cleanup behavior in the existing auth feature rather than duplicating it in the profile page.

## Capabilities

### New Capabilities

- `user-account-profile`: Dashboard profile navigation, account-management UI scaffolding, and the confirmed logout experience.

### Modified Capabilities

- `authentication-and-user-management`: Add an authenticated stateless logout contract to the existing JWT authentication flow.

## Impact

- Frontend routing, dashboard sidebar identity block, auth hook/service, and a new route-level profile page with profile-owned UI components.
- Backend authentication controller and security route rules; the JWT filter remains independent of Redis and other cache infrastructure.
- API addition: `POST /api/v1/auth/logout`; no existing API response is renamed or removed.
- No user-table or profile-field schema change, no new frontend dependency, and no change to the roadmap learning flow.
