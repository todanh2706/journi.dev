## ADDED Requirements

### Requirement: Black-and-Gold Visual Foundation
The frontend SHALL use a shared black-and-gold visual foundation across public, authentication, dashboard, roadmap, profile, and not-found routes. Base surfaces SHALL be off-black, primary content SHALL use warm high-contrast neutrals, and gold SHALL be the single brand and primary-action accent. Purple, violet, indigo, and cyan SHALL NOT remain as competing brand accents.

#### Scenario: Render a primary application route
- **WHEN** a user opens any supported frontend route
- **THEN** the route uses the shared black surface, text, border, and gold accent tokens
- **THEN** the route does not introduce a page-local purple, violet, indigo, or cyan brand theme

#### Scenario: Render a primary action
- **WHEN** the interface displays its primary action for the current task
- **THEN** the action uses the shared gold treatment with text and focus colors that meet WCAG AA contrast

### Requirement: Semantic Learning States
The frontend SHALL reserve non-brand colors for semantic communication and SHALL pair color with text or an icon. Completed or successful states SHALL use green, errors or destructive states SHALL use red, and locked or disabled states SHALL use a readable neutral treatment. Gold SHALL identify the primary action, selection, or current/available learning focus.

#### Scenario: Compare roadmap node states
- **WHEN** completed, current or available, and locked nodes are visible together
- **THEN** each state is distinguishable by an explicit label or icon in addition to its color treatment

#### Scenario: Display an error
- **WHEN** a form or data request fails
- **THEN** the interface uses the shared error treatment and communicates the failure in text without relying on red alone

### Requirement: Restrained Surface and Shape System
The frontend SHALL communicate hierarchy through typography, spacing, off-black surface elevation, thin borders, and restrained shadows. Panels SHALL use the shared `rounded-xl` or `rounded-2xl` scale, controls SHALL use `rounded-lg` or `rounded-xl`, and pill shapes SHALL be limited to compact status or metadata labels. Decorative mesh gradients, oversized blurred color blobs, glow-heavy cards, and gradient text SHALL NOT be used as the primary visual structure.

#### Scenario: Render a content panel
- **WHEN** a page renders a card, drawer, dialog, or grouped form section
- **THEN** the component uses the shared surface, border, radius, and shadow scale
- **THEN** its hierarchy remains clear without an oversized decorative glow or gradient headline

### Requirement: Consistent Interaction States
Every actionable control SHALL expose default, hover, active, disabled or pending when applicable, and keyboard-visible focus states using the shared interaction tokens. Controls that cannot perform an action SHALL NOT be presented as active links or buttons.

#### Scenario: Navigate with a keyboard
- **WHEN** a keyboard user tabs through links, buttons, form controls, roadmap nodes, and dialog controls
- **THEN** the focused control displays a visible gold focus indicator that is not clipped by its container

#### Scenario: Encounter unavailable functionality
- **WHEN** a destination or action has no implemented route or behavior
- **THEN** the interface omits the action or presents it as clearly unavailable non-interactive content
- **THEN** it does not use an `href="#"` or an active-looking control that produces no useful result

### Requirement: Responsive Product Shell
The frontend SHALL preserve content hierarchy and usable navigation at mobile, tablet, laptop, and desktop widths. Persistent desktop navigation SHALL collapse into an explicitly controlled mobile navigation surface, page gutters SHALL adapt to viewport width, and primary actions SHALL remain reachable without horizontal page overflow.

#### Scenario: Use the dashboard on a narrow viewport
- **WHEN** the viewport is narrower than the persistent-sidebar breakpoint
- **THEN** the sidebar is replaced by a compact header and keyboard-accessible menu control
- **THEN** the user can reach Overview, Roadmaps, and Profile without horizontal page scrolling

#### Scenario: Use a form on a narrow viewport
- **WHEN** a user opens an authentication or profile form on a mobile viewport
- **THEN** labels, fields, feedback, and primary actions remain visible, ordered, and operable without overlapping decoration

### Requirement: Accessible Feedback and Motion
The frontend SHALL meet WCAG AA contrast for essential text and controls, SHALL announce actionable form and request failures, and SHALL honor the user's reduced-motion preference. Nonessential floating, pulsing, and large transition effects SHALL be disabled or removed when reduced motion is requested.

#### Scenario: Receive asynchronous error feedback
- **WHEN** a submitted form or data request fails after user interaction
- **THEN** the error is presented in a live or alert region with a clear recovery action when recovery is available

#### Scenario: Prefer reduced motion
- **WHEN** the operating system reports `prefers-reduced-motion: reduce`
- **THEN** page transitions, graph emphasis, and hover effects avoid nonessential movement and animated decoration

