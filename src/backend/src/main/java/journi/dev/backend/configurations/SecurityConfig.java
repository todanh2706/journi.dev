package journi.dev.backend.configurations;

import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private final AuthenticationProvider authenticationProvider;
        private final JwtFilter jwtFilter;
        private final AuthSessionProperties authSessionProperties;

        public SecurityConfig(
                        AuthenticationProvider authenticationProvider,
                        JwtFilter jwtFilter,
                        AuthSessionProperties authSessionProperties) {
                this.authenticationProvider = authenticationProvider;
                this.jwtFilter = jwtFilter;
                this.authSessionProperties = authSessionProperties;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(csrfTokenRepository())
                                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                                                .requireCsrfProtectionMatcher(authCsrfRequestMatcher())) // Just require
                                                                                                         // CSRF token
                                                                                                         // for
                                                                                                         // specified
                                                                                                         // endpoints in
                                                                                                         // CSRF_PROTECTED
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                AuthEndpoints.SIGNUP,
                                                                AuthEndpoints.LOGIN,
                                                                AuthEndpoints.REFRESH,
                                                                AuthEndpoints.LOGOUT,
                                                                AuthEndpoints.CSRF)
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> response
                                                                .sendError(HttpServletResponse.SC_UNAUTHORIZED))
                                                .accessDeniedHandler((request, response,
                                                                accessDeniedException) -> response.sendError(
                                                                                HttpServletResponse.SC_FORBIDDEN)))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                List<String> allowedOrigins = authSessionProperties.getAllowedOrigins();
                if (allowedOrigins.isEmpty() || allowedOrigins.stream().anyMatch("*"::equals)) {
                        throw new IllegalStateException(
                                        "FRONTEND_ALLOWED_ORIGINS must contain explicit origins and cannot use a wildcard");
                }

                corsConfiguration.setAllowedOrigins(List.copyOf(allowedOrigins));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type",
                                authSessionProperties.getCsrf().getHeaderName()));
                corsConfiguration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfiguration);

                return source;
        }

        @Bean
        public CookieCsrfTokenRepository csrfTokenRepository() {
                CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
                repository.setCookieName(authSessionProperties.getCsrf().getCookieName());
                repository.setHeaderName(authSessionProperties.getCsrf().getHeaderName());
                repository.setCookiePath("/");
                repository.setCookieCustomizer(builder -> builder
                                .httpOnly(true)
                                .secure(authSessionProperties.getRefreshCookie().isSecure())
                                .sameSite(normalizedSameSite()));
                return repository;
        }

        private RequestMatcher authCsrfRequestMatcher() {
                return request -> {
                        if (!"POST".equalsIgnoreCase(request.getMethod())) {
                                return false;
                        }
                        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
                        return AuthEndpoints.CSRF_PROTECTED.contains(requestPath);
                };
        }

        private String normalizedSameSite() {
                String configured = authSessionProperties.getRefreshCookie().getSameSite();
                String lowerCase = configured.toLowerCase(Locale.ROOT);
                return Character.toUpperCase(lowerCase.charAt(0)) + lowerCase.substring(1);
        }
}
