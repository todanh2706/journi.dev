package journi.dev.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import journi.dev.backend.dtos.requests.HeatmapStreakRequest;
import journi.dev.backend.dtos.responses.HeatmapStreakResponse;
import journi.dev.backend.entities.HeatmapStreak;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HeatmapStreakMapper {
    HeatmapStreakResponse toResponse(HeatmapStreak entity);
    HeatmapStreak toEntity(HeatmapStreakRequest request);
}
