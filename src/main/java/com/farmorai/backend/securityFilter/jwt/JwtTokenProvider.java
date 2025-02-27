package com.farmorai.backend.securityFilter.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;       // JWT 토큰 객체 키를 저장할 시크릿 키
    private final long tokenValidityTime;  // 토큰 유효 시간(ms)

    /**
     * SecretKey 설정
     * 1. secret        : JWT의 서명을 위한 비밀키(Base64 인코딩된 문자열)를 참조
     * 2. byteSecretKey : Base64로 인코딩된 문자열을 바이트 배열로 변환
     * 3. secretKey     : HMAC-SHA 알고리즘을 사용하여 JWT 서명에 사용할 SecretKey 객체 생성
     */
    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.expiration}") long tokenValidityInTime
    ) {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(byteSecretKey);
        this.tokenValidityTime = tokenValidityInTime;
    }


    // JWT 생성 (memberId 추가)
    public String createJwtToken(Long memberId, String email, String role, String nickname) {
        long now = System.currentTimeMillis();  // 현재 시간(ms)

        return Jwts.builder()
                .claim("memberId", memberId)  // "memberId" 추가
                .claim("email", email)
                .claim("nickname", nickname)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + tokenValidityTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // member_id 조회
    public Long getMemberId(String token) { return parseClaims(token).get("memberId",Long.class); }

    // email 조회
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    // nickname 조회
    public String getNickname(String token) {
        return parseClaims(token).get("nickname", String.class);
    }

    // 권한 조회
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // ROLE_ 권한 조회
    public List<SimpleGrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // JWT 토큰 유효 여부 검증
    public Boolean isJwtExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}