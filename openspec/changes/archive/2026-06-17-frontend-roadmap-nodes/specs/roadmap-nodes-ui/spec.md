## ADDED Requirements

### Requirement: View Roadmap Details and Nodes
The system SHALL display the details of a roadmap and its associated skill nodes when the user selects a roadmap from the catalog.

#### Scenario: Successful roadmap details loading
- **WHEN** the user navigates to `/dashboard/roadmaps/:roadmapId`
- **THEN** the system fetches the roadmap and its skill nodes from the backend
- **THEN** the system displays the roadmap title, description, and a vertical list of skill nodes in their designated order

#### Scenario: Roadmap not found or API error
- **WHEN** the user navigates to a non-existent roadmap ID or the API request fails
- **THEN** the system displays a graceful error message indicating the roadmap could not be loaded

#### Scenario: Navigation from catalog
- **WHEN** the user clicks "View Path" on a roadmap card in the `RoadmapsPage`
- **THEN** the system navigates the user to the corresponding `/dashboard/roadmaps/:roadmapId` route
