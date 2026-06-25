package journi.dev.backend.configurations;

import java.nio.file.Path;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "practice.grader")
public class PracticeGraderProperties {
    private Path workspaceRoot = Path.of(System.getProperty("java.io.tmpdir"), "journi-grader");
    private Path assetsPath = Path.of("src/main/resources/grader").toAbsolutePath().normalize();
    private Duration cloneTimeout = Duration.ofSeconds(60);
    private Duration leaseDuration = Duration.ofMinutes(10);
    private long maxRepositoryBytes = 50L * 1024 * 1024;
    private int maxOutputBytes = 32 * 1024;
    private int maxResultBytes = 64 * 1024;
    private String cpus = "1.0";
    private String memory = "768m";
    private int pidsLimit = 128;
    private int concurrency = 1;

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }

    public void setWorkspaceRoot(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    public Path getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(Path assetsPath) {
        this.assetsPath = assetsPath;
    }

    public Duration getCloneTimeout() {
        return cloneTimeout;
    }

    public void setCloneTimeout(Duration cloneTimeout) {
        this.cloneTimeout = cloneTimeout;
    }

    public Duration getLeaseDuration() {
        return leaseDuration;
    }

    public void setLeaseDuration(Duration leaseDuration) {
        this.leaseDuration = leaseDuration;
    }

    public long getMaxRepositoryBytes() {
        return maxRepositoryBytes;
    }

    public void setMaxRepositoryBytes(long maxRepositoryBytes) {
        this.maxRepositoryBytes = maxRepositoryBytes;
    }

    public int getMaxOutputBytes() {
        return maxOutputBytes;
    }

    public void setMaxOutputBytes(int maxOutputBytes) {
        this.maxOutputBytes = maxOutputBytes;
    }

    public int getMaxResultBytes() {
        return maxResultBytes;
    }

    public void setMaxResultBytes(int maxResultBytes) {
        this.maxResultBytes = maxResultBytes;
    }

    public String getCpus() {
        return cpus;
    }

    public void setCpus(String cpus) {
        this.cpus = cpus;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public int getPidsLimit() {
        return pidsLimit;
    }

    public void setPidsLimit(int pidsLimit) {
        this.pidsLimit = pidsLimit;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = Math.max(1, concurrency);
    }
}
