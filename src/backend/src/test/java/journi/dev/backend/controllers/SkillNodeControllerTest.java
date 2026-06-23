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
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.services.SkillNodeService;

@ExtendWith(MockitoExtension.class)
class SkillNodeControllerTest {

    @Mock
    private SkillNodeService skillNodeService;

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

        SkillNodeResponse response = new SkillNodeController(skillNodeService).getNodeById(nodeId, currentUser);

        assertThat(response).isSameAs(serviceResponse);
        assertThat(response.getProgressStatus()).isEqualTo(ProgressStatus.LOCKED);
        assertThat(response.getIsLocked()).isTrue();
        verify(skillNodeService).getNodeById(nodeId, currentUser);
    }
}
