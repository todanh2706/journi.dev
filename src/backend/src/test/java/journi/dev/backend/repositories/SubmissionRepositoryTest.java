package journi.dev.backend.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.SubmissionFailureCategory;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;

@ActiveProfiles("test")
@DataJpaTest
class SubmissionRepositoryTest {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private SkillNodeRepository skillNodeRepository;

    @Autowired
    private LearningRoadmapRepository learningRoadmapRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("[TEST] Submission history is learner scoped and newest attempt first")
    @Test
    void findSubmissionHistoryOrdersAttemptsDescending() {
        Fixture fixture = fixture("history");
        submissionRepository.saveAndFlush(submission(fixture, 1, "a".repeat(40)));
        submissionRepository.saveAndFlush(submission(fixture, 2, "b".repeat(40)));

        assertThat(submissionRepository
                .findByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
                        fixture.user().getUserId(), fixture.challenge().getChallengeId()))
                .extracting(Submission::getAttemptNumber)
                .containsExactly(2, 1);
        assertThat(submissionRepository.findMaxAttemptNumber(
                fixture.user().getUserId(), fixture.challenge().getChallengeId())).isEqualTo(2);
    }

    @DisplayName("[TEST] Learner, challenge, and commit uniquely identify a submission")
    @Test
    void duplicateLearnerChallengeCommitViolatesConstraint() {
        Fixture fixture = fixture("unique");
        String commitHash = "c".repeat(40);
        submissionRepository.saveAndFlush(submission(fixture, 1, commitHash));

        assertThatThrownBy(() -> submissionRepository.saveAndFlush(submission(fixture, 2, commitHash)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("[TEST] A submitted attempt can be claimed exactly once")
    @Test
    void claimForEvaluationIsAtomic() {
        Fixture fixture = fixture("claim");
        Submission saved = submissionRepository.saveAndFlush(submission(fixture, 1, "d".repeat(40)));
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime leaseUntil = startedAt.plusMinutes(5);

        int firstClaim = submissionRepository.claimForEvaluation(
                saved.getSubmissionId(),
                SubmissionStatus.SUBMITTED,
                SubmissionStatus.EVALUATING,
                startedAt,
                leaseUntil);
        int secondClaim = submissionRepository.claimForEvaluation(
                saved.getSubmissionId(),
                SubmissionStatus.SUBMITTED,
                SubmissionStatus.EVALUATING,
                startedAt,
                leaseUntil);

        assertThat(firstClaim).isEqualTo(1);
        assertThat(secondClaim).isZero();
        Submission claimed = submissionRepository.findById(saved.getSubmissionId()).orElseThrow();
        assertThat(claimed.getStatus()).isEqualTo(SubmissionStatus.EVALUATING);
        assertThat(claimed.getEvaluationLeaseUntil()).isEqualTo(leaseUntil);
    }

    @DisplayName("[TEST] Legacy rows without a challenge are quarantined from evaluation")
    @Test
    void unresolvedLegacySubmissionIsNotEvaluationEligible() {
        Fixture fixture = fixture("legacy");
        Submission legacy = new Submission();
        legacy.setUser(fixture.user());
        legacy.setLegacyGithubRepoId(UUID.randomUUID());
        legacy.setBranchName("main");
        legacy.setCommitHash("e".repeat(40));
        legacy.setStatus(SubmissionStatus.SUBMITTED);

        Submission saved = submissionRepository.saveAndFlush(legacy);

        assertThat(saved.isEvaluationEligible()).isFalse();
        assertThat(saved.getChallenge()).isNull();
    }

    @DisplayName("[TEST] Expired evaluation leases become retryable infrastructure failures")
    @Test
    void expiredEvaluationLeaseIsRecoveredWithoutTouchingActiveLease() {
        Fixture fixture = fixture("lease");
        Submission expired = submission(fixture, 1, "f".repeat(40));
        expired.setStatus(SubmissionStatus.EVALUATING);
        expired.setEvaluationLeaseUntil(LocalDateTime.now().minusMinutes(1));
        expired = submissionRepository.saveAndFlush(expired);
        Submission active = submission(fixture, 2, "1".repeat(40));
        active.setStatus(SubmissionStatus.EVALUATING);
        active.setEvaluationLeaseUntil(LocalDateTime.now().plusMinutes(5));
        active = submissionRepository.saveAndFlush(active);

        LocalDateTime now = LocalDateTime.now();
        int recovered = submissionRepository.failExpiredEvaluationLeases(
                SubmissionStatus.EVALUATING,
                SubmissionStatus.FAILED,
                SubmissionFailureCategory.WORKER_LOST,
                "Worker lease expired",
                now,
                now);

        assertThat(recovered).isEqualTo(1);
        assertThat(submissionRepository.findById(expired.getSubmissionId()).orElseThrow().getStatus())
                .isEqualTo(SubmissionStatus.FAILED);
        assertThat(submissionRepository.findById(active.getSubmissionId()).orElseThrow().getStatus())
                .isEqualTo(SubmissionStatus.EVALUATING);
    }

    private Fixture fixture(String suffix) {
        User user = userRepository.saveAndFlush(user("submission-" + suffix, suffix + "@example.com"));
        LearningRoadmap roadmap = learningRoadmapRepository.saveAndFlush(roadmap(user, suffix));
        SkillNode node = skillNodeRepository.saveAndFlush(node(roadmap, suffix));
        Challenge challenge = challengeRepository.saveAndFlush(challenge(node, user));
        return new Fixture(user, challenge);
    }

    private Submission submission(Fixture fixture, int attemptNumber, String commitHash) {
        Submission submission = new Submission();
        submission.setUser(fixture.user());
        submission.setChallenge(fixture.challenge());
        submission.setRepositoryUrl("https://github.com/example/" + fixture.challenge().getNode().getSlug());
        submission.setBranchName("main");
        submission.setCommitHash(commitHash);
        submission.setAttemptNumber(attemptNumber);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        return submission;
    }

    private User user(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("encoded-password");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        return user;
    }

    private LearningRoadmap roadmap(User owner, String suffix) {
        LearningRoadmap roadmap = new LearningRoadmap();
        roadmap.setOwner(owner);
        roadmap.setTitle("Submission roadmap " + suffix);
        roadmap.setSlug("submission-roadmap-" + suffix);
        roadmap.setDescription("Submission repository test roadmap");
        roadmap.setVisibility("PRIVATE");
        roadmap.setIsDynamic(false);
        roadmap.setCreatedBy(owner.getUserId());
        return roadmap;
    }

    private SkillNode node(LearningRoadmap roadmap, String suffix) {
        SkillNode node = new SkillNode();
        node.setRoadmap(roadmap);
        node.setTitle("Practice " + suffix);
        node.setSlug("practice-" + suffix);
        node.setOrderIndex(1);
        node.setNodeType(NodeType.PRACTICE);
        node.setContentJson("{}");
        node.setCreatedBy(roadmap.getOwner().getUserId());
        return node;
    }

    private Challenge challenge(SkillNode node, User owner) {
        Challenge challenge = new Challenge();
        challenge.setNode(node);
        challenge.setTitle("Practice challenge");
        challenge.setDescription("Complete the required practice");
        challenge.setDifficulty("BEGINNER");
        challenge.setMaxScore(100);
        challenge.setIsRequired(true);
        challenge.setCreatedBy(owner.getUserId());
        challenge.setEvaluationEnabled(true);
        return challenge;
    }

    private record Fixture(User user, Challenge challenge) {
    }
}
