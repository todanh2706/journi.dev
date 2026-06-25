package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.SubmissionRepository;

@ExtendWith(MockitoExtension.class)
class SubmissionEvaluationStateServiceTest {
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private UserNodeProgressService progressService;

    private SubmissionEvaluationStateService stateService;

    @BeforeEach
    void setUp() {
        stateService = new SubmissionEvaluationStateService(
                submissionRepository,
                progressService,
                new PracticeGraderProperties(),
                new ObjectMapper());
    }

    @Test
    void redeliveredMessageCannotClaimAnActiveOrTerminalSubmission() {
        UUID submissionId = UUID.randomUUID();
        when(submissionRepository.claimForEvaluation(
                org.mockito.ArgumentMatchers.eq(submissionId),
                org.mockito.ArgumentMatchers.eq(SubmissionStatus.SUBMITTED),
                org.mockito.ArgumentMatchers.eq(SubmissionStatus.EVALUATING),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any())).thenReturn(0);

        assertThat(stateService.claim(submissionId)).isEmpty();
        verify(submissionRepository, never()).findById(submissionId);
    }

    @Test
    void passingTerminalResultCompletesOnlyTheSubmissionOwnerAssessment() {
        Submission submission = submission(SubmissionStatus.EVALUATING);
        EvaluationResult result = new EvaluationResult(
                SubmissionStatus.PASSED, 90, "Passed", List.of(
                        new EvaluationResult.CriterionResult("Typed map", true, "Passed")), "ok");
        when(submissionRepository.findByIdForTerminalUpdate(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));

        assertThat(stateService.recordResult(submission.getSubmissionId(), result)).isTrue();

        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.PASSED);
        verify(progressService).completeAssessmentFromPassedSubmission(
                submission.getUser(), submission.getChallenge().getNode());
    }

    @Test
    void replayedOrLateTerminalResultCannotOverwriteFirstResult() {
        Submission submission = submission(SubmissionStatus.PASSED);
        submission.setScore(90);
        when(submissionRepository.findByIdForTerminalUpdate(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        EvaluationResult late = new EvaluationResult(
                SubmissionStatus.NEEDS_CHANGES, 10, "Late", List.of(
                        new EvaluationResult.CriterionResult("Typed map", false, "Late")), "late");

        assertThat(stateService.recordResult(submission.getSubmissionId(), late)).isFalse();
        assertThat(submission.getScore()).isEqualTo(90);
        verify(progressService, never()).completeAssessmentFromPassedSubmission(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void needsChangesNeverCompletesProgress() {
        Submission submission = submission(SubmissionStatus.EVALUATING);
        when(submissionRepository.findByIdForTerminalUpdate(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        EvaluationResult result = new EvaluationResult(
                SubmissionStatus.NEEDS_CHANGES, 40, "Review", List.of(
                        new EvaluationResult.CriterionResult("Typed map", false, "Use Map<String, Book>")), "failed");

        assertThat(stateService.recordResult(submission.getSubmissionId(), result)).isTrue();
        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.NEEDS_CHANGES);
        verify(progressService, never()).completeAssessmentFromPassedSubmission(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    private Submission submission(SubmissionStatus status) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        SkillNode node = new SkillNode();
        node.setNodeId(UUID.randomUUID());
        node.setNodeType(NodeType.PRACTICE);
        Challenge challenge = new Challenge();
        challenge.setChallengeId(UUID.randomUUID());
        challenge.setNode(node);
        challenge.setIsRequired(true);
        Submission submission = new Submission();
        submission.setSubmissionId(UUID.randomUUID());
        submission.setUser(user);
        submission.setChallenge(challenge);
        submission.setStatus(status);
        return submission;
    }
}
