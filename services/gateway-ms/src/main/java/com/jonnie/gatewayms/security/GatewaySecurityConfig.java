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
                            .pathMatchers(
                                    "/api/v1/users/register",
                                    "/api/v1/users/activate-account",
                                    "/api/v1/users/authenticate")
                            .permitAll()
                            .anyExchange()
                            .authenticated()
                    )
                    .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                    .build();
        }
    }