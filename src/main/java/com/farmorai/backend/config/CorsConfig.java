package com.farmorai.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * CORS 설정
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3355","http://localhost:8080")
                .allowedMethods("GET", "POST", "DELETE", "PUT","PATCH","OPTIONS","HEAD")
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600);
    }


}
