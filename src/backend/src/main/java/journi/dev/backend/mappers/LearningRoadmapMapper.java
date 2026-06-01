package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.LearningRoadmapRequest;
import journi.dev.backend.dtos.responses.LearningRoadmapResponse;
import journi.dev.backend.entities.LearningRoadmap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LearningRoadmapMapper {
    LearningRoadmapResponse toResponse(LearningRoadmap entity);
    LearningRoadmap toEntity(LearningRoadmapRequest request);
}
