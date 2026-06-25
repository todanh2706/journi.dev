package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import journi.dev.backend.exceptions.BadRequestException;

class GitHubApiRevisionVerifierTest {
    @Test
    void normalizesSupportedGitHubRepositoryUrl() {
        GitHubApiRevisionVerifier.RepositoryCoordinates coordinates =
                GitHubApiRevisionVerifier.normalizeRepository("https://github.com/Example/catalog.git/");

        assertThat(coordinates.owner()).isEqualTo("Example");
        assertThat(coordinates.repository()).isEqualTo("catalog");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://github.com/example/catalog",
            "https://gitlab.com/example/catalog",
            "https://github.com@example.com/catalog",
            "https://github.com/example/catalog?token=secret",
            "https://github.com/example/catalog/extra",
            "https://github.com:443/example/catalog",
            "file:///tmp/catalog"
    })
    void rejectsRepositoryUrlsOutsideExactAllowlist(String url) {
        assertThatThrownBy(() -> GitHubApiRevisionVerifier.normalizeRepository(url))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"../main", "/main", "main/", "feature//one", "feature one", "main~1"})
    void rejectsUnsafeBranchNames(String branch) {
        assertThatThrownBy(() -> GitHubApiRevisionVerifier.normalizeBranch(branch))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void normalizesFullCommitShaToLowercase() {
        assertThat(GitHubApiRevisionVerifier.normalizeCommit("ABCDEF0123456789ABCDEF0123456789ABCDEF01"))
                .isEqualTo("abcdef0123456789abcdef0123456789abcdef01");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "g234567890123456789012345678901234567890", "", "main"})
    void rejectsAnythingOtherThanFullHexCommitSha(String commitSha) {
        assertThatThrownBy(() -> GitHubApiRevisionVerifier.normalizeCommit(commitSha))
                .isInstanceOf(BadRequestException.class);
    }
}
