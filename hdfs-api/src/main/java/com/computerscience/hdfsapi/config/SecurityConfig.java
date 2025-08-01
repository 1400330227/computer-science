package com.computerscience.hdfsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * 安全配置类，用于配置Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用基本认证，因为我们使用自己的登录系统
            .httpBasic(AbstractHttpConfigurer::disable)
            // 禁用表单登录，因为我们使用自己的登录页面
            .formLogin(AbstractHttpConfigurer::disable)
            // 允许所有请求，认证由我们的拦截器处理
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            // 配置CSRF保护
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/users/login", "/users/logout", "/users")
            )
            // 配置响应头
            .headers(headers -> headers
                .xssProtection(Customizer.withDefaults())
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:;"))
                .frameOptions(frameOptions -> frameOptions.deny())
            );

        return http.build();
    }
} 