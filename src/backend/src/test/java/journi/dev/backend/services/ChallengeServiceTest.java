package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.dtos.responses.ChallengeResponse;
import journi.dev.backend.configurations.PracticeSubmissionProperties;
import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ForbiddenException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.SubmissionRepository;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {
    @Mock
    private SkillNodeRepository skillNodeRepository;
    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private UserNodeProgressService userNodeProgressService;

    private ChallengeService challengeService;

    @BeforeEach
    void setUp() {
        PracticeSubmissionProperties properties = new PracticeSubmissionProperties();
        properties.setEnabled(true);
        challengeService = new ChallengeService(
                skillNodeRepository,
                challengeRepository,
                submissionRepository,
                userNodeProgressService,
                new ObjectMapper(),
                properties);
    }

    @Test
    void returnsUnlockedChallengeWithOwnedCurrentSubmissionAndNoEvaluatorFields() throws Exception {
        User user = user();
        SkillNode node = node(NodeType.PRACTICE);
        Challenge challenge = challenge(node);
        Submission submission = submission(user, challenge);
        when(skillNodeRepository.findById(node.getNodeId())).thenReturn(Optional.of(node));
        when(userNodeProgressService.getComputedStatus(user, node)).thenReturn(ProgressStatus.IN_PROGRESS);
        when(challengeRepository.findByNode_NodeId(node.getNodeId())).thenReturn(List.of(challenge));
        when(submissionRepository.findFirstByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
                user.getUserId(), challenge.getChallengeId())).thenReturn(Optional.of(submission));

        ChallengeResponse response = challengeService.getChallenge(node.getNodeId(), user);

        assertThat(response.progressStatus()).isEqualTo(ProgressStatus.IN_PROGRESS);
        assertThat(response.acceptanceCriteria()).containsExactly("Uses a typed map");
        assertThat(response.currentSubmission().submissionId()).isEqualTo(submission.getSubmissionId());
        String json = new ObjectMapper().findAndRegisterModules().writeValueAsString(response);
        assertThat(json).doesNotContain("graderImage", "graderCommand", "failureCategory", "outputExcerpt");
    }

    @Test
    void returnsCompletedChallengeReadOnly() {
        User user = user();
        SkillNode node = node(NodeType.PROJECT);
        Challenge challenge = challenge(node);
        challenge.setEvaluationEnabled(false);
        when(skillNodeRepository.findById(node.getNodeId())).thenReturn(Optional.of(node));
        when(userNodeProgressService.getComputedStatus(user, node)).thenReturn(ProgressStatus.COMPLETED);
        when(challengeRepository.findByNode_NodeId(node.getNodeId())).thenReturn(List.of(challenge));
        when(submissionRepository.findFirstByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
                user.getUserId(), challenge.getChallengeId())).thenReturn(Optional.empty());

        ChallengeResponse response = challengeService.getChallenge(node.getNodeId(), user);

        assertThat(response.progressStatus()).isEqualTo(ProgressStatus.COMPLETED);
        assertThat(response.submissionEnabled()).isFalse();
    }

    @Test
    void rejectsLockedChallengeBeforeLoadingChallengeMetadata() {
        User user = user();
        SkillNode node = node(NodeType.PRACTICE);
        when(skillNodeRepository.findById(node.getNodeId())).thenReturn(Optional.of(node));
        when(userNodeProgressService.getComputedStatus(user, node)).thenReturn(ProgressStatus.LOCKED);

        assertThatThrownBy(() -> challengeService.getChallenge(node.getNodeId(), user))
                .isInstanceOf(ForbiddenException.class);
        verify(challengeRepository, never()).findByNode_NodeId(node.getNodeId());
    }

    @Test
    void rejectsUnsupportedLessonNode() {
        User user = user();
        SkillNode node = node(NodeType.LESSON);
        when(skillNodeRepository.findById(node.getNodeId())).thenReturn(Optional.of(node));

        assertThatThrownBy(() -> challengeService.getChallenge(node.getNodeId(), user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("practice and project");
    }

    @Test
    void rejectsAssessmentWithoutRequiredChallenge() {
        User user = user();
        SkillNode node = node(NodeType.PRACTICE);
        when(skillNodeRepository.findById(node.getNodeId())).thenReturn(Optional.of(node));
        when(userNodeProgressService.getComputedStatus(user, node)).thenReturn(ProgressStatus.AVAILABLE);
        when(challengeRepository.findByNode_NodeId(node.getNodeId())).thenReturn(List.of());

        assertThatThrownBy(() -> challengeService.getChallenge(node.getNodeId(), user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void submissionAccessRejectsDisabledEvaluation() {
        User user = user();
        SkillNode node = node(NodeType.PRACTICE);
        Challenge challenge = challenge(node);
        challenge.setEvaluationEnabled(false);
        when(challengeRepository.findById(challenge.getChallengeId())).thenReturn(Optional.of(challenge));
        when(userNodeProgressService.getComputedStatus(user, node)).thenReturn(ProgressStatus.AVAILABLE);

        assertThatThrownBy(() -> challengeService.requireAccessibleChallenge(challenge.getChallengeId(), user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not enabled");
    }

    private User user() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        return user;
    }

    private SkillNode node(NodeType nodeType) {
        SkillNode node = new SkillNode();
        node.setNodeId(UUID.randomUUID());
        node.setNodeType(nodeType);
        return node;
    }

    private Challenge challenge(SkillNode node) {
        Challenge challenge = new Challenge();
        challenge.setChallengeId(UUID.randomUUID());
        challenge.setNode(node);
        challenge.setTitle("Collections challenge");
        challenge.setDescription("Build a catalog");
        challenge.setDifficulty("MEDIUM");
        challenge.setMaxScore(100);
        challenge.setPassingScore(80);
        challenge.setTimeoutSeconds(120);
        challenge.setIsRequired(true);
        challenge.setInstructions("Implement the supplied contracts");
        challenge.setAcceptanceCriteriaJson("[\"Uses a typed map\"]");
        challenge.setHintsJson("[\"Start with ISBN identity\"]");
        challenge.setExpectedArtifactsJson("[\"pom.xml\"]");
        challenge.setStarterRepositoryUrl("https://github.com/example/catalog");
        challenge.setGraderImage("private@sha256:" + "a".repeat(64));
        challenge.setGraderCommandJson("[\"/grader/run.sh\"]");
        challenge.setEvaluationEnabled(true);
        return challenge;
    }

    private Submission submission(User user, Challenge challenge) {
        Submission submission = new Submission();
        submission.setSubmissionId(UUID.randomUUID());
        submission.setUser(user);
        submission.setChallenge(challenge);
        submission.setAttemptNumber(2);
        submission.setStatus(SubmissionStatus.EVALUATING);
        submission.setSubmittedAt(LocalDateTime.now());
        return submission;
    }
}
