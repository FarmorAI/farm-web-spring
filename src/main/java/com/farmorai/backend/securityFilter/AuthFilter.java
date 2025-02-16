package com.farmorai.backend.securityFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Map;

public class AuthFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthStrategy authStrategy;

    public AuthFilter(
            AuthenticationManager authManager,
            AuthStrategy authStrategy
    ) {
        super("/login");
        this.setAuthenticationManager(authManager);
        this.authStrategy = authStrategy;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws AuthenticationException, IOException, ServletException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(
                req.getInputStream(),
                LoginRequest.class
            );
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            );
            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed Login Request");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        authStrategy.onAuthSuccess(req, resp, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse resp,
            AuthenticationException failed
    ) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(),
            Map.of(
                "success", false,
                "message", "Login Failed"
            )
        );
    }
}
