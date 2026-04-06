package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "github_repository")
@SQLDelete(sql = "UPDATE github_repository SET deleted_at = CURRENT_TIMESTAMP WHERE repo_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class GithubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "repo_id")
    private UUID repoId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "github_url", columnDefinition = "TEXT")
    private String githubUrl;

    @Column(name = "repo_name", length = 150)
    private String repoName;

    @Column(name = "default_branch", length = 100)
    private String defaultBranch;

    @Column(name = "webhook_secret_encrypted", length = 255)
    private String webhookSecretEncrypted;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public UUID getRepoId() {
        return repoId;
    }

    public void setRepoId(UUID repoId) {
        this.repoId = repoId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public String getWebhookSecretEncrypted() {
        return webhookSecretEncrypted;
    }

    public void setWebhookSecretEncrypted(String webhookSecretEncrypted) {
        this.webhookSecretEncrypted = webhookSecretEncrypted;
    }

    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(LocalDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
