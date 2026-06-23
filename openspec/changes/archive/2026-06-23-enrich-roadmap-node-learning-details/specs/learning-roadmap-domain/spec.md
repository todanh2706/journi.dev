## MODIFIED Requirements

### Requirement: Roadmap State Computation
The backend SHALL compute and return the progress state of a skill node on a per-user basis instead of relying on a global static `isLocked` state. The state SHALL be derived dynamically from the user's progress and the node's complete prerequisite set as `LOCKED`, `AVAILABLE`, `IN_PROGRESS`, or `COMPLETED`. Full-roadmap, individual-node, and completion-validation paths SHALL produce the same state for the same user and node.

#### Scenario: Determine roadmap node state
- **WHEN** the backend evaluates a node for an authenticated learner
- **THEN** it loads persisted progress for the node and every prerequisite node
- **THEN** it returns the persisted `IN_PROGRESS` or `COMPLETED` state when present, `AVAILABLE` when all prerequisites are completed, or `LOCKED` otherwise

#### Scenario: Evaluate a single dependent node
- **WHEN** the backend evaluates one dependent node outside a full-roadmap request
- **THEN** it still loads progress for every prerequisite ID and returns the same state as the full-roadmap computation

## ADDED Requirements

### Requirement: Learner-Confirmed Lesson Completion
The backend SHALL allow an authenticated learner to mark their own unlocked `LESSON` as completed through `POST /api/v1/users/me/progress/nodes/{nodeId}/complete`. The operation SHALL be idempotent, SHALL preserve the first completion timestamp, and SHALL reject locked or assessment-oriented nodes.

#### Scenario: Complete an available lesson
- **WHEN** an authenticated learner submits completion for an `AVAILABLE` or `IN_PROGRESS` `LESSON`
- **THEN** the backend upserts the unique `(user_id, node_id)` progress row as `COMPLETED`
- **THEN** the response identifies the completed node and its completion timestamp

#### Scenario: Repeat lesson completion
- **WHEN** the learner repeats completion for an already `COMPLETED` lesson
- **THEN** the request succeeds without creating a duplicate progress row or replacing the first completion timestamp

#### Scenario: Reject an unsupported completion
- **WHEN** the learner attempts to manually complete a `LOCKED`, `PRACTICE`, `PROJECT`, `QUIZ`, or `CHALLENGE` node
- **THEN** the backend rejects the request without changing progress

#### Scenario: Unlock the next node
- **WHEN** lesson completion satisfies all prerequisites of a dependent node
- **THEN** the next roadmap-node retrieval returns the dependent node as `AVAILABLE` without creating a separate unlock record
