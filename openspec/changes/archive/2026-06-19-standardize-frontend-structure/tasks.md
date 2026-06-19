## 1. Audit and Target Map

- [x] 1.1 Inventory current frontend files and classify each as route page, feature-owned code, or shared code.
- [x] 1.2 Define the target folder map for auth, dashboard, roadmaps, shared UI, shared services, shared assets, and shared utilities.

## 2. Feature Folder Preparation

- [x] 2.1 Create feature folders for `auth`, `dashboard`, and `roadmaps` with appropriate `components`, `services`, `hooks`, `types`, and `utils` subfolders only where needed.
- [x] 2.2 Add feature boundary barrel exports where they reduce import churn without hiding internal ownership.

## 3. Auth Domain Migration

- [x] 3.1 Move auth-specific services, icons, buttons, and hooks out of `pages/Auth` and top-level shared folders into `features/auth`.
- [x] 3.2 Keep `SignIn` and `SignUp` as route-level pages or thin route wrappers under `pages`.
- [x] 3.3 Update auth imports and verify token handling/API behavior remains unchanged.

## 4. Dashboard Domain Migration

- [x] 4.1 Move dashboard/feed-specific reusable widgets out of `pages/Feed/components` into `features/dashboard/components`.
- [x] 4.2 Keep dashboard route-level layout and pages under `pages` or create thin wrappers that preserve current routes.
- [x] 4.3 Update dashboard imports and verify the dashboard renders the same static demo surface.

## 5. Roadmaps Domain Migration

- [x] 5.1 Move roadmap-specific service and types into `features/roadmaps` unless they are demonstrably shared across multiple domains.
- [x] 5.2 Move `features/roadmap-canvas` under `features/roadmaps/roadmap-canvas` or another roadmaps-owned location.
- [x] 5.3 Update roadmap page imports and verify roadmap list/detail routes still render with the existing API contracts.

## 6. Shared Boundary Cleanup

- [x] 6.1 Leave only cross-feature reusable code in top-level `components`, `services`, `utils`, and `assets`.
- [x] 6.2 Remove empty folders and stale import paths after migration.
- [x] 6.3 Avoid dependency, route path, Docker, environment variable, and UI behavior changes.

## 7. Verification

- [x] 7.1 Run `npm run build` in `src/frontend`.
- [x] 7.2 Run `npm run lint` in `src/frontend`.
- [x] 7.3 Manually smoke-check the main routes: `/`, `/signin`, `/signup`, `/dashboard`, `/dashboard/roadmaps`, and `/dashboard/roadmaps/:roadmapId` when backend data is available.
