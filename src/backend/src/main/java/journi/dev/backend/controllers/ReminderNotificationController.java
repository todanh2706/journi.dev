package journi.dev.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import journi.dev.backend.dtos.requests.ReminderNotificationRequest;
import journi.dev.backend.dtos.responses.ReminderNotificationResponse;
import journi.dev.backend.services.ReminderNotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/reminder-notifications")
public class ReminderNotificationController {
    private final ReminderNotificationService reminderNotificationService;

    public ReminderNotificationController(ReminderNotificationService reminderNotificationService) {
        this.reminderNotificationService = reminderNotificationService;
    }

    @GetMapping
    public ResponseEntity<List<ReminderNotificationResponse>> getAllNotifications() {
        List<ReminderNotificationResponse> responses = reminderNotificationService.getAllNotifications();

        if (responses.size() == 0)
            return new ResponseEntity<>(responses, HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReminderNotificationResponse>> getNotifications(@PathVariable UUID userId) {
        List<ReminderNotificationResponse> responses = reminderNotificationService.getNotifications(userId);
        if (responses.size() == 0)
            return new ResponseEntity<List<ReminderNotificationResponse>>(responses, HttpStatus.NO_CONTENT);
        return new ResponseEntity<List<ReminderNotificationResponse>>(responses, HttpStatus.OK);
    }

    @GetMapping("/notification/{notificationId}")
    public ResponseEntity<ReminderNotificationResponse> getNotificationByNotification(
            @PathVariable UUID notificationId) {
        ReminderNotificationResponse response = reminderNotificationService
                .getNotificationByNotification(notificationId);
        if (response == null)
            return new ResponseEntity<ReminderNotificationResponse>(response, HttpStatus.NO_CONTENT);
        return new ResponseEntity<ReminderNotificationResponse>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ReminderNotificationResponse> createNotification(@PathVariable UUID userId,
            @RequestBody ReminderNotificationRequest request) {
        ReminderNotificationResponse response = reminderNotificationService.createNotification(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
