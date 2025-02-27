package com.jonnie.gatewayms.security;

import com.jonnie.gatewayms.exceptions.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Filtering request: {}", exchange.getRequest().getPath());
        String token = extractToken(exchange);
        if (token == null) {
            return chain.filter(exchange); // Proceed if no token
        }
        log.info("Token found: {}", token);

        return validateToken(token)
                .flatMap(authentication -> {
                    String userId = (String) authentication.getPrincipal();
                    String role = authentication.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElse("");

                    // Check if the request is for WebSockets
                    if (exchange.getRequest().getURI().getPath().contains("/ws")) {
                        log.info("WebSocket request detected. Adding userId to query parameters.");

                        URI newUri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                                .replaceQueryParam("userId", userId)
                                .build()
                                .toUri();

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(exchange.getRequest().mutate().uri(newUri).build())
                                .build();

                        return chain.filter(modifiedExchange);
                    }

                    // Normal HTTP request (Attach headers instead)
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("Authorization", "Bearer " + token)
                                    .header("X-User-Id", userId)
                                    .header("X-User-Role", role)
                                    .build())
                            .build();

                    return chain.filter(modifiedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                })
                .onErrorResume(e -> handleTokenValidationError(e, exchange, chain));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7).trim();
    }

    private Mono<Authentication> validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);
            return Mono.just(new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            ));
        } catch (ExpiredJwtException e) {
            return Mono.error(new BusinessException("JWT token has expired."));
        } catch (Exception e) {
            return Mono.error(new BusinessException("Invalid JWT token."));
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

    private Mono<Void> handleTokenValidationError(Throwable error, ServerWebExchange exchange, WebFilterChain chain) {
        if (error instanceof BusinessException) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
