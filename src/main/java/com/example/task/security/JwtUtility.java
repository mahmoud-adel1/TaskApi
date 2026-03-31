package com.example.task.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtUtility {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-minutes}")
    private long accessTokenExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        return buildToken(userDetails.getUsername(),role);
    }

    public String buildToken(String subject, String role) {
        return Jwts
                .builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (1000L * 60 * accessTokenExpiration)))
                .signWith(getSignWithKey())
                .compact();
    }

    private SecretKey getSignWithKey() {
        byte[] decodedSecretKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodedSecretKey);
    }

    public Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignWithKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSignWithKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}