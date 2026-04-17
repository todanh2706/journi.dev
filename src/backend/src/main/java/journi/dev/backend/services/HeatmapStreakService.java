package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import journi.dev.backend.dtos.requests.HeatmapStreakRequest;
import journi.dev.backend.dtos.responses.HeatmapStreakResponse;
import journi.dev.backend.entities.HeatmapStreak;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.HeatmapStreakRepository;
import journi.dev.backend.repositories.UserRepository;

@Service
public class HeatmapStreakService {
    private final HeatmapStreakRepository heatmapStreakRepository;
    private final UserRepository userRepository;

    public HeatmapStreakService(HeatmapStreakRepository heatmapStreakRepository, UserRepository userRepository) {
        this.heatmapStreakRepository = heatmapStreakRepository;
        this.userRepository = userRepository;
    }

    public HeatmapStreakResponse createHeatmapStreak(UUID userId, HeatmapStreakRequest request) {
        User owner = userRepository.findById(userId).orElse(null);

        HeatmapStreak heatmapStreak = new HeatmapStreak();
        heatmapStreak.setOwner(owner);
        heatmapStreak.setCurrentStreak(request.getCurrentStreak());
        heatmapStreak.setLongestStreak(request.getLongestStreak());

        HeatmapStreak savedHeatmapStreak = heatmapStreakRepository.save(heatmapStreak);

        return new HeatmapStreakResponse(
                savedHeatmapStreak.getStreakId(),
                savedHeatmapStreak.getOwner(),
                savedHeatmapStreak.getCurrentStreak(),
                savedHeatmapStreak.getLongestStreak());
    }

    public HeatmapStreakResponse getHeatmapStreakByStreakId(UUID streakId) {
        HeatmapStreak foundHeatmapStreak = heatmapStreakRepository.findById(streakId).orElse(null);

        if (foundHeatmapStreak == null) {
            return null;
        }

        return new HeatmapStreakResponse(
                foundHeatmapStreak.getStreakId(),
                foundHeatmapStreak.getOwner(),
                foundHeatmapStreak.getCurrentStreak(),
                foundHeatmapStreak.getLongestStreak());
    }

    public HeatmapStreakResponse getHeatmapStreak(UUID userId) {
        User owner = userRepository.findById(userId).orElse(null);

        if (owner == null) {
            return null;
        }

        HeatmapStreak foundHeatmapStreak = heatmapStreakRepository.findByOwner(owner).orElse(null);
        if (foundHeatmapStreak == null) {
            return null;
        }

        return new HeatmapStreakResponse(
                foundHeatmapStreak.getStreakId(),
                foundHeatmapStreak.getOwner(),
                foundHeatmapStreak.getCurrentStreak(),
                foundHeatmapStreak.getLongestStreak());
    }

    public List<HeatmapStreakResponse> getAllStreaks() {
        return heatmapStreakRepository.findAll().stream().map(streak -> new HeatmapStreakResponse(
                streak.getStreakId(),
                streak.getOwner(),
                streak.getCurrentStreak(),
                streak.getLongestStreak())).collect(Collectors.toList());
    }
}
