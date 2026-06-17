package journi.dev.backend.services;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.services.seed.RoadmapSeedData;

@Service
public class RoadmapSeedDataLoader {
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    public RoadmapSeedDataLoader(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public RoadmapSeedData load(String datasetLocation) {
        Resource resource = resourceLoader.getResource(datasetLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("Roadmap seed dataset not found: " + datasetLocation);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, RoadmapSeedData.class);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read roadmap seed dataset: " + datasetLocation, exception);
        }
    }
}
