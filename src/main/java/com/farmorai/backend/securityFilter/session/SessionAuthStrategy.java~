package com.farmorai.backend.securityFilter.session;

import com.farmorai.backend.securityFilter.AuthStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @ConditionalOnProperty
 * 프로퍼티(auth.strategy) 값(session)에 따라 Bean 등록 여부를 동적으로 결정
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth.strategy", havingValue = "session")
public class SessionAuthStrategy implements AuthStrategy {

    @Value("${session.timeout}")
    private int sessionTimeout;

    @Override
    public void onAuthSuccess(
            HttpServletRequest req,
            HttpServletResponse resp,
            Authentication authentication
    ) throws IOException {
        HttpSession session = req.getSession(true);  // 세션 생성
        session.setMaxInactiveInterval(sessionTimeout);    // 세션 만료 시간 설정
        writeResponse(resp, "Login Success");
    }

    @Override
    public void configHttpSecurity(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 판정
                .maximumSessions(1)              // 동시 세션 제한
                .maxSessionsPreventsLogin(false) // 기존 세션 만료
        );
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);

        if(session != null) { session.invalidate(); }
        writeResponse(resp, "Logout Success");
    }


    private void writeResponse(HttpServletResponse resp, String msg) {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            // ObjectMapper를 사용해 JSON 자동 변환 후, respMap을 resp.getWriter()에 씌움
            Map<String, String> respMap = Map.of("msg", msg);
            new ObjectMapper().writeValue(resp.getWriter(), respMap);

        } catch (IOException e) {
            throw new RuntimeException(e);  // 예외 발생 시, 런타임 에러 반환
        }
    }
}
