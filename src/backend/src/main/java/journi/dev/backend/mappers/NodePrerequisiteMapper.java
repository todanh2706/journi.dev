package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.NodePrerequisiteRequest;
import journi.dev.backend.dtos.responses.NodePrerequisiteResponse;
import journi.dev.backend.entities.NodePrerequisite;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NodePrerequisiteMapper {
    NodePrerequisiteResponse toResponse(NodePrerequisite entity);
    NodePrerequisite toEntity(NodePrerequisiteRequest request);
}
