## ADDED Requirements

### Requirement: Challenge Brief Returns The Curated Starter Repository
The learner-facing challenge brief SHALL return the curated starter repository associated with the requested unlocked assessment node, even when automated evaluation remains disabled.

#### Scenario: Retrieve a brief for a disabled assessment challenge
- **WHEN** an authenticated learner requests an unlocked `PRACTICE` or `PROJECT` challenge whose automated evaluation is disabled
- **THEN** the response still includes the curated challenge-specific starter repository URL for that assessment node
- **THEN** the starter repository metadata remains learner-visible even though submission is unavailable

