package journi.dev.backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import journi.dev.backend.entities.HeatmapStreak;

public interface HeatmapStreakRepository extends JpaRepository<HeatmapStreak, UUID> {
}
