package com.farmorai.backend.securityFilter.jwt;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.global.response.ResponseApi;
import com.farmorai.backend.mapper.MemberMapper;
import com.farmorai.backend.securityFilter.AuthStrategy;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @ConditionalOnProperty
 * : spring.auth.strategy 값(jwt)에 따라 Bean 등록 여부를 동적으로 결정
 */

@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.auth.strategy", havingValue = "jwt")
public class JwtAuthStrategy implements AuthStrategy {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemberMapper memberMapper;

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
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // JWT 토큰 생성 및 "Header"에 추가
        String token = jwtTokenProvider.createJwtToken(
                userDetails.getMemberId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                userDetails.getNickname()
        );

        MemberDto member = memberMapper.getMemberByEmail(userDetails.getUsername());


        resp.addHeader("Authorization", "Bearer " + token);

        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        // :흰색_확인_표시: 응답 데이터 생성
        // ✅ 응답 바디에 사용자 정보 추가
        objectMapper.writeValue(resp.getWriter(),
               ResponseApi.success("로그인 성공", Map.of(
                       "token", token,
                       "user", Map.of(
                               "email", userDetails.getUsername(),
                               "nickname", userDetails.getNickname(),
                               "imageUrl", member.getImageUrl(),
                               "memberRole", userDetails.getAuthorities().iterator().next().getAuthority()
                       )
               ))
        );

    }

    /**
     * Authorization 키에 JWT가 존재하는 경우
     * JWT를 검증하고 강제로 SecurityContextHolder에 세션을 생성한다.
     * (이 세션은 STATELESS 상태로 관리되므로 해당 요청이 끝나면 소멸됨)
     */
    @Override
    public void configHttpSecurity(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                new JwtFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resp.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(resp.getWriter(),
                Map.of(
                    "success", true,
                    "message", "Logout Success"
                )
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON", e);
        }
    }
}
