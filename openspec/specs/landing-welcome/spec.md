# Landing Welcome

## Requirements

### Requirement: Display animated welcome experience on root path
The system SHALL display the Code-to-Galaxy welcome animation on the root path `/` for unauthenticated users.

#### Scenario: Unauthenticated user visits root path
- **WHEN** an unauthenticated user visits `/`
- **THEN** the system displays the terminal typing animation followed by the roadmap constellation.

### Requirement: Provide CTA for login and signup
The system MUST provide clear actions for the user to authenticate or register after or during the animation.

#### Scenario: User clicks login CTA
- **WHEN** the user clicks the "Log In" button on the landing page
- **THEN** the system navigates to the login route.

#### Scenario: User clicks signup CTA
- **WHEN** the user clicks the "Sign Up" button on the landing page
- **THEN** the system navigates to the signup route.

