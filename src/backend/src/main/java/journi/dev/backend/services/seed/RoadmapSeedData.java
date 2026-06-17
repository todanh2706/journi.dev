package journi.dev.backend.services.seed;

import java.util.List;
import java.util.Map;

public record RoadmapSeedData(
        RoadmapDefinition roadmap,
        List<NodeDefinition> nodes) {

    public record RoadmapDefinition(
            String title,
            String slug,
            String description,
            String visibility,
            Boolean isDynamic) {
    }

    public record NodeDefinition(
            String title,
            String slug,
            Integer orderIndex,
            String nodeType,
            String summary,
            String level,
            Integer estimatedHours,
            String note,
            List<String> checklist,
            List<String> prerequisites,
            List<ResourceDefinition> resources,
            ChallengeDefinition challenge) {
    }

    public record ResourceDefinition(
            String sourceType,
            String title,
            String sourceUrl,
            String contentBody,
            Map<String, Object> meta) {
    }

    public record ChallengeDefinition(
            String title,
            String description,
            String difficulty,
            Integer maxScore,
            Boolean isRequired) {
    }
}
