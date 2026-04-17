package journi.dev.backend.dtos.requests;

import java.util.UUID;

public class HeatmapStreakRequest {
    private UUID streakId;
    private UUID userId;
    private Integer currentStreak;

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    private Integer longestStreak;

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public HeatmapStreakRequest() {
    }

    public UUID getStreakId() {
        return streakId;
    }

    public void setStreakId(UUID streakId) {
        this.streakId = streakId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
