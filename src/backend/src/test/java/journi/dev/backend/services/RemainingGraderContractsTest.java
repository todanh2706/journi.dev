package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class RemainingGraderContractsTest {
    @TempDir
    Path temporaryRoot;

    @ParameterizedTest
    @ValueSource(strings = {
            "jdbc-basics",
            "rest-api-development",
            "spring-data-jpa",
            "spring-security-and-jwt",
            "testing-basics",
            "docker-basics",
            "deployment-basics"
    })
    void acceptsPassingAndRejectsFailingContractFixtures(String challenge) throws Exception {
        Path passing = temporaryRoot.resolve(challenge + "-passing");
        writePassingFixture(challenge, passing);
        assertThat(run(challenge, passing).path("score").intValue()).isEqualTo(100);

        Path failing = temporaryRoot.resolve(challenge + "-failing");
        Files.createDirectories(failing);
        JsonNode failingResult = run(challenge, failing);
        assertThat(failingResult.path("score").intValue()).isZero();
        assertThat(failingResult.path("criteria").toString()).contains("false");
    }

    private void writePassingFixture(String challenge, Path workspace) throws Exception {
        Map<String, String> files = new LinkedHashMap<>();
        switch (challenge) {
            case "jdbc-basics" -> {
                files.put("pom.xml", "<project></project>");
                files.put("src/main/java/dev/journi/practice/jdbc/JdbcBookRepository.java",
                        "class JdbcBookRepository { void read() throws Exception { try (PreparedStatement statement = null) {} } }");
            }
            case "rest-api-development" -> {
                files.put("pom.xml", "<project></project>");
                files.put("src/main/java/dev/journi/catalog/controllers/BookController.java",
                        "@RestController class BookController { void create(@Valid Object request) {} }");
            }
            case "spring-data-jpa" -> {
                files.put("src/main/java/dev/journi/catalog/entities/Book.java", "@Entity class Book {}");
                files.put("src/main/java/dev/journi/catalog/entities/Author.java", "@Entity class Author {}");
                files.put("src/main/java/dev/journi/catalog/repositories/BookRepository.java",
                        "interface BookRepository extends JpaRepository<Book, Long> {}");
            }
            case "spring-security-and-jwt" -> {
                files.put("src/main/java/dev/journi/catalog/configs/SecurityConfig.java",
                        "class SecurityConfig { SecurityFilterChain chain; }");
                files.put("src/main/java/dev/journi/catalog/controllers/AuthenticationController.java",
                        "class AuthenticationController {}");
                files.put("src/main/java/dev/journi/catalog/services/AuthenticationService.java",
                        "class AuthenticationService { PasswordEncoder encoder; }");
            }
            case "testing-basics" -> {
                files.put("src/test/java/dev/journi/catalog/repositories/BookRepositoryTest.java", "class BookRepositoryTest { @Test void saves() {} }");
                files.put("src/test/java/dev/journi/catalog/services/BookServiceTest.java", "class BookServiceTest { @Test void creates() {} }");
                files.put("src/test/java/dev/journi/catalog/controllers/BookControllerTest.java", "class BookControllerTest { @Test void validates() {} }");
            }
            case "docker-basics" -> {
                files.put("Dockerfile", "FROM eclipse-temurin:25-jre\nUSER 10001\nHEALTHCHECK CMD true\n");
                files.put("compose.yaml", "services:\n  app:\n    build: .\n");
                files.put(".dockerignore", ".git\n.env\n");
            }
            case "deployment-basics" -> {
                files.put(".github/workflows/ci.yml", "steps:\n  - run: ./mvnw test\n");
                files.put(".env.example", "DATABASE_URL=\n");
                files.put("docs/release-checklist.md", "# Release\nRollback to the previous image digest.\n");
            }
            default -> throw new IllegalArgumentException("Unknown challenge " + challenge);
        }
        for (Map.Entry<String, String> file : files.entrySet()) {
            Path path = workspace.resolve(file.getKey());
            Files.createDirectories(path.getParent() == null ? workspace : path.getParent());
            Files.writeString(path, file.getValue(), StandardCharsets.UTF_8);
        }
    }

    private JsonNode run(String challenge, Path workspace) throws Exception {
        Files.createDirectories(workspace);
        Path graderRoot = Path.of("src/main/resources/grader").toAbsolutePath().normalize();
        ProcessBuilder processBuilder = new ProcessBuilder("bash", graderRoot.resolve("run.sh").toString(), challenge);
        processBuilder.environment().put("WORKSPACE_ROOT", workspace.toString());
        processBuilder.environment().put("GRADER_ROOT", graderRoot.toString());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(process.waitFor()).as(output).isZero();
        return new ObjectMapper().readTree(workspace.resolve(".journi/result.json").toFile());
    }
}
