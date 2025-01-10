package com.jonnie.elearning.jwt;

import com.jonnie.elearning.user.User;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.expiration-time}")
    private long jwtExpirationTime;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }
    public String generateToken(HashMap<String, Object>  claims, User user) {
        return buildToken(claims, user, jwtExpirationTime);
    }

    private String buildToken(
            Map<String, Object> claims,
            User user,
            long jwtExpirationTime) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte [] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
