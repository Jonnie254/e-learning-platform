package com.jonnie.gatewayms.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Extract the JWT token from the request header
        String token = extractToken(exchange);

        if (token != null && isValidToken(token)) {
            // Extract claims from the JWT token
            Claims claims = extractClaims(token);

            // Extract userId and role from the claims
            String userId = claims.getSubject();  // Typically, userId is the subject
            String role = claims.get("role", String.class); // Extract role

            // Create an Authentication object and set it in the SecurityContext
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // Continue processing the request
        return chain.filter(exchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("Authorization");
    }

    private boolean isValidToken(String token) {
        try {
            extractClaims(token); // Will throw an exception if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
