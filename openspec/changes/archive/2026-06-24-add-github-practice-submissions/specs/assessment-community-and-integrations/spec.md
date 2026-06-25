## MODIFIED Requirements

### Requirement: Submission and Review Pipeline Domain Model
The backend SHALL use `Submission` as the implemented source of truth for learner-owned deterministic challenge attempts, including revision identity, evaluation lifecycle, score, bounded feedback, and terminal result. `PeerReview`, `CodeReview`, `AiReviewTask`, and related integration entities SHALL remain separate future-domain scaffolding and SHALL NOT participate in the current grader's pass/fail decision or assessment-node completion.

#### Scenario: Inspecting the implemented submission pipeline
- **WHEN** a contributor traces a GitHub practice attempt through the current backend
- **THEN** they can follow the required `Challenge` to the learner-owned `Submission`, Redis job, deterministic grader result, and progress transition
- **THEN** no peer-review, AI-review, webhook, or connected-repository row is required for evaluation

#### Scenario: Inspecting future review entities
- **WHEN** a contributor reviews `PeerReview`, `CodeReview`, or `AiReviewTask`
- **THEN** those entities are identifiable as future extension points rather than dependencies of the implemented deterministic grading flow
