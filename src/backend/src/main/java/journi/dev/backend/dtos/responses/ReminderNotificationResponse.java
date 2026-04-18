package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReminderNotificationResponse {
    private UUID notificationId;
    private UUID receiverId;
    private String notificationType;
    private String channel;
    private String message;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String status;

    public ReminderNotificationResponse(UUID notificationId, UUID receiverId, String notificationType, String channel,
            String message, LocalDateTime scheduledAt, LocalDateTime sentAt, String status) {
        this.notificationId = notificationId;
        this.receiverId = receiverId;
        this.notificationType = notificationType;
        this.channel = channel;
        this.message = message;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.status = status;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
