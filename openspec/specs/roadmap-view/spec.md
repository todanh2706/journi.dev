## ADDED Requirements

### Requirement: Dashboard Layout with Nested Routes
The system SHALL support navigating between different dashboard sections (Overview, Roadmap) while preserving the main sidebar.

#### Scenario: Navigate to Roadmap Tab
- **WHEN** user clicks on the "Roadmap" tab in the dashboard sidebar
- **THEN** the system updates the URL to `/dashboard/roadmaps`
- **THEN** the main content area renders the Roadmap Page without unmounting the sidebar

### Requirement: Fetch and Display Roadmaps
The system SHALL retrieve available learning roadmaps from the backend API and display them in the Roadmap Page.

#### Scenario: Successful roadmap load
- **WHEN** user navigates to the Roadmap Page
- **THEN** the system makes a GET request to `/api/v1/roadmaps`
- **THEN** the system displays the returned roadmaps as styled cards showing the roadmap title, description, and level

#### Scenario: Empty roadmap list
- **WHEN** the backend returns an empty list of roadmaps
- **THEN** the system displays a friendly empty state message indicating that no roadmaps are currently available

#### Scenario: API Error
- **WHEN** the backend request fails (e.g., 500 error or network issue)
- **THEN** the system displays an error message allowing the user to retry or navigate back

### Requirement: Serve Roadmaps from Backend
The system SHALL provide a REST endpoint to retrieve all learning roadmaps.

#### Scenario: Requesting all roadmaps
- **WHEN** a GET request is made to `/api/v1/roadmaps`
- **THEN** the backend service queries the repository for all roadmaps
- **THEN** the backend maps the entities to `LearningRoadmapResponse` objects
- **THEN** it returns a 200 OK with the list of roadmaps
