package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class LeaderboardEntryId implements Serializable {
    private UUID leaderboardId;
    private UUID user;

    public LeaderboardEntryId() {
    }

    public LeaderboardEntryId(UUID leaderboardId, UUID user) {
        this.leaderboardId = leaderboardId;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LeaderboardEntryId that = (LeaderboardEntryId) o;
        return Objects.equals(leaderboardId, that.leaderboardId) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaderboardId, user);
    }
}
