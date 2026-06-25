package journi.dev.backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionFailureCategory;
import journi.dev.backend.entities.SubmissionStatus;

@Component
public class GraderResultParser {
    private static final int MAX_SUMMARY_LENGTH = 500;
    private static final int MAX_CRITERION_LENGTH = 300;
    private static final int MAX_MESSAGE_LENGTH = 500;

    private final ObjectMapper objectMapper;
    private final PracticeGraderProperties properties;

    public GraderResultParser(ObjectMapper objectMapper, PracticeGraderProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public EvaluationResult parse(EvaluationJob job, Path workspace, String boundedRunnerOutput)
            throws EvaluationException {
        Path resultFile = workspace.resolve(".journi/result.json").normalize();
        if (!resultFile.startsWith(workspace.normalize())
                || !Files.isRegularFile(resultFile, LinkOption.NOFOLLOW_LINKS)) {
            throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                    "The grader did not produce a trustworthy result");
        }

        try {
            long size = Files.size(resultFile);
            if (size <= 0 || size > properties.getMaxResultBytes()) {
                throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                        "The grader result exceeded the allowed size");
            }
            JsonNode root = objectMapper.readTree(Files.readAllBytes(resultFile));
            int score = requiredScore(root, job.maxScore());
            String summary = boundedRequiredText(root.path("summary"), MAX_SUMMARY_LENGTH, "summary");
            List<EvaluationResult.CriterionResult> criteria = readCriteria(root.path("criteria"));
            String outputExcerpt = bounded(
                    root.path("outputExcerpt").asText(boundedRunnerOutput == null ? "" : boundedRunnerOutput),
                    properties.getMaxOutputBytes());
            SubmissionStatus status = score >= job.passingScore()
                    ? SubmissionStatus.PASSED
                    : SubmissionStatus.NEEDS_CHANGES;
            return new EvaluationResult(status, score, summary, criteria, outputExcerpt);
        } catch (EvaluationException exception) {
            throw exception;
        } catch (JsonProcessingException exception) {
            throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                    "The grader result was malformed", exception);
        } catch (IOException exception) {
            throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                    "The grader result could not be read", exception);
        }
    }

    private int requiredScore(JsonNode root, int maxScore) throws EvaluationException {
        JsonNode value = root.path("score");
        if (!value.isIntegralNumber() || value.intValue() < 0 || value.intValue() > maxScore) {
            throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                    "The grader returned an invalid score");
        }
        return value.intValue();
    }

    private List<EvaluationResult.CriterionResult> readCriteria(JsonNode value) throws EvaluationException {
        if (!value.isArray() || value.isEmpty() || value.size() > 20) {
            throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                    "The grader returned invalid acceptance-criterion feedback");
        }
        List<EvaluationResult.CriterionResult> criteria = new ArrayList<>();
        for (JsonNode item : value) {
            if (!item.isObject() || !item.path("passed").isBoolean()) {
                throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                        "The grader returned malformed acceptance-criterion feedback");
            }
            criteria.add(new EvaluationResult.CriterionResult(
                    boundedRequiredText(item.path("criterion"), MAX_CRITERION_LENGTH, "criterion"),
                    item.path("passed").booleanValue(),
                    boundedRequiredText(item.path("message"), MAX_MESSAGE_LENGTH, "message")));
        }
        return List.copyOf(criteria);
    }

    private String boundedRequiredText(JsonNode value, int limit, String field) throws EvaluationException {
        if (!value.isTextual() || value.asText().isBlank()) {
            throw new EvaluationException(SubmissionFailureCategory.RESULT_INVALID,
                    "The grader result is missing " + field);
        }
        return bounded(value.asText().trim(), limit);
    }

    private String bounded(String value, int limit) {
        return value.length() <= limit ? value : value.substring(0, limit);
    }
}
