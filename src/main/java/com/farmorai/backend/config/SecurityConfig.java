package com.farmorai.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF(Cross-Site Request Forgery) 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
//            .cors(cors -> cors.configurationSource(corsSource()))
            // 권한에 따른 HTTP 요청 인가 설정
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                        .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.toString())
//                        .requestMatchers("/auth/**").hasAnyRole(MemberRole.ADMIN.toString(), MemberRole.USER.toString())
                    .anyRequest().permitAll()
            )
            // 로그인 설정
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }


    // CORS 설정
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);    // 쿠키 및 인증 정보(세션, 토큰 등) 전송을 허용하는 설정
        corsConfig.addAllowedHeader("*");        // 모든 HTTP 헤더 허용
        corsConfig.addAllowedMethod("*");        // 모든 HTTP 메소드 허용
        corsConfig.setAllowedOrigins(List.of(    // 접근 허용할 URL 등록
                "http://localhost:3030",
                "http://localhost:9090"
        ));
        // UrlBasedCorsConfigurationSource : 특정 URL 패턴(/**)에 대해 CORS 설정을 적용
        // /** : 모든 엔드포인트(URL)에 대해 CORS 규칙을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
