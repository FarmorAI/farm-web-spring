package com.farmorai.backend.securityFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final AuthStrategy authStrategy;

    public AuthenticationFilter(
            AuthenticationManager authManager,
            AuthStrategy authStrategy
    ) {
        this.authenticationManager = authManager;
        this.authStrategy = authStrategy;
    }


    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws AuthenticationException {
        try {
            // Get email, password from Client
            Map<String, String> requestBody = objectMapper.readValue(
                    req.getInputStream(), new TypeReference<>() {}
            );
            String email = requestBody.get("email");
            String password = requestBody.get("password");

            /**
             * "UsernamePasswordAuthenticationToken"에서 email, password 검증을 위한 token 생성
             * Token에 담긴 정보를 검증하기 위해 "AuthenticationManager"로 Token 전달
             */
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse requestBody", e);
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