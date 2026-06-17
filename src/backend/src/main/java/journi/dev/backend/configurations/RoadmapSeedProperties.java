package journi.dev.backend.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "journi.seed.roadmaps")
public class RoadmapSeedProperties {
    private boolean enabled;
    private String datasetLocation = "classpath:seed-data/backend-java-spring-roadmap.json";
    private boolean exitAfterRun;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDatasetLocation() {
        return datasetLocation;
    }

    public void setDatasetLocation(String datasetLocation) {
        this.datasetLocation = datasetLocation;
    }

    public boolean isExitAfterRun() {
        return exitAfterRun;
    }

    public void setExitAfterRun(boolean exitAfterRun) {
        this.exitAfterRun = exitAfterRun;
    }
}
