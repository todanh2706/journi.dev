package journi.dev.backend.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSubmissionJobPublisher implements SubmissionJobPublisher {
    private final StringRedisTemplate redisTemplate;
    private final String streamName;

    public RedisSubmissionJobPublisher(
            StringRedisTemplate redisTemplate,
            @Value("${practice.submission.stream:practice-submissions}") String streamName) {
        this.redisTemplate = redisTemplate;
        this.streamName = streamName;
    }

    @Override
    public void publish(UUID submissionId) {
        MapRecord<String, String, String> record = StreamRecords
                .newRecord()
                .ofMap(Map.of("submissionId", submissionId.toString()))
                .withStreamKey(streamName);
        redisTemplate.opsForStream().add(record);
    }
}
