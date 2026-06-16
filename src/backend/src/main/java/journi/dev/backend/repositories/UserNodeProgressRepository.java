package journi.dev.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import journi.dev.backend.entities.UserNodeProgress;

public interface UserNodeProgressRepository extends JpaRepository<UserNodeProgress, UUID> {
    List<UserNodeProgress> findByUser_UserId(UUID userId);

    List<UserNodeProgress> findByUser_UserIdAndNode_NodeIdIn(UUID userId, Collection<UUID> nodeIds);

    Optional<UserNodeProgress> findByUser_UserIdAndNode_NodeId(UUID userId, UUID nodeId);
}
