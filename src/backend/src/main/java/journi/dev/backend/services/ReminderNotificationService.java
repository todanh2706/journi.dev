package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import journi.dev.backend.repositories.ReminderNotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.requests.ReminderNotificationRequest;
import journi.dev.backend.dtos.responses.ReminderNotificationResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.ReminderNotification;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.mappers.ReminderNotificationMapper;

@Service
public class ReminderNotificationService {
    private final ReminderNotificationRepository reminderNotificationRepository;
    private final UserRepository userRepository;
    private final ReminderNotificationMapper notificationMapper;

    public ReminderNotificationService(UserRepository userRepository,
            ReminderNotificationRepository reminderNotificationRepository,
            ReminderNotificationMapper notificationMapper) {
        this.userRepository = userRepository;
        this.reminderNotificationRepository = reminderNotificationRepository;
        this.notificationMapper = notificationMapper;
    }

    public List<ReminderNotificationResponse> getAllNotifications() {
        return reminderNotificationRepository.findAll().stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReminderNotificationResponse> getNotifications(UUID receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + receiverId));

        List<ReminderNotification> foundNotifications = reminderNotificationRepository.findByReceiver(receiver)
                .orElseThrow(() -> new ResourceNotFoundException("Notifications not found for user: " + receiverId));

        return foundNotifications.stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ReminderNotificationResponse getNotificationByNotification(UUID notificationId) {
        ReminderNotification foundNotification = reminderNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        return notificationMapper.toResponse(foundNotification);
    }

    @Transactional
    public ReminderNotificationResponse createNotification(UUID receiverId, ReminderNotificationRequest request) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + receiverId));

        ReminderNotification createdNotification = notificationMapper.toEntity(request);
        createdNotification.setReceiver(receiver);

        ReminderNotification savedNotification = reminderNotificationRepository.save(createdNotification);

        return notificationMapper.toResponse(savedNotification);
    }
}