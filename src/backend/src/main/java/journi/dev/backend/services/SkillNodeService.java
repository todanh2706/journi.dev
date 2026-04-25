package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;

@Service
public class SkillNodeService {
    private final SkillNodeRepository skillNodeRepository;
    private final UserRepository userRepository;

    public SkillNodeService(SkillNodeRepository skillNodeRepository, UserRepository userRepository) {
        this.skillNodeRepository = skillNodeRepository;
        this.userRepository = userRepository;
    }

    public SkillNodeResponse createSkillNode(UUID creatorId, SkillNodeRequest request) {
        User creator = userRepository.findById(creatorId).orElse(null);

        if (creator == null)
            return null;

        SkillNode newNode = new SkillNode();
        newNode.setRoadmapId(request.getRoadmapId());
        newNode.setSlug(request.getSlug());
        newNode.setTitle(request.getTitle());
        newNode.setContentJson(request.getContentJson());
        newNode.setIsLocked(request.getIsLocked());
        newNode.setNodeType(request.getNodeType());
        newNode.setOrderIndex(request.getOrderIndex());

        SkillNode savedNode = skillNodeRepository.save(newNode);

        return new SkillNodeResponse(savedNode);
    }

    public List<SkillNodeResponse> getAllNodes() {
        return skillNodeRepository.findAll().stream().map(node -> new SkillNodeResponse(node))
                .collect(Collectors.toList());

    }

    public SkillNodeResponse getNodeById(UUID nodeId) {
        SkillNode foundNode = skillNodeRepository.findById(nodeId).orElse(null);

        if (foundNode == null)
            return null;

        return new SkillNodeResponse(foundNode);
    }
}
