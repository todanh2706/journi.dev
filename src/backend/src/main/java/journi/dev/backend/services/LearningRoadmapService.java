package journi.dev.backend.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import journi.dev.backend.dtos.requests.LearningRoadmapRequest;
import journi.dev.backend.dtos.responses.LearningRoadmapResponse;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.UserRepository;

@Service
public class LearningRoadmapService {
    private final LearningRoadmapRepository roadmapRepository;
    private final UserRepository userRepository;

    public LearningRoadmapService(LearningRoadmapRepository roadmapRepository, UserRepository userRepository) {
        this.roadmapRepository = roadmapRepository;
        this.userRepository = userRepository;
    }

    public LearningRoadmapResponse createRoadmap(UUID userId, LearningRoadmapRequest request) {
        User owner = userRepository.findById(userId).orElse(null);

        LearningRoadmap roadmap = new LearningRoadmap();
        roadmap.setOwner(owner);
        roadmap.setTitle(request.getTitle());
        roadmap.setDescription(request.getDescription());
        roadmap.setVisibility(request.getVisibility());
        roadmap.setIsDynamic(request.getIsDynamic() != null ? request.getIsDynamic() : false);

        roadmap.setCreatedAt(LocalDateTime.now());
        roadmap.setCreatedBy(owner.getUserId()); // TO DO: automatically create roadmap

        LearningRoadmap savedRoadmap = roadmapRepository.save(roadmap);

        return new LearningRoadmapResponse(
                savedRoadmap.getRoadmapId(),
                savedRoadmap.getDescription(),
                savedRoadmap.getVisibility(),
                savedRoadmap.getIsDynamic(),
                savedRoadmap.getCreatedBy(),
                savedRoadmap.getUpdatedBy(),
                savedRoadmap.getCreatedAt(),
                savedRoadmap.getUpdatedAt(),
                savedRoadmap.getDeletedAt());
    }

    public LearningRoadmapResponse getRoadmapById(UUID roadmapId) {
        LearningRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        return new LearningRoadmapResponse(
                roadmap.getRoadmapId(),
                roadmap.getDescription(),
                roadmap.getVisibility(),
                roadmap.getIsDynamic(),
                roadmap.getCreatedBy(),
                roadmap.getUpdatedBy(),
                roadmap.getCreatedAt(),
                roadmap.getUpdatedAt(),
                roadmap.getDeletedAt());
    }
}
