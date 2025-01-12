// JwtAuthenticationFilter.java
package com.jonnie.gatewayms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Starting JWT authentication for path: {}", exchange.getRequest().getPath());

        String token = extractToken(exchange);
        if (token == null) {
            log.warn("No token found in request");
            return chain.filter(exchange); // Continue unauthenticated
        }
        log.info("Token extracted: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

        return validateToken(token)
                .flatMap(authentication -> {
                    log.info("Setting authentication object in the security context");

                    // Extract details from the authentication object
                    String userId = (String) authentication.getPrincipal();
                    String role = authentication.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElse("");

                    // Add headers to the request
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-User-Id", userId)
                                    .header("X-User-Role", role)
                                    .build())
                            .build();
                    log.info("Added headers to the request: X-User-Id: {}, X-User-Role: {}", userId, role);
                    // Set the authentication in the reactive security context
                    return chain.filter(modifiedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                })
                .onErrorResume(e -> {
                    log.error("JWT authentication failed", e);
                    return chain.filter(exchange); // Continue unauthenticated
                });
    }


    private String extractToken(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private Mono<Authentication> validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);
            log.info("Authentication details - userId: {}, role: {}", userId, role);

            return Mono.just(new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            ));
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return Mono.error(e);
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
