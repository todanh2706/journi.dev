## 1. Curate and Validate Seed Content

- [x] 1.1 Review all 14 Backend Java Spring Boot node definitions and update each summary, level, estimated hours, learner note, and checklist with topic-specific, outcome-oriented content and at least three executable checklist tasks.
- [x] 1.2 Curate at least two relevant HTTPS learning resources per node, preferring canonical Java, Spring, PostgreSQL, JUnit, Docker, and deployment documentation and adding reputable exercises or guides where they improve practice value.
- [x] 1.3 Validate every seeded resource URL for a successful response or valid redirect and manually confirm that its page title/topic matches the seed title, description, and target node.
- [x] 1.4 Extend seed-data tests to assert 14 ordered nodes, minimum checklist/resource counts for every node, required resource fields, HTTPS URLs, valid prerequisite references, milestone challenges, and idempotent results after two seeding runs.

## 2. Expose Progress-Gated Learning Details

- [x] 2.1 Add explicit backend response models for parsed node learning details and learning resources, extending `SkillNodeResponse` additively while retaining existing fields.
- [x] 2.2 Implement centralized null/malformed-safe parsing of seeded `contentJson` into summary, level, estimated hours, note, and checklist response fields.
- [x] 2.3 Batch-load `LearningContent` rows for unlocked node IDs, group them by node, and map title, source type, URL, and description without per-node repository queries.
- [x] 2.4 Update roadmap-node and individual-node service mapping so `AVAILABLE`, `IN_PROGRESS`, and `COMPLETED` responses contain typed learning details while `LOCKED` responses return only basic graph metadata and empty/null detail fields.
- [x] 2.5 Add backend service/controller tests covering all four progress statuses, locked-content redaction, unlocked detail/resource mapping, `isLocked` consistency, malformed `contentJson`, and batch list behavior.

## 3. Render Unlocked Node Details

- [x] 3.1 Update frontend skill-node and learning-resource types to match the additive backend response and the canonical `LOCKED | AVAILABLE | IN_PROGRESS | COMPLETED` status enum.
- [x] 3.2 Pass typed summaries and learning details through roadmap graph node data instead of deriving the primary unlocked summary from the slug.
- [x] 3.3 Update the node drawer so unlocked nodes render level, estimated effort, note, actionable read-only checklist rows, and real resource links with safe new-tab attributes plus precise optional-field empty states.
- [x] 3.4 Update the locked drawer branch so it renders node identity and prerequisite guidance only, with no summary, learning metadata, checklist, resources, or start/completion controls.
- [x] 3.5 Add frontend tests for status labels, unlocked detail rendering, external-link attributes, partial unlocked data, locked-content suppression, keyboard close behavior, and focus handling.

## 4. Verify the MVP Flow

- [x] 4.1 Run backend tests and rerun the roadmap seeder against local development data to verify enriched content and idempotency.
- [x] 4.2 Run frontend build, lint, and relevant component tests after the contract and drawer changes.
- [x] 4.3 Manually verify the authenticated roadmap flow with one locked node and each unlocked status, confirming that completion still unlocks the next node and gated learning details become visible only after unlock.

## 5. Learner-Confirmed Lesson Completion

- [x] 5.1 Fix single-node prerequisite evaluation so completion validation loads progress for every prerequisite ID and matches full-roadmap state computation.
- [x] 5.2 Restrict the completion endpoint to `LESSON` nodes in `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED` state, preserve the first completion timestamp, and add backend tests for locked, repeated, dependent, and non-lesson requests.
- [x] 5.3 Add a typed frontend progress service method that calls `POST /users/me/progress/nodes/{nodeId}/complete` through the shared Axios client.
- [x] 5.4 Add **Mark as complete** to unlocked lesson drawers with pending, inline error, retry, completed, locked, and non-lesson states.
- [x] 5.5 Refresh or reconcile roadmap-node data after completion so the completed count and newly available dependent nodes update without a full-page reload.
- [x] 5.6 Add frontend tests for completion visibility, duplicate-submit protection, error recovery, successful refresh, and focus preservation.
