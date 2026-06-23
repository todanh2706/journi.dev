## Context

The seed dataset contains 14 Backend Java Spring Boot nodes, each with three checklist entries and two external resources. During seeding, summary, level, estimated hours, note, and checklist are serialized into `SkillNode.contentJson`; resources are stored as related `LearningContent` rows. The current `SkillNodeResponse` exposes only raw `contentJson`, does not include `LearningContent`, and the frontend graph derives a summary from the slug while the drawer always renders checklist/resource placeholders.

Node availability is already computed per authenticated user as `LOCKED`, `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED`. The frontend type currently models `NOT_STARTED` instead of the backend's `AVAILABLE`/`LOCKED` values, so the learning-detail work must also align this existing contract mismatch. In this change, the user's term “blocked” maps to the domain status `LOCKED`.

## Goals / Non-Goals

**Goals:**

- Make every seeded node actionable and relevant to its Java/Spring Boot topic.
- Return typed node learning details without exposing persistence-specific JSON parsing to React components.
- Prevent locked-node learning details from being disclosed by the API or rendered by the frontend.
- Fetch resources for a roadmap without introducing one query per node.
- Preserve current endpoints, progression rules, database schema, and seed idempotency.
- Let learners explicitly complete an unlocked theory lesson and see the roadmap unlock result without leaving the drawer flow.

**Non-Goals:**

- Checklist persistence, per-item completion, or a new progress model.
- Per-checklist-item persistence, challenge/quiz evaluation, AI content generation, or dynamic roadmaps.
- Replacing `contentJson` or `LearningContent` with a new content-management model.
- Adding a URL-health background job or guaranteeing that third-party links remain available forever.

## Decisions

### 1. Expose a typed additive learning-detail contract

`SkillNodeResponse` will gain nullable/empty-safe fields for `summary`, `level`, `estimatedHours`, `note`, `checklist`, and `learningResources`. Each learning resource will use a small response DTO containing `title`, `sourceType`, `sourceUrl`, and `description` (mapped from `contentBody`). The existing `contentJson` field remains unchanged for compatibility during this MVP change.

The backend service will parse its own seeded `contentJson` into an internal typed structure and map related `LearningContent` entities to response DTOs. The frontend will consume these typed fields directly and will not parse JSON in a component.

Alternative considered: return only `contentJson` and parse it in React. This was rejected because it duplicates backend persistence knowledge in the UI, weakens type safety, and does not solve relational resource delivery.

### 2. Enforce locked-content redaction at the response boundary

After computing a node's per-user `ProgressStatus`, the service will populate learning details only for `AVAILABLE`, `IN_PROGRESS`, and `COMPLETED`. For `LOCKED`, it will return empty/null learning-detail fields and `isLocked: true`. Basic graph metadata—ID, title, slug, order, type, and progress state—remains available so the roadmap can display the locked node and its gate.

The drawer will also branch on `isLocked`/`progressStatus === "LOCKED"` and render only the locked-state explanation. This defense-in-depth prevents accidental rendering if a client object is assembled incorrectly, while backend redaction prevents discovery through direct API inspection.

Alternative considered: hide the fields only in React. This was rejected because the API would still disclose gated content.

### 3. Batch-load learning resources

For list responses, `SkillNodeService` will obtain all resource rows using the existing `findByNode_NodeIdIn(...)` repository path, group them by node ID, and map them while building responses. Individual-node retrieval can use the same grouping helper with a one-element collection. Locked nodes may be excluded from the requested ID collection once statuses are known.

Alternative considered: call `findByNode_NodeId` inside the node mapping loop. This was rejected because it creates an N+1 query pattern for roadmap detail.

### 4. Keep the existing seed model and curate content in place

The JSON dataset remains the source of truth. Each of the 14 nodes will contain a concrete outcome-oriented summary and note, at least three verifiable checklist tasks, and at least two HTTPS resources directly related to the node. Authoritative sources (Java, Spring, PostgreSQL, JUnit, Docker, and deployment-provider documentation) are preferred; reputable educational sources are acceptable where they provide useful practice. Resource titles, descriptions, and types must match the linked page, and links will be checked during implementation.

