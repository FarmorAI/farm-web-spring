package com.farmorai.backend.securityFilter.jwt;

import io.jsonwebtoken.*;
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

    @Value("${jwt.expiration}")
    private long tokenValidityInTime; // 토큰 유효 시간(ms)
    private SecretKey secretKey;      // JWT 토큰 객체 키를 저장할 시크릿 키

    // SecretKey 생성
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());  // HMAC-SHA 알고리즘으로 서명한 키 생성
    }

    // jwt 토큰 생성
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));    // "ROLE_USER,ROLE_ADMIN" 문자열로 권한 반환

        long now = System.currentTimeMillis();                // 현재 시간(ms)
        Date validity = new Date(now + tokenValidityInTime);  // 토큰 유효 기간 설정 (현재 시간 + 토큰 유효 시간)

        return Jwts.builder()
                .setSubject(authentication.getName()) // 토큰의 주체 설정 (인증된 사용자 이름)
                .claim("auth", authorities)        // "auth"라는 키로 권한 정보 추가
                .setIssuedAt(new Date(now))           // 토큰 발급 시간 설정
                .setExpiration(validity)              // 토큰 만료 시간 설정
                .signWith(secretKey)                  // JWT 서명 (시크릿 키로 서명)
                .compact();                           // JWT 토큰을 문자열로 반환
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

/**
 * 주요 기능:
 *
 * 토큰 생성 (createToken):
 *
 * 사용자 이름과 권한 정보를 포함
 * 발급 시간과 만료 시간 설정
 * HMAC-SHA 알고리즘으로 서명
 *
 *
 * 토큰 인증 (getAuthentication):
 *
 * 토큰에서 사용자 정보와 권한 추출
 * Spring Security Authentication 객체 생성
 *
 *
 * 토큰 검증 (validateToken):
 *
 * 서명 검증
 * 만료 여부 확인
 * 토큰 형식 검증
 * 상세한 예외 처리와 로깅
 *
 *
 * 유틸리티 메서드:
 *
 * getClaims: 토큰에서 클레임 정보 추출
 * getExpirationDate: 만료 시간 조회
 * getRemainingTime: 남은 유효 시간 조회
 */
