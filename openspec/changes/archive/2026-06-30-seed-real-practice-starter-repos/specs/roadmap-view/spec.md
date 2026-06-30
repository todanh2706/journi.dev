## MODIFIED Requirements

### Requirement: GitHub Practice Entry Points
The roadmap view SHALL provide clear entry points for supported `PRACTICE` and `PROJECT` nodes. Unlocked assessment node metadata SHALL include the curated challenge-specific `starterRepositoryUrl` when a required challenge exists, and locked assessment node metadata SHALL NOT expose that URL before prerequisites are complete.

#### Scenario: Open a starter repository from an unlocked assessment node
- **WHEN** a learner opens the details for an unlocked `PRACTICE` or `PROJECT` node with a required challenge
- **THEN** the roadmap node metadata includes the curated public starter repository URL for that challenge
- **THEN** the **Open starter repository** action opens that backend-provided GitHub URL in a safe new tab

#### Scenario: Keep starter repository metadata hidden for locked assessment nodes
- **WHEN** a learner opens or receives metadata for a locked `PRACTICE` or `PROJECT` node
- **THEN** the roadmap node metadata does not expose the starter repository URL
