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
@Table(name = "deployment_environment")
@SQLDelete(sql = "UPDATE deployment_environment SET deleted_at = CURRENT_TIMESTAMP WHERE environment_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class DeploymentEnvironment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "environment_id")
    private UUID environmentId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50)
    private String platform;

    @Column(name = "container_image", length = 200)
    private String containerImage;

    @Column(name = "host_name", length = 100)
    private String hostName;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(UUID environmentId) {
        this.environmentId = environmentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getContainerImage() {
        return containerImage;
    }

    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
