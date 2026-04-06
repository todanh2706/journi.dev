package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "leaderboard")
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "leaderboard_id")
    private UUID leaderboardId;

    @Column(length = 50, nullable = false)
    private String niche;

    @Column(length = 30)
    private String period;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public UUID getLeaderboardId() {
        return leaderboardId;
    }

    public void setLeaderboardId(UUID leaderboardId) {
        this.leaderboardId = leaderboardId;
    }

    public String getNiche() {
        return niche;
    }

    public void setNiche(String niche) {
        this.niche = niche;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
