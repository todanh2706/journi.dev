package journi.dev.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionFailureCategory;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.repositories.SubmissionRepository;

@Service
public class SubmissionEvaluationStateService {
    private final SubmissionRepository submissionRepository;
    private final UserNodeProgressService userNodeProgressService;
    private final PracticeGraderProperties properties;
    private final ObjectMapper objectMapper;

    public SubmissionEvaluationStateService(
            SubmissionRepository submissionRepository,
            UserNodeProgressService userNodeProgressService,
            PracticeGraderProperties properties,
            ObjectMapper objectMapper) {
        this.submissionRepository = submissionRepository;
        this.userNodeProgressService = userNodeProgressService;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Optional<EvaluationJob> claim(UUID submissionId) {
        LocalDateTime startedAt = LocalDateTime.now();
        int claimed = submissionRepository.claimForEvaluation(
                submissionId,
                SubmissionStatus.SUBMITTED,
                SubmissionStatus.EVALUATING,
                startedAt,
                startedAt.plus(properties.getLeaseDuration()));
        if (claimed == 0) {
            return Optional.empty();
        }

        Submission submission = submissionRepository.findById(submissionId).orElseThrow();
        if (!submission.isEvaluationEligible()) {
            failClaimed(submission, SubmissionFailureCategory.INTERNAL_ERROR,
                    "This submission is missing required evaluation metadata");
            return Optional.empty();
        }
        return Optional.of(new EvaluationJob(
                submission.getSubmissionId(),
                submission.getRepositoryUrl(),
                submission.getBranchName(),
                submission.getCommitHash(),
                submission.getChallenge().getPassingScore(),
                submission.getChallenge().getMaxScore(),
                submission.getChallenge().getTimeoutSeconds(),
                submission.getChallenge().getGraderImage(),
                readCommand(submission.getChallenge().getGraderCommandJson())));
    }

    @Transactional
    public boolean recordResult(UUID submissionId, EvaluationResult result) {
        Submission submission = submissionRepository.findByIdForTerminalUpdate(submissionId).orElse(null);
        if (submission == null || submission.getStatus() != SubmissionStatus.EVALUATING) {
            return false;
        }
        if (result.status() != SubmissionStatus.PASSED && result.status() != SubmissionStatus.NEEDS_CHANGES) {
            throw new IllegalArgumentException("Evaluation result must be PASSED or NEEDS_CHANGES");
        }

        submission.setStatus(result.status());
        submission.setScore(result.score());
        submission.setResultSummary(bounded(result.summary(), 500));
        submission.setFeedbackJson(writeFeedback(result.criteria()));
        submission.setOutputExcerpt(bounded(result.outputExcerpt(), properties.getMaxOutputBytes()));
        submission.setFailureCategory(null);
        submission.setEvaluationLeaseUntil(null);
        submission.setEvaluationCompletedAt(LocalDateTime.now());
        submissionRepository.save(submission);

        if (result.status() == SubmissionStatus.PASSED
                && Boolean.TRUE.equals(submission.getChallenge().getIsRequired())) {
            userNodeProgressService.completeAssessmentFromPassedSubmission(
                    submission.getUser(), submission.getChallenge().getNode());
        }
        return true;
    }

    @Transactional
    public boolean recordFailure(UUID submissionId, SubmissionFailureCategory category, String summary) {
        Submission submission = submissionRepository.findByIdForTerminalUpdate(submissionId).orElse(null);
        if (submission == null || submission.getStatus() != SubmissionStatus.EVALUATING) {
            return false;
        }
        failClaimed(submission, category, summary);
        return true;
    }

    private void failClaimed(Submission submission, SubmissionFailureCategory category, String summary) {
        submission.setStatus(SubmissionStatus.FAILED);
        submission.setFailureCategory(category);
        submission.setResultSummary(bounded(summary, 500));
        submission.setEvaluationLeaseUntil(null);
        submission.setEvaluationCompletedAt(LocalDateTime.now());
        submissionRepository.save(submission);
    }

    private List<String> readCommand(String json) {
        try {
            JsonNode value = objectMapper.readTree(json);
            if (!value.isArray() || value.isEmpty()) {
                throw new IllegalStateException("Stored grader command is invalid");
            }
            return java.util.stream.StreamSupport.stream(value.spliterator(), false)
                    .filter(JsonNode::isTextual)
                    .map(JsonNode::asText)
                    .toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored grader command is invalid", exception);
        }
    }

    private String writeFeedback(List<EvaluationResult.CriterionResult> criteria) {
        try {
            return objectMapper.writeValueAsString(criteria);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not store grader feedback", exception);
        }
    }

    private String bounded(String value, int limit) {
        if (value == null) {
            return null;
        }
        return value.length() <= limit ? value : value.substring(0, limit);
    }
}
