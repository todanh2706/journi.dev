## 1. Backend Stateless Logout

- [x] 1.1 Add an authenticated `POST /api/v1/auth/logout` endpoint that returns `204 No Content` for a valid JWT session.
- [x] 1.2 Narrow `SecurityConfig` public matchers to signup and login so the logout route requires authentication.
- [x] 1.3 Keep `JwtFilter` independent of Redis and other cache infrastructure so protected roadmap requests retain the existing validation flow.

## 2. Configuration and Backend Verification

- [x] 2.1 Correct the Docker Compose Redis hostname source to use the existing `cache` service name.
- [x] 2.2 Add regression coverage for successful logout, unauthenticated rejection, and active JWT authentication without Redis, then run `./mvnw test`.

## 3. Frontend Auth Integration

- [x] 3.1 Add a typed logout request to the existing auth service using the shared Axios instance and `POST /auth/logout`.
- [x] 3.2 Convert the auth hook logout operation to an async flow that clears `access_token` and local user state only after the backend succeeds, while allowing callers to render failures.

## 4. Profile Route and UI

- [x] 4.1 Register `profile` as a nested route under `/dashboard` and add a route-level profile page that redirects unauthenticated direct visits to `/signin`.
- [x] 4.2 Add profile-owned components under `features/profile` for the identity header, grouped account-information boilerplate, password boilerplate, and session/logout section using existing Tailwind and Lucide conventions.
- [x] 4.3 Render the JWT-derived username without fabricating unavailable display-name or email values, and clearly disable or label profile-save actions that have no backend contract.
- [x] 4.4 Make the authenticated avatar/identity block in the existing sidebar navigate to `/dashboard/profile`, add an appropriate profile active state, and keep the guest block non-navigable.

## 5. Confirmed Logout Experience

- [x] 5.1 Add an accessible profile-owned logout confirmation dialog with confirm, cancel, overlay, and Escape dismissal behavior.
- [x] 5.2 Connect confirmation to the auth hook, disable duplicate submission while pending, retain the session and show a retryable message on failure, and navigate to `/signin` with replacement on success.

## 6. Frontend Verification

- [x] 6.1 Run `npm run build` and `npm run lint` from `src/frontend`, fixing TypeScript, route, accessibility, and unused-code issues without adding dependencies.
- [ ] 6.2 Manually verify authenticated sidebar navigation, direct unauthenticated profile redirection, honest disabled edit controls, dialog cancellation, logout failure feedback, successful token removal, and roadmap availability without Redis.
