    package com.jonnie.gatewayms.security;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
    import org.springframework.security.config.web.server.ServerHttpSecurity;
    import org.springframework.security.web.server.SecurityWebFilterChain;
    import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

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
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange
                            // Allow access to registration and login without authentication (no JWT required)
                            .pathMatchers(
                                    "/api/v1/users/register",
                                    "/api/v1/users/activate-account",
                                    "/api/v1/users/authenticate")
                            .permitAll()
                            .anyExchange()
                            .authenticated()
                    )
                    // Add JWT authentication filter before the default authentication filter
                    .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                    .build();
        }
    }
