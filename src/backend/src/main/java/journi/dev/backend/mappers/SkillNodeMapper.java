package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.entities.SkillNode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillNodeMapper {
    @Mapping(source = "roadmap.roadmapId", target = "roadmapId")
    @Mapping(target = "progressStatus", ignore = true)
    @Mapping(target = "isLocked", ignore = true)
    @Mapping(target = "summary", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "estimatedHours", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "learningResources", ignore = true)
    SkillNodeResponse toResponse(SkillNode entity);

    @Mapping(target = "roadmap", ignore = true)
    @Mapping(target = "nodeType", ignore = true)
    SkillNode toEntity(SkillNodeRequest request);
}
