package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.mappers.SkillNodeMapper;

@Service
public class SkillNodeService {
    private final SkillNodeRepository skillNodeRepository;
    private final UserRepository userRepository;
    private final SkillNodeMapper skillNodeMapper;

    public SkillNodeService(SkillNodeRepository skillNodeRepository, UserRepository userRepository, SkillNodeMapper skillNodeMapper) {
        this.skillNodeRepository = skillNodeRepository;
        this.userRepository = userRepository;
        this.skillNodeMapper = skillNodeMapper;
    }

    @Transactional

    public SkillNodeResponse createSkillNode(UUID creatorId, SkillNodeRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorId));

        SkillNode newNode = skillNodeMapper.toEntity(request);
        SkillNode savedNode = skillNodeRepository.save(newNode);

        return skillNodeMapper.toResponse(savedNode);
    }

    public List<SkillNodeResponse> getAllNodes() {
        return skillNodeRepository.findAll().stream().map(skillNodeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SkillNodeResponse getNodeById(UUID nodeId) {
        SkillNode foundNode = skillNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill node not found with id: " + nodeId));

        return skillNodeMapper.toResponse(foundNode);
    }
}
