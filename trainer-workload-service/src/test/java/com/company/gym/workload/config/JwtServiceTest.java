package com.company.gym.workload.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    private String generateTestToken(String username, long expirationMillis) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = generateTestToken("test.user", 1000 * 60 * 60);
        String username = jwtService.extractUsername(token);
        assertEquals("test.user", username);
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenNotExpired() {
        String token = generateTestToken("test.user", 1000 * 60 * 60);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenExpired() {
        String token = generateTestToken("test.user", -1000);
        assertFalse(jwtService.isTokenValid(token));
    }
}