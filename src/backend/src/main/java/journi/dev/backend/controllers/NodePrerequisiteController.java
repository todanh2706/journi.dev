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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.subst.Node;
import journi.dev.backend.dtos.requests.NodePrerequisiteRequest;
import journi.dev.backend.dtos.responses.NodePrerequisiteResponse;
import journi.dev.backend.entities.NodePrerequisiteId;
import journi.dev.backend.services.NodePrerequisiteService;

@RestController
@RequestMapping("/api/v1/node-prerequisites")
public class NodePrerequisiteController {
    private final NodePrerequisiteService nodePrerequisiteService;

    public NodePrerequisiteController(NodePrerequisiteService nodePrerequisiteService) {
        this.nodePrerequisiteService = nodePrerequisiteService;
    }

    @GetMapping
    public ResponseEntity<List<NodePrerequisiteResponse>> getAllNodePrerequisites() {
        List<NodePrerequisiteResponse> nodePrerequisiteResponses = nodePrerequisiteService.getAllNodePrerequisite();
        if (nodePrerequisiteResponses.size() == 0) {
            return new ResponseEntity<>(nodePrerequisiteResponses, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(nodePrerequisiteResponses, HttpStatus.OK);
    }

    @GetMapping("/{parentNodeId}")
    public ResponseEntity<NodePrerequisiteResponse> getNodePrerequisiteById(@PathVariable UUID parentNodeId,
            @RequestBody NodePrerequisiteRequest request) {
        NodePrerequisiteId nodePrerequisiteId = new NodePrerequisiteId(parentNodeId, request.getChildNodeId());
        NodePrerequisiteResponse foundNodePrerequisiteResponse = nodePrerequisiteService
                .getNodePrerequisiteById(nodePrerequisiteId);

        if (foundNodePrerequisiteResponse == null)
            return new ResponseEntity<>(foundNodePrerequisiteResponse, HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(foundNodePrerequisiteResponse, HttpStatus.OK);
    }

    @PostMapping("/{parentNodeId}")
    public ResponseEntity<NodePrerequisiteResponse> createNodePrerequisite(@PathVariable UUID parentNodeId,
            @RequestBody NodePrerequisiteRequest request) {
        NodePrerequisiteResponse nodePrerequisiteResponse = nodePrerequisiteService.createNodePrerequisite(parentNodeId,
                request);
        return new ResponseEntity<>(nodePrerequisiteResponse, HttpStatus.CREATED);
    }
}
