package journi.dev.backend.services;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionFailureCategory;

@Component
public class IsolatedContainerRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsolatedContainerRunner.class);
    private final ContainerExecutionCommandFactory commandFactory;
    private final BoundedProcessExecutor processExecutor;
    private final PracticeGraderProperties properties;

    public IsolatedContainerRunner(
            ContainerExecutionCommandFactory commandFactory,
            BoundedProcessExecutor processExecutor,
            PracticeGraderProperties properties) {
        this.commandFactory = commandFactory;
        this.processExecutor = processExecutor;
        this.properties = properties;
    }

    public ContainerRunResult run(EvaluationJob job, Path workspace) throws EvaluationException {
        String containerName = "journi-grader-" + UUID.randomUUID();
        List<String> command = commandFactory.create(job, workspace, containerName);
        try {
            BoundedProcessExecutor.ProcessResult result = processExecutor.execute(
                    command,
                    workspace,
                    Duration.ofSeconds(job.timeoutSeconds()),
                    properties.getMaxOutputBytes());
            if (result.timedOut()) {
                forceRemove(containerName, workspace);
                throw new EvaluationException(SubmissionFailureCategory.TIMEOUT,
                        "Evaluation exceeded the challenge time limit");
            }
            return new ContainerRunResult(result.exitCode(), result.output());
        } catch (IOException exception) {
            forceRemove(containerName, workspace);
            throw new EvaluationException(SubmissionFailureCategory.RUNNER_START_FAILED,
                    "The isolated evaluation container could not be started", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            forceRemove(containerName, workspace);
            throw new EvaluationException(SubmissionFailureCategory.RUNNER_START_FAILED,
                    "Evaluation was interrupted", exception);
        }
    }

    private void forceRemove(String containerName, Path workingDirectory) {
        try {
            processExecutor.execute(
                    List.of("docker", "rm", "--force", containerName),
                    workingDirectory,
                    Duration.ofSeconds(10),
                    2048);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Interrupted while forcing cleanup of grader container {}", containerName, exception);
        } catch (IOException exception) {
            LOGGER.warn("Could not force cleanup of grader container {}", containerName, exception);
        }
    }

    public record ContainerRunResult(int exitCode, String output) {
    }
}
