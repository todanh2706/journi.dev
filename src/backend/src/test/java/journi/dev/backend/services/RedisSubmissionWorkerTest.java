package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.repositories.SubmissionRepository;

@ExtendWith(MockitoExtension.class)
class RedisSubmissionWorkerTest {
    private static final String STREAM_NAME = "practice-submissions";
    private static final String GROUP_NAME = "practice-graders";
    private static final String CONSUMER_NAME = "test-grader";

    @Mock
    private StringRedisTemplate redisTemplate;

    @SuppressWarnings("rawtypes")
    @Mock
    private StreamOperations streamOperations;

    @Mock
    private SubmissionEvaluationProcessor processor;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private SubmissionJobPublisher publisher;

    private RedisSubmissionWorker worker;

    @BeforeEach
    void setUp() {
        worker = new RedisSubmissionWorker(
                redisTemplate,
                processor,
                submissionRepository,
                publisher,
                new PracticeGraderProperties(),
                STREAM_NAME,
                GROUP_NAME,
                CONSUMER_NAME);
    }

    @Test
    @SuppressWarnings("unchecked")
    void initializeConsumerGroupContinuesWhenConsumerGroupAlreadyExists() {
        RecordId bootstrap = RecordId.of("1-0");
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(streamOperations.add(eq(STREAM_NAME), anyMap())).thenReturn(bootstrap);
        doThrow(new RedisSystemException(
                "Error in execution",
                new RuntimeException("BUSYGROUP Consumer Group name already exists")))
                        .when(streamOperations)
                        .createGroup(eq(STREAM_NAME), any(ReadOffset.class), eq(GROUP_NAME));
        when(submissionRepository.findIdsByStatus(eq(SubmissionStatus.SUBMITTED), any(Pageable.class)))
                .thenReturn(List.of());

        worker.initializeConsumerGroup();

        verify(streamOperations).delete(STREAM_NAME, bootstrap);
        verify(submissionRepository).findIdsByStatus(eq(SubmissionStatus.SUBMITTED), any(Pageable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void initializeConsumerGroupRethrowsUnexpectedRedisErrors() {
        RecordId bootstrap = RecordId.of("1-0");
        RedisSystemException redisException = new RedisSystemException(
                "Error in execution",
                new RuntimeException("NOAUTH Authentication required"));
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(streamOperations.add(eq(STREAM_NAME), anyMap())).thenReturn(bootstrap);
        doThrow(redisException)
                .when(streamOperations)
                .createGroup(eq(STREAM_NAME), any(ReadOffset.class), eq(GROUP_NAME));

        assertThatThrownBy(worker::initializeConsumerGroup).isSameAs(redisException);

        verify(streamOperations).delete(STREAM_NAME, bootstrap);
        verify(submissionRepository, never()).findIdsByStatus(any(SubmissionStatus.class), any(Pageable.class));
    }
}
