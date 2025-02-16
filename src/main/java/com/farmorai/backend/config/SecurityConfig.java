package com.farmorai.backend.config;

import com.farmorai.backend.dto.MemberRole;
import com.farmorai.backend.service.MemberDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberDetailsService memberDetailsService;

    // 비밀번호 단방향 암호화 인터페이스 (Bean 등록)
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString(); // 비밀번호를 그대로 반환
            }
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword); // 평문 비교
            }
        };
    }

    // "DaoAuthenticationProvider"는
    // "MemberDetailsService"가 반환한 "UserDetails" 객체를 가지고
    // "UsernamePasswordAuthentication" 객체를 만들어 "ProviderManager"에 제공
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authManager(DaoAuthenticationProvider authProvider) throws Exception {
        return new ProviderManager(authProvider);
    }


    // 보안 필터 체인 (Bean 등록)
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        SecurityAuthFilter securityAuthFilter = new SecurityAuthFilter(authManager);
        securityAuthFilter.setFilterProcessesUrl("/login"); // URL 수정

        http.csrf(AbstractHttpConfigurer::disable)  // CSRF(Cross-Site Request Forgery) 비활성화
            .cors(cors -> cors.configurationSource(corsSource()))
            // HTTP 요청 인가 설정
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.toString())
                    .requestMatchers("/auth/**").hasAnyRole(MemberRole.ADMIN.toString(), MemberRole.USER.toString())
                    .anyRequest().permitAll()
            )
            // 로그인 인증 설정
            .addFilterBefore(
                    securityAuthFilter,
                    UsernamePasswordAuthenticationFilter.class
            )
            // 세션 관리 설정
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)               // 동시 세션 제한
                    .maxSessionsPreventsLogin(false)  // 기존 세션 만료
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write("로그아웃 성공");
                })
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );
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
                "http://localhost:9090",
                "http://localhost:3306"
        ));

        // UrlBasedCorsConfigurationSource : 특정 URL 패턴(/**)에 대해 CORS 설정을 적용
        // /** : 모든 엔드포인트(URL)에 대해 CORS 규칙을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}