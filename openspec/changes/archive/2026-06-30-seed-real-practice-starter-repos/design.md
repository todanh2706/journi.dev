## Context

The seeded Backend Java Spring Boot roadmap currently defines eight assessment challenges, and every one of them uses the same `starterRepositoryUrl`: the main `journi.dev` application repository. The backend validation only enforces that the URL is a public GitHub HTTPS link, so the placeholder passes structural checks even though it is not a realistic starter repo for the challenge brief, expected artifacts, or learner workflow.

The practice workspace and challenge API already treat `starterRepositoryUrl` as learner-facing metadata, so the best change is to improve the curated data and its validation rather than redesign the challenge response or persistence model.

## Goals / Non-Goals

**Goals:**
- Give every seeded `PRACTICE` and `PROJECT` challenge a real public starter repository that matches the challenge brief.
- Make the challenge-to-repo mapping explicit and maintainable for contributors.
- Add deterministic local validation that catches placeholder reuse, duplicate repo mappings, and malformed starter-repository links before reseeding.
- Keep the current API and database contract stable by continuing to use `starterRepositoryUrl`.

**Non-Goals:**
- Build automated GitHub provisioning into the application.
- Add new backend fields for repo owner, default branch, or repository metadata when the current URL field is sufficient.
- Turn disabled assessment challenges on as part of the repository-curation work.
- Rewrite grader contracts or hidden tests beyond the updates needed to stay aligned with the new starter repositories.

## Decisions

### Decision: Use one public starter repository per seeded assessment challenge
Each seeded `PRACTICE` or `PROJECT` node will map to its own public GitHub starter repository. This makes the learner brief concrete, avoids confusing unrelated files, and keeps repository scope aligned with each challenge's expected artifacts.

Alternative considered:
- Keep one shared monorepo for every challenge. Rejected because it recreates the same ambiguity as the current placeholder and makes challenge-specific onboarding harder.

### Decision: Preserve the existing `starterRepositoryUrl` challenge contract
The app will continue storing and returning a single starter-repository URL per challenge. The implementation will improve the seed data and surrounding validation, not the DTO or entity shape.

Alternative considered:
- Add structured repository metadata fields to the database and API. Rejected for now because the existing learner-facing contract is already sufficient for linking to a real repo.

### Decision: Expose starter repository metadata on unlocked assessment node responses
Roadmap node metadata will include the curated `starterRepositoryUrl` for unlocked `PRACTICE` and `PROJECT` nodes that have a required challenge. This lets roadmap-level actions, such as an **Open starter repository** link in the node drawer, use the same backend-owned challenge mapping as the practice workspace. Locked assessment nodes will continue returning `null` for this field so prerequisite-gated challenge metadata is not exposed early.

Alternative considered:
- Keep starter repository URLs available only from the challenge endpoint. Rejected because roadmap-level UI would either need an extra request before rendering the starter link or risk reintroducing frontend-side placeholder/hardcoded repository links.

### Decision: Keep curation workflow explicit with a maintainer-readable catalog plus code-level validation
The repo will maintain a contributor-readable catalog for seeded practice starter repositories, including the challenge slug, public repository URL, intended default branch, and readiness notes. Automated validation will parse the seed data to ensure every assessment challenge uses a unique, non-placeholder GitHub repository URL.

Alternative considered:
- Rely only on the seed JSON with no curation catalog. Rejected because external repository ownership and readiness would remain hard to review.

### Decision: Separate deterministic local validation from live external verification
Local tests should enforce deterministic rules such as URL format, uniqueness, and placeholder rejection. A contributor workflow can still include manual or script-assisted live GitHub verification, but the normal automated suite should not depend on outbound network access.

Alternative considered:
- Call GitHub during unit or seed tests. Rejected because it would add network flakiness and reduce local reliability.

## Risks / Trade-offs

- [Risk] External starter repositories can drift away from the challenge brief or expected artifacts. → Mitigation: keep a curated catalog, update seed validation rules, and require repo review when challenge briefs change.
- [Risk] Managing eight public repositories adds contributor overhead. → Mitigation: enforce a naming convention and keep the catalog compact and explicit.
- [Risk] Some starter repositories may be ready before their grader contracts are enabled. → Mitigation: allow real repos to exist independently from `evaluationEnabled`; the brief remains useful even while automated submission is disabled.
- [Risk] The catalog can become stale if external repos move or are archived. → Mitigation: include repository validation in the seed maintenance workflow and treat broken links as a seed-data regression.

## Migration Plan

1. Choose the GitHub owner or organization and a naming convention for all seeded assessment starter repositories.
2. Create and populate one public starter repository for each seeded `PRACTICE` or `PROJECT` challenge.
3. Update the seed dataset to use the real challenge-specific `starterRepositoryUrl` values.
4. Add the maintainer-readable starter-repository catalog and deterministic validation checks.
5. Rerun the seed-related backend tests and any practice-flow validation that depends on starter repository metadata, including roadmap node metadata that powers starter-repository actions.
6. If a repository link proves unusable, roll back by reseeding the last known-good starter repository URL for that challenge; no schema migration is required.

## Open Questions

- Which GitHub owner or organization should permanently host the starter repositories?
- Should every starter repository standardize on `main`, or can the catalog declare per-repo default branches?
- How complete should the initial starter repos be for challenges whose grader contracts are not yet enabled?
