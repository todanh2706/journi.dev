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
@Table(name = "github_webhook_event")
public class GithubWebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "webhook_event_id")
    private UUID webhookEventId;

    @Column(name = "repo_id")
    private UUID repoId;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Column(name = "delivery_id", length = 100)
    private String deliveryId;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public UUID getWebhookEventId() {
        return webhookEventId;
    }

    public void setWebhookEventId(UUID webhookEventId) {
        this.webhookEventId = webhookEventId;
    }

    public UUID getRepoId() {
        return repoId;
    }

    public void setRepoId(UUID repoId) {
        this.repoId = repoId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
