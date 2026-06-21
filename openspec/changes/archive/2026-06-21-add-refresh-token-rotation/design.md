## Context

The current backend uses Spring Boot 4.0.5, Spring Security, JJWT 0.12.5, MVC controllers, and a stateless `JwtFilter`. `POST /api/v1/auth/login` returns one JWT whose configured lifetime is 24 hours, `POST /api/v1/auth/logout` returns `204` without server-side revocation, and Redis is intentionally absent from normal JWT validation. The frontend uses React 19, React Router 7, and one shared Axios 1.16 client; it stores the JWT in `localStorage`, decodes it independently in each `useAuth()` call, and has no startup restoration or `401` renewal path.

This change crosses the controller/service/repository boundary, adds security-sensitive persistence, and centralizes frontend auth state. It must preserve the existing bearer-token contract for roadmap APIs, fit the current package/folder structure, add no dependency, and avoid turning Redis into a runtime requirement. The primary stakeholders are learners who need uninterrupted roadmap sessions and developers maintaining the auth boundary.

## Goals / Non-Goals

**Goals:**

- Use short-lived JWT access tokens for protected API calls and a renewable, revocable browser session for continuity.
- Keep long-lived refresh credentials inaccessible to JavaScript and plaintext out of persistent storage.
- Rotate refresh tokens atomically, detect replay, support independent browser/device sessions, and revoke the current session on logout.
- Protect cookie-backed auth operations against CSRF and restrict credentialed CORS to configured frontend origins.
- Give the React application one startup-aware auth state and one concurrency-safe Axios refresh path.
- Preserve the current `LoginResponse` JSON shape (`token`, `expiresIn`) and existing `/api/v1` route style.

**Non-Goals:**

- OAuth/OIDC, social login, password reset, multi-factor authentication, device-management UI, or “log out all devices”.
- Storing access tokens in cookies or converting protected APIs from bearer auth to server sessions.
- Replacing JJWT, Spring Security, Axios, React Router, JPA, or the current application architecture.
- Making Redis required for authentication, adding a token-cleanup worker, or introducing a new migration framework.
- Redesigning dashboard routing or changing the roadmap progression flow.

## Decisions

### 1. Split the session into a short-lived JWT access token and an opaque refresh token

New access JWTs will default to 15 minutes through `security.jwt.access-expiration-time`; the existing environment secret and JJWT 0.12.5 signing path remain in use. `JwtService` will issue access tokens only and include an access-token type claim so `JwtFilter` can reject any non-access JWT. `LoginResponse` remains `{ "token": string, "expiresIn": number }`, limiting API churn while its Java/TypeScript documentation is clarified to mean access token.

The refresh credential will be at least 256 bits from `SecureRandom`, encoded base64url without padding, and will not be a JWT. An opaque value contains no client-useful claims, avoids a second signing/validation contract, and makes server-side revocation authoritative. Refresh families will have a configurable absolute lifetime, defaulting to 30 days; rotation will not silently extend that absolute limit.

Alternatives considered:

- Keep the 24-hour `localStorage` JWT: smallest change, but leaves a long theft window and cannot support revocation or seamless renewal.
- Use a refresh JWT: possible, but still requires server state for rotation/replay detection and creates unnecessary claim/key complexity.
- Put the access token in an auth cookie: reduces JavaScript exposure but changes every protected endpoint to cookie authentication and expands CSRF scope beyond this MVP change.

### 2. Persist hashed refresh-session records in PostgreSQL

Add `RefreshSession` under `entities/` and `RefreshSessionRepository` under `repositories/`. The `refresh_sessions` table will contain:

| Column | Purpose |
| --- | --- |
| `refresh_session_id` UUID PK | Stable record identity |
| `user_id` UUID FK | Session owner |
| `family_id` UUID, indexed | Independent login/browser session and replay-revocation boundary |
| `token_hash` CHAR(64), unique | SHA-256 digest used for lookup; plaintext is never stored |
| `created_at`, `last_used_at` | Audit and lifecycle timestamps |
| `expires_at` | Fixed family/session absolute expiry |
| `revoked_at`, `revocation_reason` | Rotation, logout, expiry, or replay state |
| `replaced_by_session_id` UUID nullable | Links a consumed token to its successor |
| `version` | Optimistic safety in addition to the locked rotation lookup |

`RefreshSessionService` will generate and digest credentials, issue independent families at login, rotate within a transaction, revoke a family, and reject invalid credentials. The repository rotation lookup will take a database write lock so two requests cannot both consume one active row. Rotation marks the current record revoked as `ROTATED`, inserts one successor with the same `family_id` and `expires_at`, and returns the successor plaintext only to the response-cookie boundary. Presenting a rotated row revokes all active rows in its family before returning `401`.

