package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.dtos.responses.ChallengeResponse;
import journi.dev.backend.configurations.PracticeSubmissionProperties;
import journi.dev.backend.dtos.responses.SubmissionSummaryResponse;
import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ForbiddenException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.SubmissionRepository;

@Service
public class ChallengeService {
    private final SkillNodeRepository skillNodeRepository;
    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final UserNodeProgressService userNodeProgressService;
    private final ObjectMapper objectMapper;
    private final PracticeSubmissionProperties practiceSubmissionProperties;

    public ChallengeService(
            SkillNodeRepository skillNodeRepository,
            ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository,
            UserNodeProgressService userNodeProgressService,
            ObjectMapper objectMapper,
            PracticeSubmissionProperties practiceSubmissionProperties) {
        this.skillNodeRepository = skillNodeRepository;
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.userNodeProgressService = userNodeProgressService;
        this.objectMapper = objectMapper;
        this.practiceSubmissionProperties = practiceSubmissionProperties;
    }

    @Transactional(readOnly = true)
    public ChallengeResponse getChallenge(UUID nodeId, User currentUser) {
        if (currentUser == null) {
            throw new BadRequestException("Authenticated user is required");
        }

        SkillNode node = skillNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill node not found with id: " + nodeId));
        requireSupportedAssessment(node);

        ProgressStatus progressStatus = userNodeProgressService.getComputedStatus(currentUser, node);
        if (progressStatus == ProgressStatus.LOCKED) {
            throw new ForbiddenException("Complete the required prerequisite nodes before opening this challenge");
        }

        Challenge challenge = challengeRepository.findByNode_NodeId(nodeId).stream()
                .filter(candidate -> Boolean.TRUE.equals(candidate.getIsRequired()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Required challenge not found for skill node"));

        SubmissionSummaryResponse currentSubmission = submissionRepository
                .findFirstByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
                        currentUser.getUserId(), challenge.getChallengeId())
                .map(this::toSummary)
                .orElse(null);

        return toResponse(challenge, progressStatus, currentSubmission);
    }

    @Transactional(readOnly = true)
    public Challenge requireAccessibleChallenge(UUID challengeId, User currentUser) {
        if (currentUser == null) {
            throw new BadRequestException("Authenticated user is required");
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        requireSupportedAssessment(challenge.getNode());
        if (!Boolean.TRUE.equals(challenge.getIsRequired())) {
            throw new BadRequestException("Only required assessment challenges accept submissions");
        }
        if (userNodeProgressService.getComputedStatus(currentUser, challenge.getNode()) == ProgressStatus.LOCKED) {
            throw new ForbiddenException("Complete the required prerequisite nodes before submitting this challenge");
        }
        if (!practiceSubmissionProperties.isEnabled() || !challenge.isEvaluationEnabled()) {
            throw new BadRequestException("Automated evaluation is not enabled for this challenge yet");
        }
        return challenge;
    }

    private void requireSupportedAssessment(SkillNode node) {
        if (node.getNodeType() != NodeType.PRACTICE && node.getNodeType() != NodeType.PROJECT) {
            throw new BadRequestException("Only practice and project nodes support GitHub challenges");
        }
    }

    private ChallengeResponse toResponse(
            Challenge challenge,
            ProgressStatus progressStatus,
            SubmissionSummaryResponse currentSubmission) {
        return new ChallengeResponse(
                challenge.getChallengeId(),
                challenge.getNode().getNodeId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getDifficulty(),
                challenge.getInstructions(),
                readStringList(challenge.getAcceptanceCriteriaJson()),
                readStringList(challenge.getHintsJson()),
                readStringList(challenge.getExpectedArtifactsJson()),
                challenge.getStarterRepositoryUrl(),
                challenge.getMaxScore(),
                challenge.getPassingScore(),
                challenge.getTimeoutSeconds(),
                Boolean.TRUE.equals(challenge.getIsRequired()),
                practiceSubmissionProperties.isEnabled() && challenge.isEvaluationEnabled(),
                progressStatus,
                currentSubmission);
    }

    private SubmissionSummaryResponse toSummary(Submission submission) {
        return new SubmissionSummaryResponse(
                submission.getSubmissionId(),
                submission.getAttemptNumber(),
                submission.getStatus(),
                submission.getScore(),
                submission.getSubmittedAt(),
                submission.getEvaluationCompletedAt());
    }

    private List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            JsonNode value = objectMapper.readTree(json);
            if (!value.isArray()) {
                return List.of();
            }
            return java.util.stream.StreamSupport.stream(value.spliterator(), false)
                    .filter(JsonNode::isTextual)
                    .map(JsonNode::asText)
                    .filter(item -> !item.isBlank())
                    .toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored challenge metadata is invalid", exception);
        }
    }
}
