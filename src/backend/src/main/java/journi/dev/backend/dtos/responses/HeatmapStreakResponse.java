package journi.dev.backend.dtos.responses;

import java.util.UUID;

import journi.dev.backend.entities.User;

public class HeatmapStreakResponse {
    private UUID streakId;
    private User owner;
    private Integer currentStreak;
    private Integer longestStreak;

    public HeatmapStreakResponse(UUID streakId, User owner, Integer currentStreak, Integer longestStreak) {
        this.streakId = streakId;
        this.owner = owner;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
    }

    public UUID getStreakId() {
        return streakId;
    }

    public void setStreakId(UUID streakId) {
        this.streakId = streakId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

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
}
