## ADDED Requirements

### Requirement: Feature-Oriented Frontend Source Layout
The frontend source tree SHALL organize domain-specific code under feature folders while preserving route-level pages and shared primitives.

#### Scenario: Inspecting frontend feature folders
- **WHEN** a contributor explores `src/frontend/src/features`
- **THEN** they find domain-owned frontend code grouped by feature area such as auth, dashboard, and roadmaps
- **THEN** feature folders contain their own components, services, hooks, utilities, and types when those files are specific to that domain

#### Scenario: Inspecting route pages
- **WHEN** a contributor explores `src/frontend/src/pages`
- **THEN** they find route-level screen files that compose feature code
- **THEN** page folders do not own domain services or reusable feature components unless those files are strictly page-local

### Requirement: Shared Frontend Boundary
The frontend SHALL keep shared top-level folders limited to cross-feature code.

#### Scenario: Inspecting shared components and utilities
- **WHEN** a contributor explores `src/frontend/src/components`, `services`, `utils`, or `assets`
- **THEN** files in those folders are reusable across more than one feature or provide application-wide infrastructure

#### Scenario: Placing domain-specific code
- **WHEN** a contributor adds code used only by one product domain
- **THEN** the code is placed under the matching `features/<domain>` folder instead of a shared top-level folder

### Requirement: Frontend Refactor Behavior Preservation
The frontend structure migration SHALL preserve existing user-facing behavior and public contracts.

#### Scenario: Preserving routes
- **WHEN** the frontend source tree is reorganized
- **THEN** existing client routes continue to render the same screens at the same paths

#### Scenario: Preserving API contracts
- **WHEN** frontend services or types are moved into feature folders
- **THEN** API paths, request shapes, response shapes, token handling, and Axios base configuration remain unchanged

#### Scenario: Preserving MVP roadmap flow
- **WHEN** the structure migration is complete
- **THEN** the login-to-roadmap flow remains available without adding non-MVP behavior
