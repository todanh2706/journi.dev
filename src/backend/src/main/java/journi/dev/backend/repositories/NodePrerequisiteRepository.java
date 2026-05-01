package journi.dev.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodePrerequisiteId;

public interface NodePrerequisiteRepository extends JpaRepository<NodePrerequisite, NodePrerequisiteId> {
}
