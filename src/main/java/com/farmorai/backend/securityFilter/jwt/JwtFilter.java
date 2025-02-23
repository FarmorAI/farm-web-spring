package com.farmorai.backend.securityFilter.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청 헤더 "Authorization" 키에 JWT가 존재할 경우, JWT 검증을 위한 커스텀 필터 등록 필요
 * 1. JWT를 검증하고 강제로 "SecurityContextHolder"에 세션 생성
 * 2. 이 세션은 STATLESS 상태로 관리되므로 해당 요청이 끝나면 소멸됨
 */
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = req.getHeader("Authorization");

        // 🚨 추가된 로그
        log.info("🔍 Received Authorization Header: {}", authorization);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("🚨 JWT token is missing or does not start with 'Bearer'");
            filterChain.doFilter(req, resp);
            return;
        }

        String[] authParts = authorization.split(" ");
        if (authParts.length < 2) {
            log.warn("🚨 JWT token format is incorrect: {}", authorization);
            filterChain.doFilter(req, resp);
            return;
        }

        String token = authParts[1];

        // JWT 형식 확인
        if (!token.contains(".") || token.split("\\.").length != 3) {
            log.warn("🚨 JWT token is not correctly formatted (dots missing): {}", token);
            filterChain.doFilter(req, resp);
            return;
        }

        if (jwtTokenProvider.isJwtExpired(token)) {
            log.warn("🚨 JWT token expired");
            filterChain.doFilter(req, resp);
            return;
        }

        String email = jwtTokenProvider.getEmail(token);
        String role = jwtTokenProvider.getRole(token);

        UserDetails userDetails = User.builder()
                .username(email)
                .password("")
                .roles(role)
                .build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(req, resp);
    }
}