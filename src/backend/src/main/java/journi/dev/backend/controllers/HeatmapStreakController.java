package journi.dev.backend.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import journi.dev.backend.dtos.requests.HeatmapStreakRequest;
import journi.dev.backend.dtos.responses.HeatmapStreakResponse;
import journi.dev.backend.services.HeatmapStreakService;

@RestController
@RequestMapping("/api/v1/heatmap-streak")
public class HeatmapStreakController {
    private final HeatmapStreakService heatmapStreakService;

    public HeatmapStreakController(HeatmapStreakService heatmapStreakService) {
        this.heatmapStreakService = heatmapStreakService;
    }

    public ResponseEntity<HeatmapStreakResponse> createHeadmapStreak(
            @PathVariable UUID userId,
            @RequestBody HeatmapStreakRequest request) {
        HeatmapStreakResponse response = heatmapStreakService.createHeatmapStreak(userId, request);
    }
}