SHA-256 is appropriate for lookup because the input is uniformly random and high entropy; password hashing would add cost without compensating for guessable input. Expired/revoked records must remain through the family expiry to support replay detection, after which they can be deleted opportunistically by a bounded repository operation during later auth writes. No background worker is required.

PostgreSQL is preferred over Redis because it is already the durable account store, supports transactions/locking, and preserves sessions across cache restarts. Protected resource requests still validate only the JWT and load the user as they do today; they never query `refresh_sessions`.

### 3. Keep controllers thin and make the auth contracts explicit

The endpoints will be:

| Endpoint | Authentication and input | Success | Failure |
| --- | --- | --- | --- |
| `GET /api/v1/auth/csrf` | Public, credentialed request | `200` CSRF header name/token material and CSRF cookie | Standard CORS rejection |
| `POST /api/v1/auth/login` | Existing JSON credentials plus CSRF header | `200 LoginResponse` plus refresh cookie | `400` validation, `401` credentials, `403` CSRF |
| `POST /api/v1/auth/refresh` | Refresh cookie plus CSRF header; no token in body | `200 LoginResponse` plus rotated cookie | `401` refresh failure, `403` CSRF |
| `POST /api/v1/auth/logout` | Refresh cookie when present plus CSRF header | Idempotent `204`, revoked family when resolvable, cleared cookie | `403` CSRF |

`AuthenticationService` remains responsible for credential authentication. A refresh-session service owns session business rules, while a small auth-session orchestration method combines access issuance and refresh issuance. The controller only validates/binds transport data, delegates, and writes/clears the cookie. An explicit internal result object can carry `LoginResponse` plus the one-time raw refresh value without adding it to a public response DTO.

`SecurityConfig` will permit `csrf`, `login`, `refresh`, and `logout` at the authorization layer, because their credential/CSRF checks happen at the auth boundary; all other endpoints remain bearer-protected. Missing or invalid refresh credentials still return `401`, not an anonymous successful refresh. Logout is intentionally access-token independent so an expired access JWT cannot prevent revocation.

### 4. Harden refresh cookies, CSRF, and CORS with environment-backed configuration

The refresh cookie will be host-only (no `Domain`), `HttpOnly`, scoped to `/api/v1/auth`, assigned an explicit `SameSite` value, and have `Max-Age` equal to the remaining absolute family lifetime. A rotated cookie must therefore use `max(0, familyExpiresAt - now)`, never the full configured refresh lifetime. Production-like profiles require `Secure=true`; local HTTP development may explicitly override it. `SameSite=Lax` is the preferred same-site default. If production keeps the SRS-described cross-site frontend/backend deployment, configuration must use `SameSite=None` together with `Secure=true` and the CSRF controls below.

Spring Security 7-compatible CSRF support will protect `login`, `refresh`, and `logout`. `GET /auth/csrf` returns the server-generated token to the allowlisted frontend so it can send the configured header; the refresh credential remains `HttpOnly` and is never returned. Bearer-only application endpoints remain outside cookie-auth CSRF matching because browsers do not attach their `Authorization` header automatically.

The CORS configuration will move allowed frontend origins to configuration, enable credentials, allow the CSRF header, and never combine credentials with `*`. Axios will use `withCredentials` for the auth boundary. Login protection matters as well as refresh/logout protection because an attacker must not be able to bind a victim browser to the attacker's session.

