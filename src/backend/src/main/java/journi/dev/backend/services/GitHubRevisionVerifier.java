package journi.dev.backend.services;

public interface GitHubRevisionVerifier {
    VerifiedGitHubRevision verify(String repositoryUrl, String branch, String commitSha);

    record VerifiedGitHubRevision(String repositoryUrl, String branch, String commitSha) {
    }
}
