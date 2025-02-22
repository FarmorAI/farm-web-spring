package com.farmorai.backend.securityFilter.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;       // JWT 토큰 객체 키를 저장할 시크릿 키
    private final long tokenValidityInTime;  // 토큰 유효 시간(ms)

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
        this.tokenValidityInTime = tokenValidityInTime;
    }

    // JWT 생성
    public String createJwtToken(String email, String role) {
        long now = System.currentTimeMillis();                 // 현재 시간(ms)

        return Jwts.builder()
                .claim("email", email)                        // "email"로 이메일 정보 추가
                .claim("role", role)                          // "auth"로 권한 정보 추가
                .setIssuedAt(new Date(now))                         // 토큰 발급 시간 설정
                .setExpiration(new Date(now + tokenValidityInTime)) // 토큰 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256)      // JWT 서명 (시크릿 키로 서명)
                .compact();                                         // JWT 토큰을 문자열로 반환
    }


    // email 조회
    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("email", String.class);
    }

    // 권한 조회
    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    // ROLE_ 권한 조회
    public List<SimpleGrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // jwt 토큰 유효 여부 검증
    public Boolean isJwtExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
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