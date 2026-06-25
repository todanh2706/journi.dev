package journi.dev.backend.dtos.responses;

public record CriterionFeedbackResponse(String criterion, boolean passed, String message) {
}
