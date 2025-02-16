package com.farmorai.backend.securityFilter.jwt;

import com.farmorai.backend.securityFilter.AuthStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @ConditionalOnProperty
 * 프로퍼티(auth.strategy) 값(jwt)에 따라 Bean 등록 여부를 동적으로 결정
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth.strategy", havingValue = "jwt")
public class JwtAuthStrategy implements AuthStrategy {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthSuccess(
            HttpServletRequest req,
            HttpServletResponse resp,
            Authentication authentication
    ) throws IOException {
        String token = jwtTokenProvider.createToken(authentication);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(resp.getWriter(),
            Map.of(
                "success", true,
                "message", "Login Success",
                "token", token
            )
        );
    }

    @Override
    public void configHttpSecurity(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                // Authorization 키에 JWT가 존재하는 경우
                // JWT를 검증하고 강제로 SecurityContextHolder 에 세션을 생성한다.
                // (이 세션은 STATELESS 상태로 관리되므로 해당 요청이 끝나면 소멸됨)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                new JwtAuthFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(resp.getWriter(),
                Map.of(
                    "success", true,
                    "message", "Logout Success"
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
