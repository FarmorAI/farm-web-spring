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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @ConditionalOnProperty : spring.auth.strategy 값(jwt)에 따라 Bean 등록 여부를 동적으로 결정
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.auth.strategy", havingValue = "jwt")
public class JwtAuthStrategy implements AuthStrategy {
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 인증이 성공했을 때, 실행되는 메서드
     * Authentication  : 인증된 사용자 정보를 담고 있는 객체
     * .getPrincipal() : UserDetails 객체를 가져옴
     * .getAuthorities() : Collection<GrantedAuthority> 객체를 가져옴
     */
    @Override
    public void onAuthSuccess(
            HttpServletRequest req,
            HttpServletResponse resp,
            Authentication authentication
    ) throws IOException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtTokenProvider.createJwtToken(email, role);

        resp.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    public void configHttpSecurity(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                // Authorization 키에 JWT가 존재하는 경우
                // JWT를 검증하고 강제로 SecurityContextHolder 에 세션을 생성한다.
                // (이 세션은 STATELESS 상태로 관리되므로 해당 요청이 끝나면 소멸됨)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                new JwtFilter(jwtTokenProvider),
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
