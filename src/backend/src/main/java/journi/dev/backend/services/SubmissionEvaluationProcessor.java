package journi.dev.backend.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class SubmissionEvaluationProcessor {
    private final SubmissionEvaluationStateService stateService;
    private final EvaluationWorkspaceManager workspaceManager;
    private final IsolatedContainerRunner containerRunner;
    private final GraderResultParser resultParser;

    public SubmissionEvaluationProcessor(
            SubmissionEvaluationStateService stateService,
            EvaluationWorkspaceManager workspaceManager,
            IsolatedContainerRunner containerRunner,
            GraderResultParser resultParser) {
        this.stateService = stateService;
        this.workspaceManager = workspaceManager;
        this.containerRunner = containerRunner;
        this.resultParser = resultParser;
    }

    public void process(UUID submissionId) {
        EvaluationJob job = stateService.claim(submissionId).orElse(null);
        if (job == null) {
            return;
        }

        try (EvaluationWorkspaceManager.EvaluationWorkspace workspace = workspaceManager.checkout(job)) {
            IsolatedContainerRunner.ContainerRunResult runResult = containerRunner.run(job, workspace.path());
            EvaluationResult result = resultParser.parse(job, workspace.path(), runResult.output());
            stateService.recordResult(submissionId, result);
        } catch (EvaluationException exception) {
            stateService.recordFailure(submissionId, exception.getCategory(), exception.getMessage());
        } catch (RuntimeException exception) {
            stateService.recordFailure(
                    submissionId,
                    journi.dev.backend.entities.SubmissionFailureCategory.INTERNAL_ERROR,
                    "An internal evaluation error occurred");
            throw exception;
        }
    }
}
