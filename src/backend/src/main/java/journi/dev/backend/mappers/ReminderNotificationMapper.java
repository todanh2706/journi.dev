package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.ReminderNotificationRequest;
import journi.dev.backend.dtos.responses.ReminderNotificationResponse;
import journi.dev.backend.entities.ReminderNotification;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderNotificationMapper {
    ReminderNotificationResponse toResponse(ReminderNotification entity);
    ReminderNotification toEntity(ReminderNotificationRequest request);
}
