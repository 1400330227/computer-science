package com.computerscience.hdfsapi.config;

import com.computerscience.hdfsapi.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类，用于注册拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/users/login",   // 登录接口不拦截
                        "/users/logout",  // 登出接口不拦截
                        "/users",         // 用户注册接口不拦截
                        "/login.html",    // 登录页面不拦截
                        "/register.html", // 注册页面不拦截
                        "/js/**",         // JS资源不拦截
                        "/css/**",        // CSS资源不拦截
                        "/images/**",     // 图片资源不拦截
                        "/error"          // 错误页面不拦截
                );
    }
} 