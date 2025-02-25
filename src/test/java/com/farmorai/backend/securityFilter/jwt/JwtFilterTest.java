package com.farmorai.backend.securityFilter.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    // @InjectMocks <- @Mock 객체를 자동 주입
    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        // "@Mock"을 선언한 모든 객체가 자동 초기화 됨
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();  // 인증 상태 초기화
    }

    // 토큰이 없는 경우
    @Test
    void doFilterInternal_NoToken() throws Exception {
        // 원하는 동작을 when(...).thenReturn(...)을 통해 정의
        when(req.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(req, resp, filterChain);

        verify(filterChain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 유효 하지 않은 토큰이 들어온 경우
    @Test
    void doFilterInternal_InvalidToken() throws Exception {
        // 원하는 동작을 when(...).thenReturn(...)을 통해 정의
        when(req.getHeader("Authorization")).thenReturn("invalidToken");

        jwtFilter.doFilterInternal(req, resp, filterChain);

        verify(filterChain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 만료된 토큰
    @Test
    void doFilterInternal_ExpiredToken() throws Exception {
        // 원하는 동작을 when(...).thenReturn(...)을 통해 정의
        when(req.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtTokenProvider.isJwtExpired("expiredToken")).thenReturn(true);

        jwtFilter.doFilterInternal(req, resp, filterChain);

        verify(filterChain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 유효한 토큰
    @Test
    void doFilterInternal_ValidToken() throws Exception {
        String email = "test@email.com";
        String role = "USER";

        // 원하는 동작을 when(...).thenReturn(...)을 통해 정의
        when(req.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.isJwtExpired("validToken")).thenReturn(false);
        when(jwtTokenProvider.getEmail("validToken")).thenReturn(email);
        when(jwtTokenProvider.getRole("validToken")).thenReturn(role);

        jwtFilter.doFilterInternal(req, resp, filterChain);

        verify(filterChain).doFilter(req, resp);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    // 잘못된 토큰
    @Test
    void doFilterInternal_MalformedToken() throws Exception {
        // 원하는 동작을 when(...).thenReturn(...)을 통해 정의
        when(req.getHeader("Authorization")).thenReturn("Bearer malformedToken");
        when(jwtTokenProvider.isJwtExpired("malformedToken")).thenReturn(false);

        jwtFilter.doFilterInternal(req, resp, filterChain);

        verify(resp).sendError(401, "Invalid or malformed JWT");
        verify(filterChain, never()).doFilter(req, resp);
    }
}