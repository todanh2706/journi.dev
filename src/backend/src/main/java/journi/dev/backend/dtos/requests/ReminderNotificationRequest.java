package journi.dev.backend.dtos.requests;

public class ReminderNotificationRequest {
    private String notificationType;
    private String channel;
    private String message;
    private String status;

    public ReminderNotificationRequest(String notificationType, String channel, String message) {
        this.notificationType = notificationType;
        this.channel = channel;
        this.message = message;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
