package journi.dev.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionFailureCategory;
import journi.dev.backend.entities.SubmissionStatus;
import jakarta.persistence.LockModeType;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    Optional<Submission> findByUser_UserIdAndChallenge_ChallengeIdAndCommitHash(
            UUID userId,
            UUID challengeId,
            String commitHash);

    List<Submission> findByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
            UUID userId,
            UUID challengeId);

    Optional<Submission> findBySubmissionIdAndUser_UserId(UUID submissionId, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select submission from Submission submission where submission.submissionId = :submissionId")
    Optional<Submission> findByIdForTerminalUpdate(@Param("submissionId") UUID submissionId);

    @Query("select submission.submissionId from Submission submission where submission.status = :status")
    List<UUID> findIdsByStatus(@Param("status") SubmissionStatus status, org.springframework.data.domain.Pageable page);

    Optional<Submission> findFirstByUser_UserIdAndChallenge_ChallengeIdOrderByAttemptNumberDesc(
            UUID userId,
            UUID challengeId);

    @Query("""
            select coalesce(max(submission.attemptNumber), 0)
            from Submission submission
            where submission.user.userId = :userId
              and submission.challenge.challengeId = :challengeId
            """)
    int findMaxAttemptNumber(@Param("userId") UUID userId, @Param("challengeId") UUID challengeId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Submission submission
               set submission.status = :evaluatingStatus,
                   submission.evaluationStartedAt = :startedAt,
                   submission.evaluationLeaseUntil = :leaseUntil,
                   submission.failureCategory = null
             where submission.submissionId = :submissionId
               and submission.status = :submittedStatus
            """)
    int claimForEvaluation(
            @Param("submissionId") UUID submissionId,
            @Param("submittedStatus") SubmissionStatus submittedStatus,
            @Param("evaluatingStatus") SubmissionStatus evaluatingStatus,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("leaseUntil") LocalDateTime leaseUntil);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Submission submission
               set submission.status = :failedStatus,
                   submission.failureCategory = :failureCategory,
                   submission.resultSummary = :resultSummary,
                   submission.evaluationCompletedAt = :completedAt,
                   submission.evaluationLeaseUntil = null
             where submission.status = :evaluatingStatus
               and submission.evaluationLeaseUntil < :expiredBefore
            """)
    int failExpiredEvaluationLeases(
            @Param("evaluatingStatus") SubmissionStatus evaluatingStatus,
            @Param("failedStatus") SubmissionStatus failedStatus,
            @Param("failureCategory") SubmissionFailureCategory failureCategory,
            @Param("resultSummary") String resultSummary,
            @Param("completedAt") LocalDateTime completedAt,
            @Param("expiredBefore") LocalDateTime expiredBefore);
}
