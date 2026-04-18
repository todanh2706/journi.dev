package journi.dev.backend.dtos.requests;

import java.util.UUID;

public class HeatmapStreakRequest {
    private UUID userId;
    private Integer currentStreak;
    private Integer longestStreak;

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public HeatmapStreakRequest(UUID userId, Integer currentStreak, Integer longestStreak) {
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
