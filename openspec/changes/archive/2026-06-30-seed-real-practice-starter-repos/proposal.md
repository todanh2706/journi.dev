## Why

The seeded practice challenges currently point learners to the main `journi.dev` source repository instead of real challenge-specific starter repositories. That makes the practice experience feel temporary, weakens the realism promised by the roadmap, and leaves the seeded dataset out of alignment with its own expectation that each assessment challenge should have an intended public GitHub starter repository.

## What Changes

- Replace placeholder `starterRepositoryUrl` values in the seeded assessment challenges with real public GitHub starter repositories curated specifically for each `PRACTICE` or `PROJECT` node.
- Define a maintainer-facing starter-repository curation workflow so repo ownership, naming, default branches, and challenge-to-repo mapping stay explicit and reviewable.
- Add deterministic local validation so the seed dataset rejects reused placeholder URLs, duplicate starter repositories, and malformed challenge-to-repo mappings before contributors rely on them.
- Preserve the current learner-facing API contract by continuing to expose the existing `starterRepositoryUrl` field in challenge responses.
- Surface the curated starter repository on unlocked roadmap node metadata so roadmap-level practice actions can open the same challenge-specific public repository without falling back to placeholder links.

## Capabilities

### New Capabilities
- `practice-starter-repositories`: Defines how public starter repositories are curated, mapped, and validated for seeded assessment challenges.

### Modified Capabilities
- `roadmap-seed-data`: Tightens the seeded assessment-challenge requirements so starter repositories are real challenge-specific public GitHub repos instead of placeholder links.
- `github-practice-submissions`: Clarifies that learner-facing challenge delivery returns the curated challenge-specific starter repository associated with the unlocked assessment node.
- `roadmap-view`: Clarifies that unlocked assessment node metadata can include the curated starter repository for roadmap-level actions while locked nodes keep it hidden.

## Impact

- Seed dataset files under `src/backend/src/main/resources/seed-data/**`
- Backend seed validation and seed-related tests under `src/backend/src/main/java/journi/dev/backend/services/**` and `src/backend/src/test/java/journi/dev/backend/services/**`
- Backend node DTO/service metadata for assessment nodes and the frontend roadmap drawer starter-repository action
- Learner-facing challenge metadata returned by the existing challenge endpoint without changing the response field name
- Public GitHub repositories maintained outside the app repo for each seeded assessment challenge
