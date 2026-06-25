package journi.dev.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import journi.dev.backend.entities.Challenge;
import jakarta.persistence.LockModeType;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByNode_NodeId(UUID nodeId);

    List<Challenge> findByNode_NodeIdIn(Collection<UUID> nodeIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select challenge from Challenge challenge where challenge.challengeId = :challengeId")
    java.util.Optional<Challenge> findByIdForSubmission(@Param("challengeId") UUID challengeId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            delete from Challenge challenge
            where challenge.node.nodeId in :nodeIds
            """)
    void deleteByNodeIds(@Param("nodeIds") Collection<UUID> nodeIds);
}
