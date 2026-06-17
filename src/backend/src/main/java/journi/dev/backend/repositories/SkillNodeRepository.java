package journi.dev.backend.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import journi.dev.backend.entities.SkillNode;

public interface SkillNodeRepository extends JpaRepository<SkillNode, UUID> {
    List<SkillNode> findByRoadmap_RoadmapIdOrderByOrderIndexAsc(UUID roadmapId);

    boolean existsByRoadmap_RoadmapIdAndSlug(UUID roadmapId, String slug);

    Optional<SkillNode> findByRoadmap_RoadmapIdAndSlug(UUID roadmapId, String slug);
}
