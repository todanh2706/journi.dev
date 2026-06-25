package journi.dev.backend.services;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionFailureCategory;

@Component
public class EvaluationWorkspaceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationWorkspaceManager.class);
    private final PracticeGraderProperties properties;
    private final BoundedProcessExecutor processExecutor;

    public EvaluationWorkspaceManager(
            PracticeGraderProperties properties,
            BoundedProcessExecutor processExecutor) {
        this.properties = properties;
        this.processExecutor = processExecutor;
    }

    public EvaluationWorkspace checkout(EvaluationJob job) throws EvaluationException {
        GitHubApiRevisionVerifier.normalizeRepository(job.repositoryUrl());
        Path root = properties.getWorkspaceRoot().toAbsolutePath().normalize();
        Path workspace = root.resolve("submission-" + job.submissionId() + "-" + UUID.randomUUID()).normalize();
        if (!workspace.startsWith(root)) {
            throw new EvaluationException(SubmissionFailureCategory.INTERNAL_ERROR, "Invalid workspace path");
        }

        try {
            Files.createDirectories(root);
            Files.createDirectory(workspace);
            runGit(workspace, List.of("git", "init", "--quiet"), properties.getCloneTimeout());
            runGit(workspace, List.of("git", "remote", "add", "origin", job.repositoryUrl()),
                    properties.getCloneTimeout());
            runGit(workspace, List.of("git", "fetch", "--quiet", "--depth=1", "origin", job.commitSha()),
                    properties.getCloneTimeout());
            runGit(workspace, List.of("git", "checkout", "--quiet", "--detach", "FETCH_HEAD"),
                    properties.getCloneTimeout());

            BoundedProcessExecutor.ProcessResult revision = processExecutor.execute(
                    List.of("git", "rev-parse", "HEAD"), workspace, Duration.ofSeconds(10), 1024);
            if (!revision.succeeded() || !job.commitSha().equals(revision.output().trim())) {
                throw new EvaluationException(
                        SubmissionFailureCategory.COMMIT_NOT_FOUND,
                        "The checked out revision did not match the submitted commit");
            }
            if (calculateSize(workspace) > properties.getMaxRepositoryBytes()) {
                throw new EvaluationException(
                        SubmissionFailureCategory.CLONE_FAILED,
                        "The repository exceeds the evaluation size limit");
            }

            Path resultDirectory = workspace.resolve(".journi");
            deleteRecursively(resultDirectory);
            Files.createDirectory(resultDirectory);
            makeWorkspaceWritable(workspace);
            return new EvaluationWorkspace(root, workspace);
        } catch (EvaluationException exception) {
            deleteQuietly(workspace);
            throw exception;
        } catch (IOException exception) {
            deleteQuietly(workspace);
            throw new EvaluationException(SubmissionFailureCategory.CLONE_FAILED,
                    "The repository could not be prepared for evaluation", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            deleteQuietly(workspace);
            throw new EvaluationException(SubmissionFailureCategory.CLONE_FAILED,
                    "Repository preparation was interrupted", exception);
        }
    }

    private void runGit(Path workspace, List<String> command, Duration timeout)
            throws IOException, InterruptedException, EvaluationException {
        BoundedProcessExecutor.ProcessResult result = processExecutor.execute(
                command, workspace, timeout, properties.getMaxOutputBytes());
        if (result.timedOut()) {
            throw new EvaluationException(SubmissionFailureCategory.CLONE_FAILED,
                    "GitHub repository preparation timed out");
        }
        if (!result.succeeded()) {
            throw new EvaluationException(SubmissionFailureCategory.CLONE_FAILED,
                    "GitHub repository preparation failed");
        }
    }

    private long calculateSize(Path workspace) throws IOException {
        AtomicLong size = new AtomicLong();
        Files.walkFileTree(workspace, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                long total = size.addAndGet(attributes.size());
                return total > properties.getMaxRepositoryBytes()
                        ? FileVisitResult.TERMINATE
                        : FileVisitResult.CONTINUE;
            }
        });
        return size.get();
    }

    private void makeWorkspaceWritable(Path workspace) {
        try {
            Set<PosixFilePermission> permissions = EnumSet.allOf(PosixFilePermission.class);
            Files.walkFileTree(workspace, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes)
                        throws IOException {
                    Files.setPosixFilePermissions(directory, permissions);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    if (!Files.isSymbolicLink(file)) {
                        Files.setPosixFilePermissions(file, permissions);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (UnsupportedOperationException | IOException exception) {
            LOGGER.debug("Workspace POSIX permissions could not be adjusted", exception);
        }
    }

    private void deleteQuietly(Path path) {
        try {
            deleteRecursively(path);
        } catch (IOException exception) {
            LOGGER.warn("Could not clean evaluation workspace {}", path, exception);
        }
    }

    static void deleteRecursively(Path path) throws IOException {
        if (path == null || !Files.exists(path, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException exception) throws IOException {
                if (exception != null) {
                    throw exception;
                }
                Files.deleteIfExists(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static final class EvaluationWorkspace implements AutoCloseable {
        private final Path root;
        private final Path path;

        EvaluationWorkspace(Path root, Path path) {
            this.root = root;
            this.path = path;
        }

        public Path path() {
            return path;
        }

        @Override
        public void close() throws EvaluationException {
            if (!path.normalize().startsWith(root.normalize())) {
                throw new EvaluationException(SubmissionFailureCategory.INTERNAL_ERROR,
                        "Refused to clean an invalid workspace path");
            }
            try {
                deleteRecursively(path);
            } catch (IOException exception) {
                throw new EvaluationException(SubmissionFailureCategory.INTERNAL_ERROR,
                        "Evaluation workspace cleanup failed", exception);
            }
        }
    }
}
