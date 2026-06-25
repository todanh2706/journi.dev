package journi.dev.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateSubmissionRequest(
        @NotBlank(message = "Repository URL is required")
        @Size(max = 500, message = "Repository URL must be 500 characters or fewer")
        String repositoryUrl,

        @NotBlank(message = "Branch is required")
        @Size(max = 100, message = "Branch must be 100 characters or fewer")
        @Pattern(regexp = "[A-Za-z0-9._/-]+", message = "Branch contains unsupported characters")
        String branch,

        @NotBlank(message = "Commit SHA is required")
        @Pattern(regexp = "[0-9a-fA-F]{40}", message = "Commit SHA must contain exactly 40 hexadecimal characters")
        String commitSha) {
}
