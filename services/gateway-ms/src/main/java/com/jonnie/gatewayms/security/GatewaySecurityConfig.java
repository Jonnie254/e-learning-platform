    package com.jonnie.gatewayms.security;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.server.reactive.ServerHttpRequest;
    import org.springframework.http.server.reactive.ServerHttpResponse;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
    import org.springframework.security.config.web.server.ServerHttpSecurity;
    import org.springframework.security.web.server.SecurityWebFilterChain;
    import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.server.WebFilter;

    import java.util.Arrays;
    import java.util.Collections;
    import java.util.List;

    import static org.springframework.security.config.Customizer.withDefaults;

    @Configuration
    @EnableWebFluxSecurity
    public class GatewaySecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public GatewaySecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
            this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .cors(corsSpec -> corsSpec.configurationSource(request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(List.of("http://localhost:4200"));
                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowedHeaders(List.of(
                                "Origin", "Content-Type", "Accept", "Authorization",
                                "Sec-WebSocket-Key", "Sec-WebSocket-Protocol", "Sec-WebSocket-Version"
                        ));
                        config.setAllowCredentials(true);
                        return config;
                    }))
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange
                            .pathMatchers(
                                    "/api/v1/users/register",
                                    "/api/v1/users/activate-account",
                                    "/api/v1/payments/success",
                                    "/api/v1/payments/cancel",
                                    "/api/v1/courses/all-courses",
                                    "/api/v1/courses/{course-id}",
                                    "/api/v1/users/authenticate",
                                    "/api/v1/courses/instructor/{courseId}",
                                    "/ws/info/**",
                                    "/ws/**")
                            .permitAll()
                            .anyExchange()
                            .authenticated()
                    )
                    .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                    .build();
        }

        @Bean
        public WebFilter logHeadersFilter() {
            return (exchange, chain) -> {
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                System.out.println("🔹 Request Headers: " + request.getHeaders());

                return chain.filter(exchange)
                        .doOnSuccess(aVoid -> {
                            System.out.println("Response Headers: " + response.getHeaders());
                        });
            };
        }

    }
