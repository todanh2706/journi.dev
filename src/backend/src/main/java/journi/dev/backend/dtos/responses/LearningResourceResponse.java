package journi.dev.backend.dtos.responses;

public class LearningResourceResponse {
    private String title;
    private String sourceType;
    private String sourceUrl;
    private String description;

    public LearningResourceResponse() {
    }

    public LearningResourceResponse(String title, String sourceType, String sourceUrl, String description) {
        this.title = title;
        this.sourceType = sourceType;
        this.sourceUrl = sourceUrl;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
