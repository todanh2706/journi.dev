package journi.dev.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import journi.dev.backend.repositories.ReminderNotificationRepository;

import org.springframework.stereotype.Service;

import journi.dev.backend.dtos.requests.ReminderNotificationRequest;
import journi.dev.backend.dtos.responses.ReminderNotificationResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.ReminderNotification;
import journi.dev.backend.repositories.UserRepository;

@Service
public class ReminderNotificationService {
    private final ReminderNotificationRepository reminderNotificationRepository;
    private final UserRepository userRepository;

    public ReminderNotificationService(UserRepository userRepository,
            ReminderNotificationRepository reminderNotificationRepository) {
        this.userRepository = userRepository;
        this.reminderNotificationRepository = reminderNotificationRepository;
    }

    public List<ReminderNotificationResponse> getAllNotifications() {
        return reminderNotificationRepository.findAll().stream().map(notification -> new ReminderNotificationResponse(
                notification.getNotificationId(),
                notification.getReceiver().getUserId(),
                notification.getNotificationType(),
                notification.getChannel(),
                notification.getMessage(),
                notification.getScheduledAt(),
                notification.getSentAt(),
                notification.getStatus())).collect(Collectors.toList());
    }

    public List<ReminderNotificationResponse> getNotifications(UUID receiverId) {
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (receiver == null)
            return null;

        List<ReminderNotification> foundNotifications = reminderNotificationRepository.findByReceiver(receiver)
                .orElse(null);

        if (foundNotifications == null || foundNotifications.size() == 0)
            return null;

        return foundNotifications.stream().map(notification -> new ReminderNotificationResponse(
                notification.getNotificationId(),
                notification.getReceiver().getUserId(),
                notification.getNotificationType(),
                notification.getChannel(),
                notification.getMessage(),
                notification.getScheduledAt(),
                notification.getSentAt(),
                notification.getStatus())).collect(Collectors.toList());
    }

    public ReminderNotificationResponse getNotificationByNotification(UUID notificationId) {
        ReminderNotification foundNotification = reminderNotificationRepository.findById(notificationId).orElse(null);

        if (foundNotification == null)
            return null;

        return new ReminderNotificationResponse(
                foundNotification.getNotificationId(),
                foundNotification.getReceiver().getUserId(),
                foundNotification.getNotificationType(),
                foundNotification.getChannel(),
                foundNotification.getMessage(),
                foundNotification.getScheduledAt(),
                foundNotification.getSentAt(),
                foundNotification.getStatus());
    }

    public ReminderNotificationResponse createNotification(UUID receiverId, ReminderNotificationRequest request) {
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (receiver == null)
            return null;

        ReminderNotification createdNotification = new ReminderNotification();
        createdNotification.setReceiver(receiver);
        createdNotification.setNotificationType(request.getNotificationType());
        createdNotification.setChannel(request.getChannel());
        createdNotification.setMessage(request.getMessage());
        createdNotification.setStatus(request.getStatus());

        ReminderNotification savedNotification = reminderNotificationRepository.save(createdNotification);

        return new ReminderNotificationResponse(
                savedNotification.getNotificationId(),
                savedNotification.getReceiver().getUserId(),
                savedNotification.getNotificationType(),
                savedNotification.getChannel(),
                savedNotification.getMessage(),
                savedNotification.getScheduledAt(),
                savedNotification.getSentAt(),
                savedNotification.getStatus());
    }
}