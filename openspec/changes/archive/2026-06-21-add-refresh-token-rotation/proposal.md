## Why

Journi.dev currently stores a 24-hour bearer JWT in `localStorage`; expired tokens immediately end the frontend session, and logout cannot revoke any server-side credential. Learners need a safer, durable sign-in session that can renew short-lived access without exposing a long-lived refresh credential to JavaScript.

## What Changes

- Add a refresh-session lifecycle to login, refresh, and logout while keeping protected application APIs authenticated by short-lived bearer access tokens.
- Issue the refresh credential as a hardened `HttpOnly` cookie and store only a one-way token digest plus session metadata in PostgreSQL.
- Rotate the refresh credential on every successful renewal, reject expired or revoked credentials, and revoke the token family when replay is detected.
- Replace `localStorage` access-token persistence with a centralized in-memory frontend auth session that restores itself through the refresh endpoint on application startup.
- Extend the shared Axios client to send credentials to auth endpoints, attach the current access token, coordinate one refresh request for concurrent `401` responses, retry eligible requests once, and end the local session when renewal fails.
- Protect cookie-backed auth mutations with exact-origin/CORS controls and CSRF-resistant request handling; keep development cookie flags configurable without weakening production defaults.
- Add focused backend and frontend verification for issuance, rotation, replay rejection, logout revocation, startup restoration, retry behavior, and failure cleanup.

## Capabilities

### New Capabilities
- `refresh-token-session-management`: Defines refresh-session issuance, secure cookie transport, persistence, rotation, replay detection, expiry, and revocation behavior.

### Modified Capabilities
- `authentication-and-user-management`: Changes login/logout and the shared frontend API client from a long-lived `localStorage` JWT session to short-lived access tokens backed by a renewable server-side refresh session.

## Impact

- **Frontend:** `src/frontend/src/services/axios.tsx`, auth services/types/hooks, application provider composition, sign-in/logout flows, and auth-aware screens.
- **Backend:** authentication controller/service, JWT service/configuration, Spring Security and CORS/CSRF configuration, new refresh-session entity/repository/service/DTOs, and auth/security tests.
- **API:** `POST /api/v1/auth/login` additionally sets a refresh cookie; `POST /api/v1/auth/refresh` is added; `POST /api/v1/auth/logout` revokes the refresh session and clears its cookie. The access-token response remains aligned across backend and frontend.
- **Database:** PostgreSQL gains a refresh-session table containing token digests and rotation/revocation metadata; plaintext refresh credentials are never persisted.
- **Configuration/docs:** access/refresh lifetimes, cookie security, and allowed frontend origins become environment-backed settings; `docs/ERD.md`, environment examples, and relevant auth documentation require updates.
- **Dependencies:** uses the existing Spring Boot 4, Spring Security, JJWT, JPA/PostgreSQL, React 19, and Axios stack; no new framework or Redis dependency is introduced.
