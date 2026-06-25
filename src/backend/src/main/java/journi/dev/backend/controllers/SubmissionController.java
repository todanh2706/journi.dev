package journi.dev.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import journi.dev.backend.dtos.requests.CreateSubmissionRequest;
import journi.dev.backend.dtos.responses.SubmissionResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.services.SubmissionService;

@RestController
@RequestMapping("/api/v1/users/me")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/challenges/{challengeId}/submissions")
    public ResponseEntity<SubmissionResponse> createSubmission(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID challengeId,
            @Valid @RequestBody CreateSubmissionRequest request) {
        return ResponseEntity.accepted().body(
                submissionService.createSubmission(currentUser, challengeId, request));
    }

    @GetMapping("/challenges/{challengeId}/submissions")
    public List<SubmissionResponse> getHistory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID challengeId) {
        return submissionService.getHistory(currentUser, challengeId);
    }

    @GetMapping("/submissions/{submissionId}")
    public SubmissionResponse getSubmission(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID submissionId) {
        return submissionService.getSubmission(currentUser, submissionId);
    }

    @PostMapping("/submissions/{submissionId}/retry")
    public ResponseEntity<SubmissionResponse> retrySubmission(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID submissionId) {
        return ResponseEntity.accepted().body(
                submissionService.retrySubmission(currentUser, submissionId));
    }
}
