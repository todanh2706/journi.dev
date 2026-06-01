package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.entities.SkillNode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillNodeMapper {
    SkillNodeResponse toResponse(SkillNode entity);
    SkillNode toEntity(SkillNodeRequest request);
}
