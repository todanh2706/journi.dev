## Why

The frontend source tree has grown with mixed responsibilities: page folders contain feature services and reusable UI, while newer code uses `features/`. Standardizing the structure now will make future MVP work easier to navigate without changing product behavior.

## What Changes

- Define a clear frontend source organization standard for `src/frontend/src`.
- Move domain-specific UI and API code toward feature folders such as `features/auth`, `features/dashboard`, and `features/roadmaps`.
- Keep route-level files under `pages/`, domain code under `features/<domain>`, and shared cross-feature primitives under `components/`, `services/`, `utils/`, and `assets/`.
- Preserve existing routes, API contracts, environment variables, Docker ports, visual behavior, and dependency versions.
- Use compatibility exports or small import updates so the migration can happen incrementally and safely.
- Document the new structure in OpenSpec so future frontend work follows the same pattern.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `system-architecture`: Add frontend source organization requirements that define route, feature, shared, and migration boundaries.

## Impact

- Frontend source layout under `src/frontend/src`.
- Imports in route pages, feature components, hooks, services, and types affected by moved files.
- OpenSpec architecture documentation under `openspec/specs/system-architecture/spec.md`.
- No backend changes, API changes, database changes, dependency changes, Docker changes, or environment variable changes.
