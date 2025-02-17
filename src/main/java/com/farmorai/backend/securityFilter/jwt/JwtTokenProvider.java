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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private SecretKey secretKey;       // JWT 토큰 객체 키를 저장할 시크릿 키

    @Value("${spring.jwt.expiration}")
    private long tokenValidityInTime;  // 토큰 유효 시간(ms)

    /**
     * SecretKey 설정
     * 1. secret        : JWT의 서명을 위한 비밀키(Base64 인코딩된 문자열)를 참조
     * 2. byteSecretKey : Base64로 인코딩된 문자열을 바이트 배열로 변환
     * 3. secretKey     : HMAC-SHA 알고리즘을 사용하여 JWT 서명에 사용할 SecretKey 객체 생성
     */
    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret) {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(byteSecretKey);
    }

    // jwt 토큰 생성
    public String createJwtToken(String email, String role) {
        long now = System.currentTimeMillis();                 // 현재 시간(ms)
        Date validity = new Date(now + tokenValidityInTime);   // 토큰 유효 기간 설정 (현재 시간 + 토큰 유효 시간)

        return Jwts.builder()
                .claim("email", email)                      // "email"로 이메일 정보 추가
                .claim("auth", role)                        // "auth"로 권한 정보 추가
                .setIssuedAt(new Date(now))                    // 토큰 발급 시간 설정
                .setExpiration(validity)                       // 토큰 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256) // JWT 서명 (시크릿 키로 서명)
                .compact();                                    // JWT 토큰을 문자열로 반환
    }

    // jwt 토큰 정보 파싱
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰의 만료 시간 조회
    public Date getExpirationTime(String token) {
        return getClaims(token).getExpiration();
    }

    // 토큰의 남은 유효 시간 조회 (ms)
    public long getRemainingTime(String token) {
        Date expiration = getExpirationTime(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    public Boolean isJwtExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }


    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
