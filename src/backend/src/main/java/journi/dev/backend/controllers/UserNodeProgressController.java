package journi.dev.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import journi.dev.backend.dtos.responses.UserNodeProgressResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.services.UserNodeProgressService;

@RestController
@RequestMapping("/api/v1/users/me/progress")
public class UserNodeProgressController {
    private final UserNodeProgressService userNodeProgressService;

    public UserNodeProgressController(UserNodeProgressService userNodeProgressService) {
        this.userNodeProgressService = userNodeProgressService;
    }

    @GetMapping
    public List<UserNodeProgressResponse> getMyProgress(@AuthenticationPrincipal User currentUser) {
        return userNodeProgressService.getProgressForUser(currentUser);
    }

    @PostMapping("/nodes/{nodeId}/complete")
    public ResponseEntity<UserNodeProgressResponse> completeNode(@AuthenticationPrincipal User currentUser,
            @PathVariable UUID nodeId) {
        UserNodeProgressResponse response = userNodeProgressService.markNodeCompleted(currentUser, nodeId);
        return ResponseEntity.ok(response);
    }
}
