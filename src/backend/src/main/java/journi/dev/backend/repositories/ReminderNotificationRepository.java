package journi.dev.backend.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import journi.dev.backend.entities.ReminderNotification;
import journi.dev.backend.entities.User;

public interface ReminderNotificationRepository extends JpaRepository<ReminderNotification, UUID> {
    Optional<List<ReminderNotification>> findByReceiver(User receiver);
}
