## Why

The predefined Backend Java Spring Boot roadmap already stores basic node metadata, but learners cannot use it from the roadmap drawer because the API does not expose the checklist and relational learning resources and the UI renders placeholders. The MVP needs actionable, trustworthy node details while preserving prerequisite-based content gating for locked nodes.

## What Changes

- Enrich all 14 seeded Backend Java Spring Boot nodes with topic-specific summaries, notes, estimated effort, actionable checklist items, and verified external learning resources that resolve to real documentation, guides, courses, or exercises.
- Extend the skill-node response contract with typed learning-detail fields instead of requiring the frontend to interpret raw `contentJson` or fabricate content.
- Load relational `LearningContent` records efficiently when returning roadmap nodes and individual node details.
- Redact checklist and learning-resource details for `LOCKED` nodes while retaining enough metadata to render the roadmap and explain the prerequisite gate.
- Render real summary, level, estimated effort, note, checklist, and learning-resource links in the node drawer for `AVAILABLE`, `IN_PROGRESS`, and `COMPLETED` nodes.
- Keep locked-node drawers limited to node identity, state, and unlock guidance; do not render gated learning details or completion affordances.
- Add an authenticated **Mark as complete** action for unlocked `LESSON` nodes, then refresh roadmap state so newly satisfied dependent nodes become available immediately.
- Make single-node completion validation load the complete prerequisite progress set and reject manual completion for assessment-oriented node types.
- Add backend and frontend tests for response mapping, locked-node redaction, unlocked detail rendering, and external-link behavior.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `roadmap-seed-data`: Strengthen the predefined dataset requirements so every node has useful, topic-specific checklist work and valid real-world learning resources.
- `roadmap-view`: Expand the node-detail contract and drawer behavior to show seeded learning details only for nodes that are not locked.
- `learning-roadmap-domain`: Define idempotent learner-confirmed completion for unlocked theory lessons and consistent prerequisite evaluation across list, detail, and completion paths.

## Impact

- Backend seed dataset, skill-node DTOs, service mapping, learning-content repository queries, and related tests.
- Frontend roadmap types, graph data mapping, node drawer rendering, and component tests.
- `GET /api/v1/roadmaps/{roadmapId}/nodes` and `GET /api/v1/skill-nodes/{nodeId}` gain additive learning-detail fields; existing fields and routes remain unchanged.
- The existing `POST /api/v1/users/me/progress/nodes/{nodeId}/complete` endpoint is restricted to unlocked `LESSON` nodes and becomes the drawer's completion contract.
- No schema migration or new dependency is expected because `contentJson` and `LearningContent` already store the required data.
