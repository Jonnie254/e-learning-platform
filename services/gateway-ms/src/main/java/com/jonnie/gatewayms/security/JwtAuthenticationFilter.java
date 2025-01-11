// JwtAuthenticationFilter.java
package com.jonnie.gatewayms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // Log all headers for debugging
        exchange.getRequest().getHeaders().forEach((key, value) -> {
            log.debug("Header '{}': {}", key, value);
        });

        String token = extractToken(exchange);
        if (token == null) {
            log.warn("No token found in request");
            return chain.filter(exchange);
        }
        log.info("Token extracted: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

        try {
            if (!isValidToken(token)) {
                log.error("Token validation failed");
                return chain.filter(exchange);
            }
            log.info("Token validation successful");

            Claims claims = extractClaims(token);
            log.info("Claims extracted: {}", claims);

            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);
            log.info("Authentication details - userId: {}, role: {}", userId, role);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authentication object set in SecurityContext");

        } catch (Exception e) {
            log.error("Error during authentication process", e);
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        try {
            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            log.debug("Authorization header: {}", authorizationHeader);

            if (authorizationHeader == null) {
                log.warn("Authorization header is missing");
                return null;
            }

            if (!authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization header does not start with 'Bearer '");
                return null;
            }

            return authorizationHeader.substring(7);
        } catch (Exception e) {
            log.error("Error extracting token", e);
            return null;
        }
    }

    private boolean isValidToken(String token) {
        try {
            log.debug("Attempting to validate token");
            Key signingKey = getSigningKey();
            log.debug("Signing key generated");

            extractClaims(token);
            log.info("Token validated successfully");
            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage(), e);
            return false;
        }
    }

    private Key getSigningKey() {
        try {
            log.debug("Secret key length: {}", secretKey.length());
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            log.debug("Key bytes generated, length: {}", keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error generating signing key", e);
            throw e;
        }
    }

    private Claims extractClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Claims extracted successfully: {}", claims);
            return claims;
        } catch (Exception e) {
            log.error("Error extracting claims", e);
            throw e;
        }
    }
}