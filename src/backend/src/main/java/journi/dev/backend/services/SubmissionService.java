package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.dtos.requests.CreateSubmissionRequest;
import journi.dev.backend.dtos.responses.CriterionFeedbackResponse;
import journi.dev.backend.dtos.responses.SubmissionResponse;
import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.SubmissionRepository;

@Service
public class SubmissionService {
    private static final int MAX_FEEDBACK_MESSAGE_LENGTH = 500;

    private final ChallengeService challengeService;
    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final GitHubRevisionVerifier githubRevisionVerifier;
    private final UserNodeProgressService userNodeProgressService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public SubmissionService(
            ChallengeService challengeService,
            ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository,
            GitHubRevisionVerifier githubRevisionVerifier,
            UserNodeProgressService userNodeProgressService,
            ApplicationEventPublisher eventPublisher,
            ObjectMapper objectMapper) {
        this.challengeService = challengeService;
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.githubRevisionVerifier = githubRevisionVerifier;
        this.userNodeProgressService = userNodeProgressService;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SubmissionResponse createSubmission(
            User currentUser,
            UUID challengeId,
            CreateSubmissionRequest request) {
        challengeService.requireAccessibleChallenge(challengeId, currentUser);
        GitHubRevisionVerifier.VerifiedGitHubRevision revision = githubRevisionVerifier.verify(
                request.repositoryUrl(), request.branch(), request.commitSha());

        Submission existing = submissionRepository
                .findByUser_UserIdAndChallenge_ChallengeIdAndCommitHash(
                        currentUser.getUserId(), challengeId, revision.commitSha())
                .orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        Challenge lockedChallenge = challengeRepository.findByIdForSubmission(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        existing = submissionRepository
                .findByUser_UserIdAndChallenge_ChallengeIdAndCommitHash(
                        currentUser.getUserId(), challengeId, revision.commitSha())
                .orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        Submission submission = new Submission();
        submission.setUser(currentUser);
        submission.setChallenge(lockedChallenge);
        submission.setRepositoryUrl(revision.repositoryUrl());
        submission.setBranchName(revision.branch());
        submission.setCommitHash(revision.commitSha());
        submission.setAttemptNumber(submissionRepository.findMaxAttemptNumber(
                currentUser.getUserId(), challengeId) + 1);
        submission.setStatus(SubmissionStatus.SUBMITTED);

        Submission saved = submissionRepository.saveAndFlush(submission);
        userNodeProgressService.markAssessmentInProgress(currentUser, lockedChallenge.getNode());
        eventPublisher.publishEvent(new SubmissionQueuedEvent(saved.getSubmissionId()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getHistory(User currentUser, UUID challengeId) {
        requireUser(currentUser);
        if (!challengeRepository.existsById(challengeId)) {
            throw new ResourceNotFoundException("Challenge not found");
        }
        return submissionRepository
                .findByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
                        currentUser.getUserId(), challengeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(User currentUser, UUID submissionId) {
        requireUser(currentUser);
        Submission submission = submissionRepository
                .findBySubmissionIdAndUser_UserId(submissionId, currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        return toResponse(submission);
    }

    @Transactional
    public SubmissionResponse retrySubmission(User currentUser, UUID submissionId) {
        requireUser(currentUser);
        Submission submission = submissionRepository
                .findBySubmissionIdAndUser_UserId(submissionId, currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        if (submission.getStatus() != SubmissionStatus.FAILED) {
            throw new BadRequestException("Only failed infrastructure evaluations can be retried");
        }
        if (!submission.isEvaluationEligible()) {
            throw new BadRequestException("This legacy submission cannot be evaluated");
        }

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setFailureCategory(null);
        submission.setResultSummary(null);
        submission.setFeedbackJson(null);
        submission.setOutputExcerpt(null);
        submission.setScore(null);
        submission.setEvaluationLeaseUntil(null);
        submission.setEvaluationStartedAt(null);
        submission.setEvaluationCompletedAt(null);
        Submission saved = submissionRepository.saveAndFlush(submission);
        eventPublisher.publishEvent(new SubmissionQueuedEvent(saved.getSubmissionId()));
        return toResponse(saved);
    }

    SubmissionResponse toResponse(Submission submission) {
        return new SubmissionResponse(
                submission.getSubmissionId(),
                submission.getChallenge().getChallengeId(),
                submission.getRepositoryUrl(),
                submission.getBranchName(),
                submission.getCommitHash(),
                submission.getAttemptNumber(),
                submission.getStatus(),
                submission.getScore(),
                submission.getResultSummary(),
                parseFeedback(submission.getFeedbackJson()),
                submission.getOutputExcerpt(),
                submission.getStatus() == SubmissionStatus.FAILED,
                submission.getSubmittedAt(),
                submission.getEvaluationStartedAt(),
                submission.getEvaluationCompletedAt());
    }

    private List<CriterionFeedbackResponse> parseFeedback(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isArray()) {
                return List.of();
            }
            return java.util.stream.StreamSupport.stream(root.spliterator(), false)
                    .filter(JsonNode::isObject)
                    .limit(20)
                    .map(item -> new CriterionFeedbackResponse(
                            boundedText(item.path("criterion").asText("")),
                            item.path("passed").asBoolean(false),
                            boundedText(item.path("message").asText(""))))
                    .toList();
        } catch (JsonProcessingException exception) {
            return List.of();
        }
    }

    private String boundedText(String value) {
        return value.length() <= MAX_FEEDBACK_MESSAGE_LENGTH
                ? value
                : value.substring(0, MAX_FEEDBACK_MESSAGE_LENGTH);
    }

    private void requireUser(User user) {
        if (user == null) {
            throw new BadRequestException("Authenticated user is required");
        }
    }
}
