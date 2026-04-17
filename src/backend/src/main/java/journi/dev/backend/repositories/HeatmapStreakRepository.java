package journi.dev.backend.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import journi.dev.backend.entities.HeatmapStreak;
import journi.dev.backend.entities.User;

public interface HeatmapStreakRepository extends JpaRepository<HeatmapStreak, UUID> {
    Optional<HeatmapStreak> findByOwner(User owner);
}
