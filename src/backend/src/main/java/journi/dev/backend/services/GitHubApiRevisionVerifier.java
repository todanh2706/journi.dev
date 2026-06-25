package journi.dev.backend.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ExternalServiceException;

@Service
public class GitHubApiRevisionVerifier implements GitHubRevisionVerifier {
    private static final Pattern REPOSITORY_PATH = Pattern.compile("^/([A-Za-z0-9](?:[A-Za-z0-9-]{0,38}))/([A-Za-z0-9._-]+?)(?:\\.git)?/?$");
    private static final Pattern BRANCH_PATTERN = Pattern.compile("^[A-Za-z0-9._/-]{1,100}$");
    private static final Pattern COMMIT_PATTERN = Pattern.compile("^[0-9a-f]{40}$");

    private final HttpClient httpClient;
    private final String githubToken;

    @Autowired
    public GitHubApiRevisionVerifier(@Value("${PRACTICE_GITHUB_TOKEN:}") String githubToken) {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build(), githubToken);
    }

    GitHubApiRevisionVerifier(HttpClient httpClient, String githubToken) {
        this.httpClient = httpClient;
        this.githubToken = githubToken == null ? "" : githubToken.trim();
    }

    @Override
    public VerifiedGitHubRevision verify(String repositoryUrl, String branch, String commitSha) {
        RepositoryCoordinates coordinates = normalizeRepository(repositoryUrl);
        String normalizedBranch = normalizeBranch(branch);
        String normalizedCommit = normalizeCommit(commitSha);
        String repositoryApiPath = "/repos/" + coordinates.owner() + "/" + coordinates.repository();

        requireFound(repositoryApiPath, "The public GitHub repository could not be found");
        requireFound(repositoryApiPath + "/commits/" + normalizedCommit,
                "The commit could not be found in this repository");
        requireFound(repositoryApiPath + "/branches/"
                + URLEncoder.encode(normalizedBranch, StandardCharsets.UTF_8).replace("+", "%20"),
                "The branch could not be found in this repository");

        return new VerifiedGitHubRevision(
                "https://github.com/" + coordinates.owner() + "/" + coordinates.repository(),
                normalizedBranch,
                normalizedCommit);
    }

    static RepositoryCoordinates normalizeRepository(String repositoryUrl) {
        URI uri;
        try {
            uri = URI.create(repositoryUrl == null ? "" : repositoryUrl.trim());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Repository URL must be a valid GitHub HTTPS URL");
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || !"github.com".equalsIgnoreCase(uri.getHost())
                || uri.getPort() != -1
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null) {
            throw new BadRequestException("Repository URL must use https://github.com/{owner}/{repository}");
        }

        Matcher matcher = REPOSITORY_PATH.matcher(uri.getPath());
        if (!matcher.matches()) {
            throw new BadRequestException("Repository URL must use https://github.com/{owner}/{repository}");
        }
        return new RepositoryCoordinates(matcher.group(1), matcher.group(2));
    }

    static String normalizeBranch(String branch) {
        String value = branch == null ? "" : branch.trim();
        if (!BRANCH_PATTERN.matcher(value).matches()
                || value.startsWith("/")
                || value.endsWith("/")
                || value.startsWith(".")
                || value.endsWith(".")
                || value.contains("..")
                || value.contains("//")) {
            throw new BadRequestException("Branch name is invalid");
        }
        return value;
    }

    static String normalizeCommit(String commitSha) {
        String value = commitSha == null ? "" : commitSha.trim().toLowerCase(Locale.ROOT);
        if (!COMMIT_PATTERN.matcher(value).matches()) {
            throw new BadRequestException("Commit SHA must contain exactly 40 hexadecimal characters");
        }
        return value;
    }

    private void requireFound(String apiPath, String notFoundMessage) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://api.github.com" + apiPath))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "journi-dev-practice-grader")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET();
        if (!githubToken.isBlank()) {
            builder.header("Authorization", "Bearer " + githubToken);
        }

        try {
            HttpResponse<Void> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == 404 || response.statusCode() == 422) {
                throw new BadRequestException(notFoundMessage);
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ExternalServiceException("GitHub could not verify this revision right now");
            }
        } catch (IOException exception) {
            throw new ExternalServiceException("GitHub could not be reached to verify this revision", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceException("GitHub revision verification was interrupted", exception);
        }
    }

    record RepositoryCoordinates(String owner, String repository) {
    }
}
