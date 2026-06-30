package journi.dev.backend.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import journi.dev.backend.entities.SubmissionFailureCategory;
import journi.dev.backend.configurations.PracticeGraderProperties;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.repositories.SubmissionRepository;

@Component
@Profile("grader")
public class RedisSubmissionWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubmissionWorker.class);
    private static final String REDIS_BUSY_GROUP_ERROR = "BUSYGROUP";
    private final StringRedisTemplate redisTemplate;
    private final SubmissionEvaluationProcessor processor;
    private final SubmissionRepository submissionRepository;
    private final SubmissionJobPublisher publisher;
    private final String streamName;
    private final String groupName;
    private final String consumerName;
    private final PracticeGraderProperties graderProperties;

    public RedisSubmissionWorker(
            StringRedisTemplate redisTemplate,
            SubmissionEvaluationProcessor processor,
            SubmissionRepository submissionRepository,
            SubmissionJobPublisher publisher,
            PracticeGraderProperties graderProperties,
            @Value("${practice.submission.stream:practice-submissions}") String streamName,
            @Value("${practice.submission.consumer-group:practice-graders}") String groupName,
            @Value("${practice.submission.consumer-name:${HOSTNAME:local-grader}}") String consumerName) {
        this.redisTemplate = redisTemplate;
        this.processor = processor;
        this.submissionRepository = submissionRepository;
        this.publisher = publisher;
        this.graderProperties = graderProperties;
        this.streamName = streamName;
        this.groupName = groupName;
        this.consumerName = consumerName;
    }

    @PostConstruct
    public void initializeConsumerGroup() {
        RecordId bootstrap = redisTemplate.opsForStream().add(streamName, Map.of("bootstrap", "true"));
        try {
            redisTemplate.opsForStream().createGroup(streamName, ReadOffset.latest(), groupName);
        } catch (RedisSystemException exception) {
            if (!isConsumerGroupAlreadyExists(exception)) {
                throw exception;
            }
            LOGGER.info("Redis consumer group {} already exists for stream {}; continuing", groupName, streamName);
        } finally {
            if (bootstrap != null) {
                redisTemplate.opsForStream().delete(streamName, bootstrap);
            }
        }
        republishSubmittedRows();
    }

    private boolean isConsumerGroupAlreadyExists(RedisSystemException exception) {
        Throwable current = exception;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains(REDIS_BUSY_GROUP_ERROR)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @Scheduled(fixedDelayString = "${practice.submission.poll-delay-ms:1000}")
    public void poll() {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                Consumer.from(groupName, consumerName),
                StreamReadOptions.empty().count(graderProperties.getConcurrency()).block(Duration.ofSeconds(2)),
                StreamOffset.create(streamName, ReadOffset.lastConsumed()));
        if (records == null) {
            return;
        }
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = records.stream().map(record -> executor.submit(() -> processRecord(record))).toList();
            for (var task : tasks) {
                try {
                    task.get();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (java.util.concurrent.ExecutionException exception) {
                    LOGGER.error("Submission evaluation failed before acknowledgement; Redis will retain the message",
                            exception.getCause());
                }
            }
        }
    }

    @Scheduled(fixedDelayString = "${practice.submission.recovery-delay-ms:60000}")
    @Transactional
    public void recoverStaleEvaluations() {
        LocalDateTime now = LocalDateTime.now();
        submissionRepository.failExpiredEvaluationLeases(
                SubmissionStatus.EVALUATING,
                SubmissionStatus.FAILED,
                SubmissionFailureCategory.WORKER_LOST,
                "The evaluation worker stopped before completing this attempt",
                now,
                now);
        republishSubmittedRows();
    }

    private void republishSubmittedRows() {
        submissionRepository.findIdsByStatus(SubmissionStatus.SUBMITTED, PageRequest.of(0, 100))
                .forEach(publisher::publish);
    }

    private void acknowledge(MapRecord<String, Object, Object> record) {
        redisTemplate.opsForStream().acknowledge(streamName, groupName, record.getId());
    }

    private void processRecord(MapRecord<String, Object, Object> record) {
        Object rawSubmissionId = record.getValue().get("submissionId");
        if (rawSubmissionId == null) {
            acknowledge(record);
            return;
        }
        try {
            processor.process(UUID.fromString(rawSubmissionId.toString()));
            acknowledge(record);
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Discarding malformed submission queue message {}", record.getId(), exception);
            acknowledge(record);
        }
    }
}
