package com.computerscience.hdfsapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 精确匹配前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)  // 关键！允许携带凭证
                .maxAge(3600);
    }
}