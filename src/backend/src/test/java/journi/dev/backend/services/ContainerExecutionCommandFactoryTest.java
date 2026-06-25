package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import journi.dev.backend.configurations.PracticeGraderProperties;

class ContainerExecutionCommandFactoryTest {
    @Test
    void createsFixedArgumentIsolatedContainerCommandWithoutShellInterpretation() throws Exception {
        PracticeGraderProperties properties = new PracticeGraderProperties();
        properties.setAssetsPath(Path.of("/safe/grader-assets"));
        ContainerExecutionCommandFactory factory = new ContainerExecutionCommandFactory(properties);
        String injectionLikeArgument = "collections; touch /tmp/escaped";
        EvaluationJob job = new EvaluationJob(
                UUID.randomUUID(),
                "https://github.com/example/catalog",
                "main",
                "a".repeat(40),
                80,
                100,
                120,
                "grader@sha256:" + "b".repeat(64),
                List.of("/grader/run.sh", injectionLikeArgument));

        List<String> command = factory.create(job, Path.of("/safe/workspace"), "grader-container");

        assertThat(command).containsSubsequence("docker", "run", "--rm");
        assertThat(command).contains("--network", "none", "--user", "65532:65532", "--read-only");
        assertThat(command).contains("--cap-drop", "ALL", "--security-opt", "no-new-privileges");
        assertThat(command).contains("--env", "HOME=/tmp", "MAVEN_CONFIG=/tmp/.m2");
        assertThat(command).contains("--cpus", "1.0", "--memory", "768m", "--pids-limit", "128");
        assertThat(command).contains(injectionLikeArgument);
        assertThat(command).doesNotContain("sh", "-c");
        assertThat(command.stream().filter(injectionLikeArgument::equals)).hasSize(1);
    }
}
