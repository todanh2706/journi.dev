package journi.dev.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import journi.dev.backend.configurations.RoadmapSeedProperties;
import journi.dev.backend.services.RoadmapSeedService.RoadmapSeedResult;

@Component
public class RoadmapSeedRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(RoadmapSeedRunner.class);

    private final RoadmapSeedProperties roadmapSeedProperties;
    private final RoadmapSeedService roadmapSeedService;
    private final ConfigurableApplicationContext applicationContext;

    public RoadmapSeedRunner(
            RoadmapSeedProperties roadmapSeedProperties,
            RoadmapSeedService roadmapSeedService,
            ConfigurableApplicationContext applicationContext) {
        this.roadmapSeedProperties = roadmapSeedProperties;
        this.roadmapSeedService = roadmapSeedService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!roadmapSeedProperties.isEnabled()) {
            return;
        }

        RoadmapSeedResult result = roadmapSeedService.seedConfiguredRoadmaps();
        log.info(
                "Seeded roadmap '{}' with {} nodes, {} resources, {} challenges, and {} prerequisites",
                result.roadmapSlug(),
                result.nodeCount(),
                result.resourceCount(),
                result.challengeCount(),
                result.prerequisiteCount());

        if (roadmapSeedProperties.isExitAfterRun()) {
            int exitCode = SpringApplication.exit(applicationContext, () -> 0);
            if (exitCode != 0) {
                throw new IllegalStateException("Roadmap seed runner exited with a non-zero code: " + exitCode);
            }
        }
    }
}
