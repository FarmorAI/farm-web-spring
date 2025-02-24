package com.farmorai.backend.securityFilter.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 요청 헤더 "Authorization" 키에 JWT가 존재할 경우, JWT 검증을 위한 커스텀 필터 등록 필요
 * 1. JWT를 검증하고 강제로 "SecurityContextHolder"에 세션 생성
 * 2. 이 세션은 STATLESS 상태로 관리되므로 해당 요청이 끝나면 소멸됨
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain)
            throws ServletException, IOException {

        // "Request"의 Header:"Authorization"을 찾아 "JWT"를 반환
        String token = resolveToken(req);

        // JWT 존재 여부 확인
        if(token == null) {
            log.debug("No JWT found in request: {}", req.getRequestURI());
            filterChain.doFilter(req, resp);
            return;
        }

        try {
            // JWT 유효성 확인
            if (jwtTokenProvider.isJwtExpired(token)) {
               log.warn("Expried JWT: {}", req.getRequestURI());
               filterChain.doFilter(req, resp);
               return;
            }
            Authentication authentication = createAuth(token);  // 스프링 시큐리티 인증 토큰 생성
            SecurityContextHolder.getContext().setAuthentication(authentication);  // 세션에 사용자 등록

        } catch (Exception e) {
            log.error("Invalid JWT for Request: {}", req.getRequestURI(), e);
            resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or malformed JWT");
            return;
        }

        filterChain.doFilter(req, resp);
    }


    private String resolveToken(HttpServletRequest req) {
        // Request -> "Authorization" Header 찾음
        String authorization = req.getHeader("Authorization");

        // "Authorization" Header 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.split(" ")[1];
    }


    // Authentication 토큰 생성
    private Authentication createAuth(String token) {
        String email = jwtTokenProvider.getEmail(token);  // token -> email
        String role = jwtTokenProvider.getRole(token);    // token -> role
        List<SimpleGrantedAuthority> auth = jwtTokenProvider.getAuthorities(role);
        User principal = new User(email, "", auth);

        return new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );
    }
}