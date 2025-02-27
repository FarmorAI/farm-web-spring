package com.farmorai.backend.securityFilter.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private SecretKey secretKey;
    private final String SECRET = "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5"; // Base64 encoded "testsecretkeytestsecretkeytestsecretkey"
    private final long VALIDITY = 36000000;  // 1시간(ms)
    private Long memberId = 3L;
    private String email = "test@example.com";
    private String role = "ADMIN";
    private String nickname = "testName";

    @BeforeEach
    void setUp() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        jwtTokenProvider = new JwtTokenProvider(SECRET, VALIDITY);
    }

    @Test
    void createJwtToken() {
        String token = jwtTokenProvider.createJwtToken(memberId,email, role, nickname);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

        log.info("claims: {}", claims);
        assertEquals(email, claims.get("email", String.class));
        assertEquals(role, claims.get("role", String.class));
        assertTrue(claims.getExpiration().after(new Date()));
    }


    @Test
    void isJwtExpired() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(SECRET, -1);
        String token = expiredProvider.createJwtToken(memberId,email, role, nickname);
        boolean isExpired = expiredProvider.isJwtExpired(token);
        assertTrue(isExpired);
    }
}