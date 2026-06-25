## ADDED Requirements

### Requirement: Assessment-Driven Node Completion
The backend SHALL complete a `PRACTICE` or `PROJECT` node only from a trusted `PASSED` result for the authenticated learner's required challenge submission. `SUBMITTED`, `EVALUATING`, `NEEDS_CHANGES`, and `FAILED` submissions SHALL NOT complete the node or unlock dependents. Result processing SHALL preserve the first completion timestamp and reuse the existing prerequisite computation.

#### Scenario: Complete an assessment node after a passing submission
- **WHEN** a trusted grader result transitions the learner's required submission to `PASSED`
- **THEN** the backend upserts that learner's node progress as `COMPLETED` and preserves the first completion timestamp
- **THEN** the next roadmap-node retrieval returns each newly satisfied dependent node as `AVAILABLE`

#### Scenario: Keep an unsuccessful assessment node incomplete
- **WHEN** a required submission is `SUBMITTED`, `EVALUATING`, `NEEDS_CHANGES`, or `FAILED`
- **THEN** the associated node remains `IN_PROGRESS` and dependent nodes remain locked unless independently satisfied

#### Scenario: Replay a passing result
- **WHEN** the same trusted `PASSED` result is processed more than once
- **THEN** the backend does not create duplicate progress rows, replace the first completion timestamp, or repeat an unlock side effect

#### Scenario: Reject manual assessment completion
- **WHEN** a learner calls the lesson-completion endpoint for a `PRACTICE` or `PROJECT` node with any submission state
- **THEN** the backend continues to reject manual completion and requires a trusted passing submission result
