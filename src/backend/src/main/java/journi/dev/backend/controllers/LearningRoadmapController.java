package journi.dev.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import journi.dev.backend.dtos.requests.LearningRoadmapRequest;
import journi.dev.backend.dtos.responses.LearningRoadmapResponse;
import journi.dev.backend.services.LearningRoadmapService;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/roadmaps")
public class LearningRoadmapController {
    private final LearningRoadmapService roadmapService;

    public LearningRoadmapController(LearningRoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<LearningRoadmapResponse> createRoadmap(
            @PathVariable UUID userId,
            @RequestBody LearningRoadmapRequest request) {
        LearningRoadmapResponse response = roadmapService.createRoadmap(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<LearningRoadmapResponse> getRoadmap(@PathVariable UUID roadmapId) {
        LearningRoadmapResponse response = roadmapService.getRoadmapById(roadmapId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
