package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.NodePrerequisiteRequest;
import journi.dev.backend.dtos.responses.NodePrerequisiteResponse;
import journi.dev.backend.entities.NodePrerequisite;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NodePrerequisiteMapper {
    @Mapping(source = "parentNode.nodeId", target = "parentId")
    @Mapping(source = "childNode.nodeId", target = "childNodeId")
    NodePrerequisiteResponse toResponse(NodePrerequisite entity);

    @Mapping(target = "parentNode", ignore = true)
    @Mapping(target = "childNode", ignore = true)
    NodePrerequisite toEntity(NodePrerequisiteRequest request);
}