Relevant guidance: [RFC 9700 refresh-token rotation and replay detection](https://www.rfc-editor.org/rfc/rfc9700), [OWASP Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html), and [Spring Security 7 SPA CSRF guidance](https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html).

### 5. Centralize frontend auth state and keep the access token in memory

Add one `AuthProvider` within `features/auth/` and change `useAuth` into a context consumer. The provider wraps the routed application, exposes `user`, `isLoading`, `login`, and `logout`, and performs one startup refresh before declaring the session guest or authenticated. JWT decoding remains a presentation convenience for the username/expiry; it is not treated as client-side authorization.

Add a small app-wide in-memory access-token store under `src/frontend/src/services/` so the shared Axios service can read/update the token without importing a feature module or creating a React dependency cycle. No access or refresh credential is written to `localStorage` or `sessionStorage`. On migration, the provider removes the legacy `access_token` key.

The auth service obtains CSRF material when required, then calls login/refresh/logout with credentials enabled. Startup restoration and React 19 development `StrictMode` share the same module-level refresh promise, preventing the double effect pass from rotating twice.

Alternatives considered:

- Keep access JWTs in `localStorage`: simpler reload behavior, but any XSS can exfiltrate the token and the new refresh flow makes persistence unnecessary.
- Put auth state in Redux/Zustand: prohibited scope expansion; React context and a small transport store cover the current consumers.

### 6. Use one retry and one refresh coordinator in Axios

The existing Axios instance remains the only general API client. Its request interceptor reads the memory token and attaches `Authorization`. Its response interceptor handles eligible `401` responses only when the request has not been retried and the normalized pathname is not one of the auth paths exported from centralized endpoint constants; loose substring matching is forbidden. A shared promise ensures concurrent failures in one tab await the same refresh. Successful renewal updates memory and retries each original request once; failure clears auth state and rejects all waiters without recursion.

To avoid legitimate multi-tab refreshes being mistaken for replay, the coordinator will use the browser Web Locks API under a stable lock name when available. After acquiring the cross-tab lock, it performs the refresh using the browser's latest cookie. A `BroadcastChannel` carries only session-ended/login state signals—never token values—so logout in one tab promptly clears other tabs. The backend transaction remains the final concurrency authority. Browsers without Web Locks retain strict replay behavior; this limitation is documented rather than weakening server replay detection.

### 7. Configuration and tests follow the existing versions and patterns

Add typed backend configuration properties for access lifetime, refresh lifetime, refresh-cookie name/path/secure/same-site, and allowed frontend origins, backed by environment variables in `.env.example`. `FRONTEND_ALLOWED_ORIGINS` is required: Java and `application.properties` must not supply an origin fallback, while test properties may define an explicit isolated fixture. Do not hard-code secrets or real domains. Keep Spring MVC/Jakarta APIs compatible with Boot 4 and use the existing JUnit 6, RestTestClient, Mockito, and H2 test patterns.

Backend tests cover digest-only persistence, independent families, locked rotation, expiry, replay-family revocation, logout idempotency, cookie flags, CSRF, CORS, and unchanged stateless access-JWT validation. The repository/service concurrency test should use the closest practical integration level supported by the current test setup. The frontend has no test runner dependency, so verification uses `npm run build`, `npm run lint`, and focused browser checks for startup, concurrent `401`s, refresh failure, and logout; adding a new frontend test framework is outside scope.

## Risks / Trade-offs

- [Strict rotation can treat simultaneous multi-tab use as replay] → Serialize refresh with a shared in-tab promise and Web Locks across tabs; retain atomic backend rotation and prefer a safe re-login over accepting replay.
- [Cross-site cookies can be blocked by browser privacy controls] → Prefer same-site custom frontend/API domains; keep `SameSite` configurable and verify the production topology before deployment.
- [Cookie auth introduces CSRF exposure] → Protect login/refresh/logout with Spring Security CSRF tokens, exact credentialed CORS origins, explicit SameSite, and transport-level tests.
- [In-memory access tokens disappear on reload] → Bootstrap through the `HttpOnly` refresh cookie and show the existing auth loading state until restoration completes.
- [XSS can still act as the user while code is running] → Limit access-token lifetime, keep refresh material `HttpOnly`, avoid persistent access storage, and continue normal output-encoding/CSP work separately.
- [Refresh rows accumulate] → Index hash/family/expiry fields and opportunistically remove families only after replay-detection retention is no longer needed.
- [A database outage prevents renewal] → Existing unexpired JWTs continue to work; renewal fails closed and does not fall back to an unverifiable credential.
- [Backend-first deployment can reject the old login client once CSRF is enforced] → Deploy backend and frontend as a coordinated change or stage CSRF enforcement behind configuration only for the migration window.

## Migration Plan

1. Add the refresh-session entity/repository and configuration with safe defaults; let the current JPA schema mechanism create the table in local/MVP environments, and document the equivalent DDL/ERD change for managed environments.
2. Add backend CSRF bootstrap, refresh, cookie issuance, rotation, and idempotent logout while preserving the existing access-token response fields and JWT validation path.
3. Deploy the frontend provider, memory store, CSRF-aware auth service, and Axios coordinator together with CSRF enforcement. Remove the legacy `localStorage.access_token` value during startup.
4. Verify login → roadmap access → forced access expiry → transparent refresh → logout, including concurrent request and replay cases, before shortening the configured access lifetime in shared environments.
5. Expect users who logged in before refresh cookies existed to sign in once after the frontend migration; existing bearer tokens can continue working until their original expiry during the deployment window.

Rollback: restore the previous frontend token handling and backend auth controller/security configuration. The additive `refresh_sessions` table can remain unused during rollback, and the new refresh cookie can be explicitly cleared; no existing user or roadmap data is rewritten. If rollback occurs after a security incident, revoke affected refresh families before disabling the new endpoints.

## Open Questions

- What exact production frontend and API origins will be used? This decides whether the production cookie can use `SameSite=Lax` or must use `SameSite=None; Secure`; the implementation must not guess from the placeholder CORS domains.
- Will production continue relying on Hibernate schema updates or adopt an explicit migration tool later? This change documents the table and stays within the existing JPA mechanism unless a migration framework is separately approved.
