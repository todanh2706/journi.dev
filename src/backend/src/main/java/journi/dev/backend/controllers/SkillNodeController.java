package journi.dev.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.services.SkillNodeService;

@RestController
@RequestMapping("/api/v1/skill-nodes")
public class SkillNodeController {
    private final SkillNodeService skillNodeService;

    public SkillNodeController(SkillNodeService skillNodeService) {
        this.skillNodeService = skillNodeService;
    }

    @GetMapping
    public List<SkillNodeResponse> getAllNodes() {
        return skillNodeService.getAllNodes();
    }

    @GetMapping("/{nodeId}")
    public SkillNodeResponse getNodeById(@PathVariable UUID nodeId) {
        return skillNodeService.getNodeById(nodeId);
    }

    @PostMapping("/{creatorId}")
    public ResponseEntity<SkillNodeResponse> createNode(@PathVariable UUID creatorId,
            @RequestBody SkillNodeRequest request) {
        return new ResponseEntity<>(skillNodeService.createSkillNode(creatorId, request), HttpStatus.CREATED);
    }
}
