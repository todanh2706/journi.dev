package journi.dev.backend.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import journi.dev.backend.entities.LearningRoadmap;

public interface LearningRoadmapRepository extends JpaRepository<LearningRoadmap, UUID> {
}
