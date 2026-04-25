package journi.dev.backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import journi.dev.backend.entities.SkillNode;

public interface SkillNodeRepository extends JpaRepository<SkillNode, UUID> {
}
