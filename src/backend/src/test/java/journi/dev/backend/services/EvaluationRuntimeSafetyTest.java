package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionFailureCategory;

class EvaluationRuntimeSafetyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void cloneCommitMismatchIsRejectedAndWorkspaceIsRemoved() throws Exception {
        PracticeGraderProperties properties = new PracticeGraderProperties();
        properties.setWorkspaceRoot(temporaryDirectory);
        BoundedProcessExecutor processExecutor = org.mockito.Mockito.mock(BoundedProcessExecutor.class);
        when(processExecutor.execute(anyList(), any(), any(), anyInt())).thenAnswer(invocation -> {
            List<String> command = invocation.getArgument(0);
            if (command.contains("rev-parse")) {
                return new BoundedProcessExecutor.ProcessResult(0, false, "b".repeat(40));
            }
            return new BoundedProcessExecutor.ProcessResult(0, false, "");
        });
        EvaluationWorkspaceManager manager = new EvaluationWorkspaceManager(properties, processExecutor);

        assertThatThrownBy(() -> manager.checkout(job()))
                .isInstanceOf(EvaluationException.class)
                .extracting(exception -> ((EvaluationException) exception).getCategory())
                .isEqualTo(SubmissionFailureCategory.COMMIT_NOT_FOUND);
        try (var children = Files.list(temporaryDirectory)) {
            assertThat(children).isEmpty();
        }
    }

    @Test
    void timedOutContainerIsForceRemovedAndClassifiedAsInfrastructureFailure() throws Exception {
        PracticeGraderProperties properties = new PracticeGraderProperties();
        properties.setAssetsPath(temporaryDirectory.resolve("assets"));
        BoundedProcessExecutor processExecutor = org.mockito.Mockito.mock(BoundedProcessExecutor.class);
        when(processExecutor.execute(anyList(), eq(temporaryDirectory), any(), anyInt()))
                .thenReturn(new BoundedProcessExecutor.ProcessResult(-1, true, "timeout"))
                .thenReturn(new BoundedProcessExecutor.ProcessResult(0, false, "removed"));
        IsolatedContainerRunner runner = new IsolatedContainerRunner(
                new ContainerExecutionCommandFactory(properties), processExecutor, properties);

        assertThatThrownBy(() -> runner.run(job(), temporaryDirectory))
                .isInstanceOf(EvaluationException.class)
                .extracting(exception -> ((EvaluationException) exception).getCategory())
                .isEqualTo(SubmissionFailureCategory.TIMEOUT);
        verify(processExecutor, atLeastOnce()).execute(anyList(), eq(temporaryDirectory), any(), anyInt());
    }

    @Test
    void processOutputIsCappedWhileRemainingOutputIsDrained() throws Exception {
        BoundedProcessExecutor executor = new BoundedProcessExecutor();

        BoundedProcessExecutor.ProcessResult result = executor.execute(
                List.of("bash", "-c", "printf '1234567890abcdefghijklmnopqrstuvwxyz'"),
                temporaryDirectory,
                Duration.ofSeconds(5),
                10);

        assertThat(result.succeeded()).isTrue();
        assertThat(result.output()).startsWith("1234567890").endsWith("[output truncated]");
        assertThat(result.output()).doesNotContain("abcdefghijklmnopqrstuvwxyz");
    }

    private EvaluationJob job() {
        return new EvaluationJob(
                UUID.randomUUID(), "https://github.com/example/catalog", "main", "a".repeat(40),
                80, 100, 1, "grader@sha256:" + "b".repeat(64),
                List.of("/grader/run.sh", "collections-and-generics"));
    }
}
