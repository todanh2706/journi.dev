package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class LeaderboardEntryId implements Serializable {
    private UUID leaderboardId;
    private UUID userId;

    public LeaderboardEntryId() {
    }

    public LeaderboardEntryId(UUID leaderboardId, UUID userId) {
        this.leaderboardId = leaderboardId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LeaderboardEntryId that = (LeaderboardEntryId) o;
        return Objects.equals(leaderboardId, that.leaderboardId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaderboardId, userId);
    }
}
