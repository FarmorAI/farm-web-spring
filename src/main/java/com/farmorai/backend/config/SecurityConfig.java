package com.farmorai.backend.config;

import com.farmorai.backend.dto.MemberRole;
import com.farmorai.backend.securityFilter.AuthStrategy;
import com.farmorai.backend.securityFilter.AuthenticationFilter;
import com.farmorai.backend.service.MemberDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
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
    private final AuthStrategy authStrategy;
    private final MemberDetailsService memberDetailsService;

    // 비밀번호 단방향 암호화 인터페이스 (Bean 등록)
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // "DaoAuthenticationProvider"는
    // "MemberDetailsService"가 반환한 "UserDetails" 객체를 가지고,
    // "UsernamePasswordAuthentication" 객체를 만들어 "ProviderManager"에 제공
    @Bean
    public AuthenticationManager authManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider DaoAuthProvider = new DaoAuthenticationProvider();
        DaoAuthProvider.setUserDetailsService(memberDetailsService);
        DaoAuthProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(DaoAuthProvider);
    }

    // 보안 필터 체인 (Bean 등록)
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        AuthenticationFilter authFilter = new AuthenticationFilter(authManager, authStrategy);
        authFilter.setFilterProcessesUrl("/login"); // 로그인 인증 URL

        http.csrf((auth) -> auth.disable())  // CSRF(Cross-Site Request Forgery) 비활성화
            .cors(cors -> cors.configurationSource(corsSource()))
            .formLogin((auth) -> auth.disable())
            .httpBasic((auth) -> auth.disable())
            // HTTP 요청 경로별 인가 설정
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                    .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.toString())
//                    .requestMatchers("/auth/**").hasAnyRole(MemberRole.ADMIN.toString(), MemberRole.USER.toString())
                    .anyRequest().permitAll()
            )
            // Login 설정
            .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
            // Logout 설정
            .logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessHandler((req, res, authentication) ->
                        authStrategy.logout(req, res))
            );

        authStrategy.configHttpSecurity(http);  // 전략별 추가 설정 적용
        return http.build();
    }


    // HTTP 요청 경로별 인가 설정
    private void configAuth(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authz) {
        authz
            .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.name())
            .requestMatchers("/auth/**").hasAnyRole(MemberRole.ADMIN.name(), MemberRole.USER.name())
            .anyRequest().permitAll();
    }


    /**
     * ** CORS 설정 **
     * UrlBasedCorsConfigurationSource : 특정 URL 패턴(/**)에 대해 CORS 설정을 적용
     * /** : 모든 엔드포인트(URL)에 대해 CORS 규칙을 적용
      */
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);    // 쿠키 및 인증 정보(세션, 토큰 등) 전송을 허용하는 설정
        corsConfig.addAllowedHeader("*");        // 모든 HTTP 헤더 허용
        corsConfig.addAllowedMethod("*");        // 모든 HTTP 메소드 허용
        corsConfig.setAllowedOriginPatterns(List.of("*")); // 모든 도메인 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
