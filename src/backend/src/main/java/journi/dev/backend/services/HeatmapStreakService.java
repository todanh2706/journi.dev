package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.requests.HeatmapStreakRequest;
import journi.dev.backend.dtos.responses.HeatmapStreakResponse;
import journi.dev.backend.entities.HeatmapStreak;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.HeatmapStreakRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.mappers.HeatmapStreakMapper;

@Service
public class HeatmapStreakService {
    private final HeatmapStreakRepository heatmapStreakRepository;
    private final UserRepository userRepository;
    private final HeatmapStreakMapper streakMapper;

    public HeatmapStreakService(HeatmapStreakRepository heatmapStreakRepository, UserRepository userRepository, HeatmapStreakMapper streakMapper) {
        this.heatmapStreakRepository = heatmapStreakRepository;
        this.userRepository = userRepository;
        this.streakMapper = streakMapper;
    }

    @Transactional
    public HeatmapStreakResponse createHeatmapStreak(UUID userId, HeatmapStreakRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        HeatmapStreak heatmapStreak = streakMapper.toEntity(request);
        heatmapStreak.setOwner(owner);

        HeatmapStreak savedHeatmapStreak = heatmapStreakRepository.save(heatmapStreak);

        return streakMapper.toResponse(savedHeatmapStreak);
    }

    public HeatmapStreakResponse getHeatmapStreakByStreak(UUID streakId) {
        HeatmapStreak foundHeatmapStreak = heatmapStreakRepository.findById(streakId)
                .orElseThrow(() -> new ResourceNotFoundException("Heatmap streak not found with id: " + streakId));

        return streakMapper.toResponse(foundHeatmapStreak);
    }

    public HeatmapStreakResponse getHeatmapStreak(UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        HeatmapStreak foundHeatmapStreak = heatmapStreakRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Heatmap streak not found for user: " + userId));

        return streakMapper.toResponse(foundHeatmapStreak);
    }

    public List<HeatmapStreakResponse> getAllStreaks() {
        return heatmapStreakRepository.findAll().stream()
                .map(streakMapper::toResponse)
                .collect(Collectors.toList());
    }
}
