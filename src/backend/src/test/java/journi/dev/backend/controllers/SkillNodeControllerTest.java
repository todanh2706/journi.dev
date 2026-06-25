package journi.dev.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.dtos.responses.ChallengeResponse;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.services.ChallengeService;
import journi.dev.backend.services.SkillNodeService;

@ExtendWith(MockitoExtension.class)
class SkillNodeControllerTest {

    @Mock
    private SkillNodeService skillNodeService;

    @Mock
    private ChallengeService challengeService;

    @Test
    void getNodeByIdReturnsProgressGatedServiceResponseForCurrentUser() {
        UUID nodeId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setUserId(UUID.randomUUID());
        SkillNodeResponse serviceResponse = new SkillNodeResponse();
        serviceResponse.setNodeId(nodeId);
        serviceResponse.setProgressStatus(ProgressStatus.LOCKED);
        serviceResponse.setIsLocked(true);

        when(skillNodeService.getNodeById(nodeId, currentUser)).thenReturn(serviceResponse);

        SkillNodeResponse response = new SkillNodeController(skillNodeService, challengeService)
                .getNodeById(nodeId, currentUser);

        assertThat(response).isSameAs(serviceResponse);
        assertThat(response.getProgressStatus()).isEqualTo(ProgressStatus.LOCKED);
        assertThat(response.getIsLocked()).isTrue();
        verify(skillNodeService).getNodeById(nodeId, currentUser);
    }

    @Test
    void getChallengeReturnsProgressGatedChallengeForCurrentUser() {
        UUID nodeId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setUserId(UUID.randomUUID());
        ChallengeResponse serviceResponse = new ChallengeResponse(
                UUID.randomUUID(), nodeId, "Practice", "Description", "MEDIUM", "Instructions",
                java.util.List.of("Criterion"), java.util.List.of("Hint"), java.util.List.of("pom.xml"),
                "https://github.com/example/practice", 100, 80, 120, true, true,
                ProgressStatus.AVAILABLE, null);
        when(challengeService.getChallenge(nodeId, currentUser)).thenReturn(serviceResponse);

        ChallengeResponse response = new SkillNodeController(skillNodeService, challengeService)
                .getChallenge(nodeId, currentUser);

        assertThat(response).isSameAs(serviceResponse);
        verify(challengeService).getChallenge(nodeId, currentUser);
    }
}
