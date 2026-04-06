package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class LeaderboardEntryId implements Serializable {
    private UUID leaderboardId;
    private UUID owner;

    public LeaderboardEntryId() {
    }

    public LeaderboardEntryId(UUID leaderboardId, UUID owner) {
        this.leaderboardId = leaderboardId;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LeaderboardEntryId that = (LeaderboardEntryId) o;
        return Objects.equals(leaderboardId, that.leaderboardId) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaderboardId, owner);
    }
}
