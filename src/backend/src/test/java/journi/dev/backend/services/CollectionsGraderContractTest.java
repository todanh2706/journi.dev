package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class CollectionsGraderContractTest {
    @TempDir
    Path workspace;

    @Test
    void acceptsKnownPassingFixture() throws Exception {
        assertThat(runFixture("collections-passing").path("score").intValue()).isEqualTo(100);
    }

    @Test
    void rejectsKnownFailingFixture() throws Exception {
        JsonNode result = runFixture("collections-failing");
        assertThat(result.path("score").intValue()).isZero();
        assertThat(result.path("criteria").toString()).contains("false");
    }

    private JsonNode runFixture(String fixture) throws Exception {
        Path fixtureRoot = Path.of("src/test/resources/grader-fixtures", fixture);
        copyRecursively(fixtureRoot, workspace);
        Path graderRoot = Path.of("src/main/resources/grader").toAbsolutePath().normalize();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "bash", graderRoot.resolve("run.sh").toString(), "collections-and-generics");
        processBuilder.environment().put("WORKSPACE_ROOT", workspace.toString());
        processBuilder.environment().put("GRADER_ROOT", graderRoot.toString());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertThat(process.waitFor()).as(output).isZero();
        return new ObjectMapper().readTree(workspace.resolve(".journi/result.json").toFile());
    }

    private void copyRecursively(Path source, Path destination) throws Exception {
        try (var paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path target = destination.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.copy(path, target);
                }
            }
        }
    }
}
