## ADDED Requirements

### Requirement: Contributor-Readable Feature Regression Inventory
The project SHALL maintain a contributor-readable inventory that maps implemented feature areas to their automated tests or verification scripts, and it SHALL identify any known coverage gaps that remain after the audit.

#### Scenario: Review implemented feature coverage
- **WHEN** a contributor reviews the regression inventory for the current codebase
- **THEN** they can find the implemented auth/session, roadmap, progress, practice-submission, and seed-data feature areas
- **THEN** each listed area names the test files or commands that currently protect it, or explicitly calls out a remaining gap

### Requirement: Targeted Regression Entry Points For Existing Frontend and API Flows
The project SHALL expose targeted automated entry points for existing frontend logic and seeded roadmap API verification without requiring a full browser test framework.

#### Scenario: Run the targeted frontend regression checks
- **WHEN** a contributor runs the documented targeted frontend regression command for roadmap and practice logic
- **THEN** the command executes the existing or updated checks under `src/frontend/tests`
- **THEN** failures identify a regression in the corresponding current feature behavior

#### Scenario: Verify the seeded roadmap API flow
- **WHEN** a contributor runs the documented roadmap API verification flow against the local stack
- **THEN** the check exercises signup or login, roadmap retrieval, gated node details, lesson completion, and dependent-node unlocking
- **THEN** the verification fails if the seeded MVP roadmap behavior no longer matches the current contract

### Requirement: Critical Existing Features Have Automated Regression Coverage
Each critical feature that the repository already implements SHALL be exercised by at least one automated regression test or verification script at the narrowest effective layer.

#### Scenario: Audit current MVP and practice features
- **WHEN** the audit evaluates currently implemented auth/session, roadmap progression, lesson completion, practice challenge access, submission lifecycle, and seed-data behavior
- **THEN** each feature area is covered by an existing or newly added automated regression check
- **THEN** any intentionally deferred gap is documented explicitly rather than left implicit
