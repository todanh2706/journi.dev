package journi.dev.backend.configurations;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

class AuthSessionPropertiesTest {

    @Test
    void allowedOriginsHaveNoSourceCodeDefault() {
        assertThat(new AuthSessionProperties().getAllowedOrigins()).isEmpty();
    }

    @Test
    void allowedOriginsBindFromExternalConfiguration() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
                "security.auth.allowed-origins",
                "https://journi.example,https://admin.journi.example"));

        AuthSessionProperties properties = new Binder(source)
                .bind("security.auth", Bindable.of(AuthSessionProperties.class))
                .orElseThrow(() -> new AssertionError("Auth session properties were not bound"));

        assertThat(properties.getAllowedOrigins())
                .containsExactly("https://journi.example", "https://admin.journi.example");
    }
}
