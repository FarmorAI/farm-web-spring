package com.farmorai.backend.config;

import com.farmorai.backend.dto.MemberRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * "@EnableWebSecurity"는 다양한 보안 기능을 활성화하는 역할을 하며,
 * 모든 웹 요청 URL이 스프링 시큐리티의 제어를 받게 합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 단방향 암호화 인터페이스 (Bean 등록)
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인 (Bean 등록)
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        // CSRF(Cross-Site Request Forgery) 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
                // 권한에 따른 HTTP 요청 인가 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.toString())
                        .requestMatchers("/auth/**").hasRole(MemberRole.USER.name())
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
