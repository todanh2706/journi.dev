## 1. Curate External Starter Repositories

- [x] 1.1 Choose the GitHub owner or organization, naming convention, and default-branch policy for seeded assessment starter repositories.
- [x] 1.2 Create or populate one public starter repository for each seeded `PRACTICE` and `PROJECT` challenge with challenge-appropriate starter files.
- [x] 1.3 Record the curated repository mapping, default branch, and readiness notes in a contributor-readable starter repository catalog.

## 2. Wire Real Repositories Into The Seed Dataset

- [x] 2.1 Replace the placeholder `starterRepositoryUrl` values in the seeded roadmap dataset with the curated challenge-specific public GitHub repositories.
- [x] 2.2 Review challenge briefs and expected artifacts so each seeded assessment challenge still matches the linked starter repository content.

## 3. Add Deterministic Validation

- [x] 3.1 Strengthen seed-data validation so assessment challenges reject malformed GitHub URLs, duplicate starter repositories, and known placeholder repository mappings.
- [x] 3.2 Add or revise backend seed-related tests to enforce the curated starter-repository rules and protect the challenge-to-repo mapping from regression.

## 4. Verify Learner-Facing Practice Metadata

- [x] 4.1 Verify that unlocked challenge responses and practice-workspace flows continue to expose the curated `starterRepositoryUrl` without changing the current API field contract.
- [x] 4.2 Update contributor-facing docs or maintenance notes so future seed-data changes preserve the real starter repository workflow.
- [x] 4.3 Expose the curated `starterRepositoryUrl` on unlocked assessment node metadata and wire roadmap-level **Open starter repository** actions to that backend-provided URL.