No database migration is needed. Rerunning the existing idempotent seeder will replace scoped resource/challenge relationships and update node content from the curated dataset.

### 5. Align frontend progress types with the backend enum

The frontend `SkillNode.progressStatus` union and status labels will use `LOCKED | AVAILABLE | IN_PROGRESS | COMPLETED`. Locked rendering continues to rely primarily on `isLocked`, but both fields must be consistent. This makes the access branch explicit and removes the current `NOT_STARTED`/`AVAILABLE` mismatch.

### 6. Render resources as safe external links and checklists as read-only tasks

Unlocked drawers will render checklist entries as non-interactive task rows because per-item persistence is out of scope. Resources will render their title, type/description, and an external link opened with `target="_blank"` and `rel="noreferrer"`. Missing unlocked details will retain concise empty states; locked nodes will not show those empty-state sections because doing so would reveal the shape of gated content without helping the learner.

### 7. Use explicit self-completion for theory lessons

For an authenticated `LESSON` whose computed state is `AVAILABLE` or `IN_PROGRESS`, the drawer will show a gold primary **Mark as complete** action. The frontend will call the existing `/api/v1/users/me/progress/nodes/{nodeId}/complete` endpoint through the shared Axios client, disable the action while pending, keep the drawer open on failure, and refresh or reconcile roadmap-node data after success.

The backend will load progress for every prerequisite ID even when validating a single node, upsert the unique user-node progress row as `COMPLETED`, and preserve the first `completedAt` on repeated requests. It will reject `LOCKED` nodes and `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE` nodes. Availability of dependent nodes remains computed; completing a lesson does not create separate progress rows for newly available nodes.

Alternative considered: infer completion when all checklist rows have been viewed or clicked. This was rejected because checklist state is not persisted and passive reading signals do not prove learner intent.

## Risks / Trade-offs

- [Third-party resource URLs can change] → Prefer canonical official HTTPS documentation, validate all seeded URLs during implementation, and keep resources isolated in the rerunnable seed JSON for easy maintenance.
- [Malformed legacy `contentJson` can fail parsing] → Use a centralized parser with explicit error handling and an empty detail result; cover malformed/null content in service tests rather than throwing a 500 for the entire roadmap.
- [Additive DTO fields increase roadmap payload size] → Return only small text metadata and links, batch-load resources, and redact locked details. The 14-node MVP roadmap keeps the payload bounded.
- [Rerunning the seeder replaces scoped resource rows] → Preserve the current documented seed behavior and test idempotent counts/content after two runs.
- [Two lock indicators can drift] → Derive `isLocked` from computed `ProgressStatus` in the backend and test contract consistency; the frontend treats `LOCKED` as gated even if one indicator is malformed.
- [Completion validation can disagree with the roadmap list] → Query progress for the evaluated node plus every prerequisite ID and cover single-node versus full-roadmap parity in tests.
- [Repeated clicks can submit duplicate requests] → Disable the action while pending and keep the backend operation idempotent under the `(user_id, node_id)` unique constraint.

## Migration Plan

1. Add DTOs, service mapping/redaction, repository usage, and backend tests while keeping current response fields.
2. Curate and validate the seed JSON, then rerun the existing seed flow in local development.
3. Update frontend types and drawer rendering against the additive contract; run build, lint, and relevant component tests.
4. Fix single-node prerequisite evaluation and restrict manual completion to unlocked lessons.
5. Connect the drawer completion action, refresh roadmap state after success, and verify pending/error/completed states.
6. Verify one locked, one available, and one completed lesson through the authenticated roadmap flow.

Rollback consists of reverting the additive response fields and drawer rendering. Existing database rows remain compatible; rerunning the prior dataset restores prior seeded content if needed.

## Open Questions

None. Checklist rows are intentionally read-only for this change, and `LOCKED` is the canonical interpretation of “blocked.”
