## MODIFIED Requirements

### Requirement: Not-Found Recovery Screen

The frontend SHALL provide a branded 404 experience for unknown client-side routes. The screen SHALL communicate that the requested resource is unavailable. If the user has a prior navigation history within the app, the screen SHALL provide a "Go Back" button that returns them to the previous page. If there is no navigation history, the screen SHALL provide a "Go to Homepage" button linking back to the landing page (`/`).

#### Scenario: Visiting an unknown route with navigation history

- **WHEN** a user navigates from an existing app page to an unknown route
- **THEN** the application renders the not-found page with a "Go Back" button
- **THEN** clicking the button executes a browser back action (`navigate(-1)`)

#### Scenario: Visiting an unknown route without navigation history

- **WHEN** a user opens an unknown route directly (e.g., via bookmark or direct URL entry)
- **THEN** the application renders the not-found page with a "Go to Homepage" button
- **THEN** clicking the button navigates the user to `/`
