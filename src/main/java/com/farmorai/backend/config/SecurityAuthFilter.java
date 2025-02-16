package com.farmorai.backend.config;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class SecurityAuthFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private final AuthenticationManager authManager;

    public SecurityAuthFilter(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    // 인바운드 토큰 생성
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp)
            throws AuthenticationException {
        try {
            Map<String, String> requestBody = new ObjectMapper()
                    .readValue(req.getInputStream(), new TypeReference<Map<String, String>>() {});
            String email = requestBody.get("email");
            String password = requestBody.get("password");
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);
            return authManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    // 로그인 성공 시
    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {

        HttpSession session = req.getSession(true);  //세션 생성
        session.setMaxInactiveInterval(1800);      // 세션 만료 시간 (1800초:30분)

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Login Success");
    }

    // 로그인 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed)
            throws IOException, ServletException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write("Login Failed");
    }
}