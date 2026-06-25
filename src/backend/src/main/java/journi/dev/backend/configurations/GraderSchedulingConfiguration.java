package journi.dev.backend.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("grader")
@EnableScheduling
public class GraderSchedulingConfiguration {
}
