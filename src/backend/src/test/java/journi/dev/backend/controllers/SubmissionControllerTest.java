package journi.dev.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import journi.dev.backend.dtos.requests.CreateSubmissionRequest;
import journi.dev.backend.dtos.responses.SubmissionResponse;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.services.SubmissionService;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @Test
    void createSubmissionReturnsAcceptedSubmissionPayloadForCurrentLearner() {
        User currentUser = currentUser();
        UUID challengeId = UUID.randomUUID();
        CreateSubmissionRequest request = new CreateSubmissionRequest(
                "https://github.com/example/catalog",
                "main",
                "a".repeat(40));
        SubmissionResponse serviceResponse = submissionResponse(challengeId, SubmissionStatus.SUBMITTED, true);

        when(submissionService.createSubmission(currentUser, challengeId, request)).thenReturn(serviceResponse);

        ResponseEntity<SubmissionResponse> response = new SubmissionController(submissionService)
                .createSubmission(currentUser, challengeId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isSameAs(serviceResponse);
        verify(submissionService).createSubmission(currentUser, challengeId, request);
    }

    @Test
    void getHistoryReturnsLearnerScopedAttemptList() {
        User currentUser = currentUser();
        UUID challengeId = UUID.randomUUID();
        List<SubmissionResponse> serviceResponse = List.of(
                submissionResponse(challengeId, SubmissionStatus.NEEDS_CHANGES, false),
                submissionResponse(challengeId, SubmissionStatus.SUBMITTED, false));

        when(submissionService.getHistory(currentUser, challengeId)).thenReturn(serviceResponse);

        List<SubmissionResponse> response = new SubmissionController(submissionService)
                .getHistory(currentUser, challengeId);

        assertThat(response).isSameAs(serviceResponse);
        verify(submissionService).getHistory(currentUser, challengeId);
    }

    @Test
    void getSubmissionReturnsCurrentLearnerAttempt() {
        User currentUser = currentUser();
        SubmissionResponse serviceResponse = submissionResponse(UUID.randomUUID(), SubmissionStatus.EVALUATING, false);

        when(submissionService.getSubmission(currentUser, serviceResponse.submissionId())).thenReturn(serviceResponse);

        SubmissionResponse response = new SubmissionController(submissionService)
                .getSubmission(currentUser, serviceResponse.submissionId());

        assertThat(response).isSameAs(serviceResponse);
        verify(submissionService).getSubmission(currentUser, serviceResponse.submissionId());
    }

    @Test
    void retrySubmissionReturnsAcceptedPayload() {
        User currentUser = currentUser();
        SubmissionResponse serviceResponse = submissionResponse(UUID.randomUUID(), SubmissionStatus.SUBMITTED, true);

        when(submissionService.retrySubmission(currentUser, serviceResponse.submissionId())).thenReturn(
                serviceResponse);

        ResponseEntity<SubmissionResponse> response = new SubmissionController(submissionService)
                .retrySubmission(currentUser, serviceResponse.submissionId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isSameAs(serviceResponse);
        verify(submissionService).retrySubmission(currentUser, serviceResponse.submissionId());
    }

    private User currentUser() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        return user;
    }

    private SubmissionResponse submissionResponse(UUID challengeId, SubmissionStatus status, boolean retryable) {
        return new SubmissionResponse(
                UUID.randomUUID(),
                challengeId,
                "https://github.com/example/catalog",
                "main",
                "a".repeat(40),
                2,
                status,
                87,
                "Latest evaluator result",
                List.of(),
                null,
                retryable,
                LocalDateTime.of(2026, 6, 24, 12, 0),
                LocalDateTime.of(2026, 6, 24, 12, 1),
                LocalDateTime.of(2026, 6, 24, 12, 2));
    }
}
