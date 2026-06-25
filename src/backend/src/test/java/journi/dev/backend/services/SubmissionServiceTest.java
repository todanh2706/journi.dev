package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.dtos.requests.CreateSubmissionRequest;
import journi.dev.backend.dtos.responses.SubmissionResponse;
import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.SubmissionRepository;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
    @Mock
    private ChallengeService challengeService;
    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private GitHubRevisionVerifier githubRevisionVerifier;
    @Mock
    private UserNodeProgressService userNodeProgressService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionService(
                challengeService,
                challengeRepository,
                submissionRepository,
                githubRevisionVerifier,
                userNodeProgressService,
                eventPublisher,
                new ObjectMapper());
    }

    @Test
    void createsNextAttemptUpdatesProgressAndPublishesSubmissionEvent() {
        User user = user();
        Challenge challenge = challenge();
        CreateSubmissionRequest request = request();
        GitHubRevisionVerifier.VerifiedGitHubRevision revision = revision();
        when(challengeService.requireAccessibleChallenge(challenge.getChallengeId(), user)).thenReturn(challenge);
        when(githubRevisionVerifier.verify(request.repositoryUrl(), request.branch(), request.commitSha()))
                .thenReturn(revision);
        when(submissionRepository.findByUser_UserIdAndChallenge_ChallengeIdAndCommitHash(
                user.getUserId(), challenge.getChallengeId(), revision.commitSha()))
                .thenReturn(Optional.empty());
        when(challengeRepository.findByIdForSubmission(challenge.getChallengeId())).thenReturn(Optional.of(challenge));
        when(submissionRepository.findMaxAttemptNumber(user.getUserId(), challenge.getChallengeId())).thenReturn(2);
        when(submissionRepository.saveAndFlush(any(Submission.class))).thenAnswer(invocation -> {
            Submission value = invocation.getArgument(0);
            value.setSubmissionId(UUID.randomUUID());
            return value;
        });

        SubmissionResponse response = submissionService.createSubmission(user, challenge.getChallengeId(), request);

        assertThat(response.attemptNumber()).isEqualTo(3);
        assertThat(response.status()).isEqualTo(SubmissionStatus.SUBMITTED);
        assertThat(response.repositoryUrl()).isEqualTo(revision.repositoryUrl());
        verify(userNodeProgressService).markAssessmentInProgress(user, challenge.getNode());
        ArgumentCaptor<SubmissionQueuedEvent> event = ArgumentCaptor.forClass(SubmissionQueuedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().submissionId()).isEqualTo(response.submissionId());
    }

    @Test
    void duplicateCommitReturnsExistingAttemptWithoutProgressOrQueueSideEffects() {
        User user = user();
        Challenge challenge = challenge();
        CreateSubmissionRequest request = request();
        Submission existing = submission(user, challenge, SubmissionStatus.SUBMITTED);
        when(challengeService.requireAccessibleChallenge(challenge.getChallengeId(), user)).thenReturn(challenge);
        when(githubRevisionVerifier.verify(request.repositoryUrl(), request.branch(), request.commitSha()))
                .thenReturn(revision());
        when(submissionRepository.findByUser_UserIdAndChallenge_ChallengeIdAndCommitHash(
                user.getUserId(), challenge.getChallengeId(), request.commitSha()))
                .thenReturn(Optional.of(existing));

        SubmissionResponse response = submissionService.createSubmission(user, challenge.getChallengeId(), request);

        assertThat(response.submissionId()).isEqualTo(existing.getSubmissionId());
        verify(challengeRepository, never()).findByIdForSubmission(any());
        verify(userNodeProgressService, never()).markAssessmentInProgress(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void historyIsLearnerScopedAndNewestFirstFromRepositoryContract() {
        User user = user();
        Challenge challenge = challenge();
        Submission newest = submission(user, challenge, SubmissionStatus.NEEDS_CHANGES);
        newest.setAttemptNumber(2);
        Submission oldest = submission(user, challenge, SubmissionStatus.NEEDS_CHANGES);
        oldest.setAttemptNumber(1);
        when(challengeRepository.existsById(challenge.getChallengeId())).thenReturn(true);
        when(submissionRepository.findByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
                user.getUserId(), challenge.getChallengeId())).thenReturn(List.of(newest, oldest));

        assertThat(submissionService.getHistory(user, challenge.getChallengeId()))
                .extracting(SubmissionResponse::attemptNumber)
                .containsExactly(2, 1);
    }

    @Test
    void foreignSubmissionIdentifierUsesNonDisclosingNotFound() {
        User user = user();
        UUID submissionId = UUID.randomUUID();
        when(submissionRepository.findBySubmissionIdAndUser_UserId(submissionId, user.getUserId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.getSubmission(user, submissionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Submission not found");
    }

    @Test
    void retryRequeuesSameFailedAttemptWithoutIncrementingAttemptNumber() {
        User user = user();
        Challenge challenge = challenge();
        Submission failed = submission(user, challenge, SubmissionStatus.FAILED);
        failed.setResultSummary("Runner timed out");
        when(submissionRepository.findBySubmissionIdAndUser_UserId(failed.getSubmissionId(), user.getUserId()))
                .thenReturn(Optional.of(failed));
        when(submissionRepository.saveAndFlush(failed)).thenReturn(failed);

        SubmissionResponse response = submissionService.retrySubmission(user, failed.getSubmissionId());

        assertThat(response.status()).isEqualTo(SubmissionStatus.SUBMITTED);
        assertThat(response.attemptNumber()).isEqualTo(1);
        assertThat(response.resultSummary()).isNull();
        verify(eventPublisher).publishEvent(new SubmissionQueuedEvent(failed.getSubmissionId()));
    }

    @Test
    void retryRejectsLearnerResultAndActiveOrPassedAttempts() {
        User user = user();
        Challenge challenge = challenge();
        for (SubmissionStatus status : List.of(
                SubmissionStatus.SUBMITTED,
                SubmissionStatus.EVALUATING,
                SubmissionStatus.NEEDS_CHANGES,
                SubmissionStatus.PASSED)) {
            Submission submission = submission(user, challenge, status);
            when(submissionRepository.findBySubmissionIdAndUser_UserId(
                    submission.getSubmissionId(), user.getUserId())).thenReturn(Optional.of(submission));

            assertThatThrownBy(() -> submissionService.retrySubmission(user, submission.getSubmissionId()))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    private User user() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        return user;
    }

    private Challenge challenge() {
        SkillNode node = new SkillNode();
        node.setNodeId(UUID.randomUUID());
        node.setNodeType(NodeType.PRACTICE);
        Challenge challenge = new Challenge();
        challenge.setChallengeId(UUID.randomUUID());
        challenge.setNode(node);
        challenge.setIsRequired(true);
        challenge.setEvaluationEnabled(true);
        return challenge;
    }

    private CreateSubmissionRequest request() {
        return new CreateSubmissionRequest(
                "https://github.com/example/catalog",
                "main",
                "a".repeat(40));
    }

    private GitHubRevisionVerifier.VerifiedGitHubRevision revision() {
        return new GitHubRevisionVerifier.VerifiedGitHubRevision(
                "https://github.com/example/catalog",
                "main",
                "a".repeat(40));
    }

    private Submission submission(User user, Challenge challenge, SubmissionStatus status) {
        Submission submission = new Submission();
        submission.setSubmissionId(UUID.randomUUID());
        submission.setUser(user);
        submission.setChallenge(challenge);
        submission.setRepositoryUrl("https://github.com/example/catalog");
        submission.setBranchName("main");
        submission.setCommitHash("a".repeat(40));
        submission.setAttemptNumber(1);
        submission.setStatus(status);
        return submission;
    }
}
