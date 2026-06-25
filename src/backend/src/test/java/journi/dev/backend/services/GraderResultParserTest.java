package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionFailureCategory;
import journi.dev.backend.entities.SubmissionStatus;

class GraderResultParserTest {
    @TempDir
    Path workspace;

    private PracticeGraderProperties properties;
    private GraderResultParser parser;

    @BeforeEach
    void setUp() throws Exception {
        properties = new PracticeGraderProperties();
        properties.setMaxResultBytes(1024);
        Files.createDirectories(workspace.resolve(".journi"));
        parser = new GraderResultParser(new ObjectMapper(), properties);
    }

    @Test
    void derivesPassedStatusFromServerOwnedPassingScore() throws Exception {
        Files.writeString(workspace.resolve(".journi/result.json"), """
                {"score":90,"summary":"All checks passed","criteria":[{"criterion":"Typed collections","passed":true,"message":"No raw types"}]}
                """);

        EvaluationResult result = parser.parse(job(), workspace, "runner output");

        assertThat(result.status()).isEqualTo(SubmissionStatus.PASSED);
        assertThat(result.score()).isEqualTo(90);
        assertThat(result.criteria()).singleElement().satisfies(item -> assertThat(item.passed()).isTrue());
    }

    @Test
    void derivesNeedsChangesWithoutTrustingAStatusField() throws Exception {
        Files.writeString(workspace.resolve(".journi/result.json"), """
                {"status":"PASSED","score":50,"summary":"One check failed","criteria":[{"criterion":"Duplicates","passed":false,"message":"Duplicate ISBN accepted"}]}
                """);

        assertThat(parser.parse(job(), workspace, "").status()).isEqualTo(SubmissionStatus.NEEDS_CHANGES);
    }

    @Test
    void rejectsMalformedOversizedAndSymlinkResults() throws Exception {
        Path result = workspace.resolve(".journi/result.json");
        Files.writeString(result, "not-json");
        assertInvalidResult(result);

        Files.writeString(result, "x".repeat(1025));
        assertInvalidResult(result);

        Files.delete(result);
        Path outside = workspace.resolve("outside.json");
        Files.writeString(outside, "{}");
        try {
            Files.createSymbolicLink(result, outside);
            assertInvalidResult(result);
        } catch (UnsupportedOperationException exception) {
            assertThat(Files.exists(outside)).isTrue();
        }
    }

    private void assertInvalidResult(Path ignored) {
        assertThatThrownBy(() -> parser.parse(job(), workspace, ""))
                .isInstanceOf(EvaluationException.class)
                .extracting(exception -> ((EvaluationException) exception).getCategory())
                .isEqualTo(SubmissionFailureCategory.RESULT_INVALID);
    }

    private EvaluationJob job() {
        return new EvaluationJob(
                UUID.randomUUID(), "https://github.com/example/catalog", "main", "a".repeat(40),
                80, 100, 120, "grader@sha256:" + "b".repeat(64), List.of("/grader/run.sh"));
    }
}
