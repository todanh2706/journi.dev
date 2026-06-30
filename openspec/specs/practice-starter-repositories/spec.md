# practice-starter-repositories Specification

## Purpose
Define how seeded assessment starter repositories are curated, mapped, and validated so learner-facing practice links point to challenge-specific public GitHub repositories instead of placeholder application source links.

## Requirements

### Requirement: Curated Starter Repository Catalog
The project SHALL maintain a contributor-readable catalog for seeded assessment starter repositories. The catalog SHALL map each seeded `PRACTICE` or `PROJECT` node slug to its public repository URL, intended default branch, and maintainer notes or readiness status.

#### Scenario: Review starter repository ownership and readiness
- **WHEN** a contributor reviews the seeded practice starter repository catalog
- **THEN** they can identify every seeded assessment node and its intended public starter repository
- **THEN** they can see enough metadata to understand which repo belongs to which challenge and whether it is ready for learner use

### Requirement: Distinct Public Starter Repositories For Assessment Nodes
Each seeded `PRACTICE` and `PROJECT` challenge SHALL use a distinct public GitHub starter repository that is scoped to that challenge rather than the main Journi.dev application repository.

#### Scenario: Inspect seeded assessment starter repositories
- **WHEN** a contributor inspects the starter repositories assigned to the seeded assessment challenges
- **THEN** each challenge points to a public GitHub repository URL unique to that challenge
- **THEN** no seeded assessment challenge points to the main `journi.dev` application repository as its starter repository

### Requirement: Deterministic Local Validation Of Starter Repository Mappings
The seeded practice starter repository mapping SHALL be validated locally without depending on live network access. Validation SHALL reject malformed GitHub URLs, duplicate starter repositories across seeded assessment challenges, and known placeholder mappings.

#### Scenario: Validate starter repository mappings during development
- **WHEN** the seeded practice dataset is validated locally
- **THEN** the validation fails if an assessment challenge uses a malformed GitHub HTTPS URL, reuses another challenge's starter repository, or falls back to a known placeholder repository
