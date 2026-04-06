package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_model_config")
public class AiModelConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "config_id")
    private UUID configId;

    @Column(name = "model_provider", length = 50)
    private String modelProvider;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "api_key_encrypted", length = 255)
    private String apiKeyEncrypted;

    private Boolean enabled;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UUID getConfigId() {
        return configId;
    }

    public void setConfigId(UUID configId) {
        this.configId = configId;
    }

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getApiKeyEncrypted() {
        return apiKeyEncrypted;
    }

    public void setApiKeyEncrypted(String apiKeyEncrypted) {
        this.apiKeyEncrypted = apiKeyEncrypted;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
