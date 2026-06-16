# dashboard-auth-state Specification

## Purpose
TBD - created by archiving change dynamic-dashboard-auth-state. Update Purpose after archive.
## Requirements
### Requirement: Dashboard Auth State Reflection
The Dashboard SHALL dynamically reflect the global authentication state of the user. When an active session is detected, the dashboard SHALL display the authenticated user's information. When no active session exists, the dashboard SHALL render a distinct "not logged in" state without restricting access to the page layout itself.

#### Scenario: User views dashboard while logged in
- **WHEN** the user visits `/dashboard` and holds a valid authentication token
- **THEN** the Dashboard renders the user's real username or avatar in place of mock data

#### Scenario: User views dashboard while logged out
- **WHEN** the user visits `/dashboard` without a valid authentication token
- **THEN** the Dashboard renders placeholder/guest profile information without restricting access

