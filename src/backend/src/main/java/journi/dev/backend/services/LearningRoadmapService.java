package journi.dev.backend.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.requests.LearningRoadmapRequest;
import journi.dev.backend.dtos.responses.LearningRoadmapResponse;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.mappers.LearningRoadmapMapper;
import journi.dev.backend.exceptions.ResourceNotFoundException;

@Service
public class LearningRoadmapService {
    private final LearningRoadmapRepository roadmapRepository;
    private final UserRepository userRepository;
    private final LearningRoadmapMapper roadmapMapper;

    public LearningRoadmapService(LearningRoadmapRepository roadmapRepository, UserRepository userRepository, LearningRoadmapMapper roadmapMapper) {
        this.roadmapRepository = roadmapRepository;
        this.userRepository = userRepository;
        this.roadmapMapper = roadmapMapper;
    }

    @Transactional

    public LearningRoadmapResponse createRoadmap(UUID userId, LearningRoadmapRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LearningRoadmap roadmap = roadmapMapper.toEntity(request);
        roadmap.setOwner(owner);
        roadmap.setIsDynamic(request.getIsDynamic() != null ? request.getIsDynamic() : false);
        roadmap.setCreatedAt(LocalDateTime.now());
        roadmap.setCreatedBy(owner.getUserId()); // TO DO: automatically create roadmap

        LearningRoadmap savedRoadmap = roadmapRepository.save(roadmap);
        return roadmapMapper.toResponse(savedRoadmap);
    }

    public LearningRoadmapResponse getRoadmapById(UUID roadmapId) {
        LearningRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + roadmapId));

        return roadmapMapper.toResponse(roadmap);
    }
}
