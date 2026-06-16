## Context

The backend signup endpoint already expects `UserRequest` with `username`, `email`, and `password`, creates an enabled `USER` account, rejects duplicate username or email with `400 Bad Request`, and returns a sanitized `UserResponse`. The frontend `/signup` screen still collects `name`, logs form values locally, and has no request, loading, or error handling. This creates a contract mismatch and makes the public registration journey non-functional.

## Goals / Non-Goals

**Goals:**
- Make the sign-up page submit the exact payload shape expected by the current backend signup endpoint.
- Preserve the current UI shell while replacing placeholder submit logic with a real API request.
- Surface backend validation failures in the page so users understand duplicate username or email rejections.
- Provide a success path that fits the backend's current behavior of returning a created user record without issuing a JWT.

**Non-Goals:**
- Changing backend signup semantics, response models, or duplicate-validation rules.
- Implementing automatic sign-in after signup.
- Implementing social login, email verification, password-strength rules, or forgot-password flows.
- Redesigning the sign-in page in this change.

## Decisions

### Use `username` in the form model and UI copy
The frontend will replace the current `name` field with a `username` field because the backend contract is username-based. This avoids frontend-side mapping ambiguity and keeps error handling consistent with backend duplicate checks.

Alternative considered: keep the UI label as "Full Name" and map it to `username`.
Rejected because it misleads users about what account identifier is being created and does not match backend validation language.

### Add a dedicated frontend signup service around the shared Axios client
The page will call a small service helper under `src/frontend/src/pages/Auth/services/` that posts to `/api/v1/auth/signup` and returns typed data. This keeps component code focused on UI state and reuses the existing shared Axios setup.

Alternative considered: call Axios directly inside `SignUp.tsx`.
Rejected because it would couple request details and error parsing to the page and make later auth-flow reuse harder.

### Treat signup success as account creation, not authentication
After a successful signup, the frontend will not write `access_token` or redirect as if the user is logged in, because the backend currently returns `UserResponse` and not `LoginResponse`. The success UX should guide the user toward signing in next or clearly state that the account was created.

Alternative considered: auto-login locally after signup.
Rejected because it would invent a token that the backend does not return and would diverge from the current backend logic.

### Normalize backend `400 Bad Request` responses into field-safe user feedback
The frontend will catch backend validation errors and show clear messages for duplicate username or duplicate email cases, with a fallback generic error for unexpected failures. This keeps the screen aligned with the backend's current error behavior without introducing brittle assumptions about more advanced validation payloads.

Alternative considered: show only one generic toast or banner for all failures.
Rejected because duplicate-credential failures are a primary part of the existing backend signup logic and should be understandable to the user.

## Risks / Trade-offs

- [Backend error payload shape may vary] -> Mitigation: parse known duplicate messages defensively and fall back to a generic signup failure message.
- [Changing `name` to `username` may feel less friendly] -> Mitigation: use supportive helper copy that explains the username is the account identifier.
- [Users may expect immediate sign-in after account creation] -> Mitigation: present explicit success messaging and a clear next step to navigate to `/signin`.
- [Frontend service typing can drift from backend DTOs over time] -> Mitigation: keep the request/response interfaces minimal and aligned to the current backend contract documented in specs.
