package com.farmorai.backend.securityFilter.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthStrategyTest {
    @InjectMocks
    private JwtAuthStrategy jwtAuthStrategy;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 인증 성공 시, "Header"에 JWT 토큰을 추가
    @Test
    void testOnAuthSuccess() throws Exception {
        User user = new User(
                "test@example.com",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtTokenProvider.createJwtToken("test@example.com", "USER")).thenReturn("validToken");

        jwtAuthStrategy.onAuthSuccess(req, resp, authentication);

        verify(resp).addHeader("Authorization", "Bearer validToken");
    }

}