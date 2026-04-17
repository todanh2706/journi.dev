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

import journi.dev.backend.dtos.requests.HeatmapStreakRequest;
import journi.dev.backend.dtos.responses.HeatmapStreakResponse;
import journi.dev.backend.services.HeatmapStreakService;

@RestController
@RequestMapping("/api/v1/heatmap-streaks")
public class HeatmapStreakController {
    private final HeatmapStreakService heatmapStreakService;

    public HeatmapStreakController(HeatmapStreakService heatmapStreakService) {
        this.heatmapStreakService = heatmapStreakService;
    }

    @GetMapping
    public List<HeatmapStreakResponse> getAllStreaks() {
        return heatmapStreakService.getAllStreaks();
    }

    @PostMapping
    public ResponseEntity<HeatmapStreakResponse> createHeadmapStreak(
            @PathVariable UUID userId,
            @RequestBody HeatmapStreakRequest request) {
        HeatmapStreakResponse response = heatmapStreakService.createHeatmapStreak(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<HeatmapStreakResponse> getHeatmapStreak(@PathVariable UUID userId) {
        HeatmapStreakResponse response = heatmapStreakService.getHeatmapStreak(userId);
        if (response == null)
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
