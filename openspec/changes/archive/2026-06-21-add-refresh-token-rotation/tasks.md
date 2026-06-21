## 1. Backend Configuration and Persistence

- [x] 1.1 Add typed auth-session configuration for access-token lifetime, absolute refresh lifetime, refresh-cookie name/path/secure/same-site, and credentialed frontend origin allowlist; require `FRONTEND_ALLOWED_ORIGINS` without a source-code fallback and keep test origins isolated in test properties.
- [x] 1.2 Add the `RefreshSession` JPA entity and revocation-reason model with UUID identity, user/family links, unique SHA-256 token digest, lifecycle timestamps, successor link, expiry, indexes, and optimistic versioning.
- [x] 1.3 Add `RefreshSessionRepository` queries for digest lookup with explicit pessimistic write locking, active-family revocation, and bounded cleanup after family expiry; document that H2 locking verification is best-effort and PostgreSQL verification is required before production rollout.
- [x] 1.4 Add repository persistence tests proving the digest is unique, independent login families coexist, family rows can be revoked together, and locked lookup works with the current JPA/H2 test stack.

## 2. Backend Token and Session Services

- [x] 2.1 Update `JwtService` to use the configured short access-token lifetime, emit an access-token type claim, preserve `LoginResponse.token`/`expiresIn`, and reject non-access JWTs in normal access validation.
- [x] 2.2 Implement `RefreshSessionService` issuance with `SecureRandom`, base64url encoding, SHA-256 digest-only persistence, a fixed family expiry, and independent families per successful login.
- [x] 2.3 Implement transactional refresh rotation with locked consumption, one successor, preserved family expiry, expiry rejection, and replay-triggered revocation of all active family records.
- [x] 2.4 Implement current-family logout revocation and idempotent handling for missing, unknown, expired, or already-revoked refresh credentials.
- [x] 2.5 Add an auth-session orchestration result/service that combines the public access-token response with the one-time refresh value while keeping raw refresh credentials out of response DTOs and logs.
- [x] 2.6 Add service tests for issuance, digest-only storage, fixed expiry, independent families, rotation, concurrent consumption, expired credentials, replay-family revocation, and logout.

## 3. Backend HTTP and Security Boundary

- [x] 3.1 Add a refresh-cookie helper that emits and clears a host-only `HttpOnly` cookie with configured path, `SameSite`, and profile-appropriate `Secure` behavior; set rotation `Max-Age` from the remaining absolute family lifetime rather than the configured full lifetime.
- [x] 3.2 Refactor `AuthenticationController` into thin login, refresh, and idempotent logout handlers that delegate session rules, return the existing access-token JSON contract, and write or clear refresh cookies.
- [x] 3.3 Add `GET /api/v1/auth/csrf` and configure Spring Security 7-compatible CSRF validation for login, refresh, and logout without imposing cookie CSRF checks on bearer-only resource endpoints.
- [x] 3.4 Update `SecurityConfig` authorization so CSRF bootstrap, login, refresh, and logout reach their auth-boundary validation while every existing protected endpoint remains bearer-authenticated.
- [x] 3.5 Replace placeholder CORS origins with the configured exact allowlist, enable credentials, allow the CSRF header, and prohibit wildcard credentialed origins.
- [x] 3.6 Extend exception handling so refresh failures consistently return `401`, CSRF failures return `403`, no internal token details leak, and failed refresh responses clear the stale refresh cookie where applicable.
- [x] 3.7 Update controller/security tests for login cookie issuance, refresh rotation response, invalid/expired/replayed refresh rejection, idempotent logout without a bearer token, cookie clearing, CSRF enforcement, and credentialed CORS allow/deny behavior.
- [x] 3.8 Update JWT filter tests to cover access-token type validation and confirm valid protected requests still avoid refresh persistence and Redis.

## 4. Frontend Auth Transport

- [x] 4.1 Add an app-wide in-memory access-token store and session-event hooks under shared services so Axios and React auth code can coordinate without a shared-to-feature dependency cycle.
- [x] 4.2 Extend auth endpoint constants, request/response types, and service functions to bootstrap frontend-readable CSRF material and call login, refresh, and logout with credentials enabled while never reading or returning the `HttpOnly` refresh cookie.
- [x] 4.3 Update the shared Axios request interceptor to read the in-memory access token instead of `localStorage` and attach the bearer header without changing the configured API base URL.
- [x] 4.4 Add an Axios response interceptor that excludes auth endpoints by centralized constants and normalized path equality (not substring matching), marks one retry, shares one in-flight refresh across concurrent `401` responses, retries eligible requests once, and clears the session on renewal failure without recursion.
- [x] 4.5 Add cross-tab refresh serialization with Web Locks when available and `BroadcastChannel` session-ended notifications that never transmit access or refresh token values.

## 5. Frontend Global Session Integration

- [x] 5.1 Introduce one `AuthProvider` and convert `useAuth` into a context consumer exposing `user`, `isLoading`, `login`, and `logout` from a single application-wide session state.
- [x] 5.2 Implement startup restoration through the shared refresh coordinator, make it idempotent under React 19 `StrictMode`, decode access claims only for display state, and remove the legacy `localStorage.access_token` key.
- [x] 5.3 Wrap the routed application with `AuthProvider` in the existing React Router composition without redesigning or newly restricting dashboard routes.
- [x] 5.4 Update `SignIn` to enter the provider session instead of writing `localStorage`, then preserve the existing successful `/dashboard` redirect and error states.
- [x] 5.5 Update profile logout and all current auth-aware screens/components to consume the provider loading/auth state; clear local state even if server logout is unreachable while handling the revocation failure deliberately.

## 6. Verification and Documentation

- [x] 6.1 Run the backend test suite with `./mvnw test` (or `mvn test` only if the wrapper is unavailable) and fix auth/session regressions.
- [x] 6.2 Run `npm run build` and `npm run lint` for the frontend and fix TypeScript, React 19, Axios 1.16, and React Router 7 compatibility issues without adding a test framework.
- [x] 6.3 Perform focused browser verification for fresh login, application reload restoration, protected roadmap calls, forced access expiry, concurrent `401` recovery, refresh failure cleanup, multi-tab logout, and re-login after replay revocation.
- [x] 6.4 Update `docs/ERD.md` with `refresh_sessions`, update README/auth documentation with the new session flow, and document non-secret environment variables in the existing `.env.example` files.
- [x] 6.5 Review the final frontend/backend contracts together to confirm refresh tokens never appear in JSON, JavaScript storage, logs, or API error details and that the MVP login-to-roadmap flow remains intact.
