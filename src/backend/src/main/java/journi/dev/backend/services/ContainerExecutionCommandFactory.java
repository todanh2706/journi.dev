package journi.dev.backend.services;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionFailureCategory;

@Component
public class ContainerExecutionCommandFactory {
    private final PracticeGraderProperties properties;

    public ContainerExecutionCommandFactory(PracticeGraderProperties properties) {
        this.properties = properties;
    }

    public List<String> create(EvaluationJob job, Path workspace, String containerName) throws EvaluationException {
        if (job.graderImage() == null
                || !job.graderImage().matches("^[^\\s]+@sha256:[0-9a-f]{64}$")) {
            throw new EvaluationException(SubmissionFailureCategory.RUNNER_START_FAILED,
                    "The challenge grader image is not pinned by digest");
        }
        if (job.graderCommand() == null || job.graderCommand().isEmpty()
                || job.graderCommand().stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new EvaluationException(SubmissionFailureCategory.RUNNER_START_FAILED,
                    "The challenge grader command is invalid");
        }

        List<String> command = new ArrayList<>(List.of(
                "docker", "run", "--rm",
                "--name", containerName,
                "--network", "none",
                "--user", "65532:65532",
                "--cpus", properties.getCpus(),
                "--memory", properties.getMemory(),
                "--memory-swap", properties.getMemory(),
                "--pids-limit", Integer.toString(properties.getPidsLimit()),
                "--read-only",
                "--security-opt", "no-new-privileges",
                "--cap-drop", "ALL",
                "--tmpfs", "/tmp:rw,noexec,nosuid,size=128m",
                "--env", "HOME=/tmp",
                "--env", "MAVEN_CONFIG=/tmp/.m2",
                "--mount", "type=bind,src=" + workspace.toAbsolutePath().normalize() + ",dst=/workspace",
                "--mount", "type=bind,src=" + properties.getAssetsPath().toAbsolutePath().normalize()
                        + ",dst=/grader,readonly",
                "--workdir", "/workspace",
                job.graderImage()));
        command.addAll(job.graderCommand());
        return List.copyOf(command);
    }
}
