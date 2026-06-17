package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.requests.LearningRoadmapRequest;
import journi.dev.backend.dtos.responses.LearningRoadmapResponse;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.mappers.LearningRoadmapMapper;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.utils.SlugUtils;

@Service
public class LearningRoadmapService {
    private final LearningRoadmapRepository roadmapRepository;
    private final UserRepository userRepository;
    private final LearningRoadmapMapper roadmapMapper;
    private final SkillNodeService skillNodeService;

    public LearningRoadmapService(LearningRoadmapRepository roadmapRepository, UserRepository userRepository,
            LearningRoadmapMapper roadmapMapper, SkillNodeService skillNodeService) {
        this.roadmapRepository = roadmapRepository;
        this.userRepository = userRepository;
        this.roadmapMapper = roadmapMapper;
        this.skillNodeService = skillNodeService;
    }

    @Transactional
    public LearningRoadmapResponse createRoadmap(UUID userId, LearningRoadmapRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LearningRoadmap roadmap = roadmapMapper.toEntity(request);
        roadmap.setOwner(owner);
        roadmap.setSlug(resolveRoadmapSlug(request));
        roadmap.setIsDynamic(Boolean.TRUE.equals(request.getIsDynamic()));
        roadmap.setCreatedBy(owner.getUserId());

        LearningRoadmap savedRoadmap = roadmapRepository.save(roadmap);
        return roadmapMapper.toResponse(savedRoadmap);
    }

    @Transactional(readOnly = true)
    public LearningRoadmapResponse getRoadmapById(UUID roadmapId) {
        LearningRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + roadmapId));

        return roadmapMapper.toResponse(roadmap);
    }

    @Transactional(readOnly = true)
    public List<LearningRoadmapResponse> getAllRoadmaps() {
        List<LearningRoadmap> roadmaps = roadmapRepository.findAll();

        return roadmaps.stream().map(roadmapMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SkillNodeResponse> getRoadmapNodes(UUID roadmapId, User currentUser) {
        if (!roadmapRepository.existsById(roadmapId)) {
            throw new ResourceNotFoundException("Roadmap not found with id: " + roadmapId);
        }

        return skillNodeService.getNodesByRoadmap(roadmapId, currentUser);
    }

    private String resolveRoadmapSlug(LearningRoadmapRequest request) {
        String explicitSlug = SlugUtils.toSlug(request.getSlug());
        if (!explicitSlug.isBlank()) {
            if (roadmapRepository.existsBySlug(explicitSlug)) {
                throw new BadRequestException("Roadmap slug already exists: " + explicitSlug);
            }

            return explicitSlug;
        }

        String baseSlug = SlugUtils.toSlug(request.getTitle());
        if (baseSlug.isBlank()) {
            throw new BadRequestException("Roadmap slug could not be generated");
        }

        String candidateSlug = baseSlug;
        int suffix = 2;
        while (roadmapRepository.existsBySlug(candidateSlug)) {
            candidateSlug = baseSlug + "-" + suffix++;
        }

        return candidateSlug;
    }
}
