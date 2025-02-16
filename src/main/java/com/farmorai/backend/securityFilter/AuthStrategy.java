package com.farmorai.backend.securityFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface AuthStrategy {
    void onAuthSuccess(
            HttpServletRequest req,
            HttpServletResponse resp,
            Authentication authentication
    ) throws IOException;

    void configHttpSecurity(HttpSecurity http) throws Exception;

    void logout(HttpServletRequest req, HttpServletResponse resp);
}
