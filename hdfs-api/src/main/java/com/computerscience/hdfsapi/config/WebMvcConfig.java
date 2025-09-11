package com.computerscience.hdfsapi.config;

import com.computerscience.hdfsapi.interceptor.AdminAuthInterceptor;
import com.computerscience.hdfsapi.interceptor.AuthInterceptor;
import com.computerscience.hdfsapi.interceptor.SessionValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类，用于注册拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final SessionValidationInterceptor sessionValidationInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor, SessionValidationInterceptor sessionValidationInterceptor, AdminAuthInterceptor adminAuthInterceptor) {
        this.authInterceptor = authInterceptor;
        this.sessionValidationInterceptor = sessionValidationInterceptor;
        this.adminAuthInterceptor = adminAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 首先添加Session验证拦截器（检查session是否被踢出）
        registry.addInterceptor(sessionValidationInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/users/login",   // 登录接口不拦截（已废弃，保留兼容性）
                        "/users/login-with-sms",  // 短信验证码登录接口不拦截
                        "/users/send-verification-code",  // 发送验证码接口不拦截
                        "/users/public-key",  // 获取公钥接口不拦截（已废弃，保留兼容性）
                        "/users/logout",  // 登出接口不拦截（登出时需要清理）
                        "/users/heartbeat",  // 心跳检测接口不拦截
                        "/users",         // 用户注册接口不拦截
                        "/login.html",    // 登录页面不拦截
                        "/register.html", // 注册页面不拦截
                        "/js/**",         // JS资源不拦截
                        "/css/**",        // CSS资源不拦截
                        "/images/**",     // 图片资源不拦截
                        "/error",          // 错误页面不拦截
                        "/dataView/**"    // 数据统计页面
                );
        
        // 然后添加认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/users/login",   // 登录接口不拦截（已废弃，保留兼容性）
                        "/users/login-with-sms",  // 短信验证码登录接口不拦截
                        "/users/send-verification-code",  // 发送验证码接口不拦截
                        "/users/public-key",  // 获取公钥接口不拦截（已废弃，保留兼容性）
                        "/users/logout",  // 登出接口不拦截
                        "/users/heartbeat",  // 心跳检测接口不拦截
                        "/users",         // 用户注册接口不拦截
                        "/login.html",    // 登录页面不拦截
                        "/register.html", // 注册页面不拦截
                        "/js/**",         // JS资源不拦截
                        "/css/**",        // CSS资源不拦截
                        "/images/**",     // 图片资源不拦截
                        "/error",        // 错误页面不拦截
                        "/dataView/**"    // 数据统计页面

                );

        // 最后添加管理员权限拦截器，只拦截管理员API路径
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**");  // 修正拦截路径
    }
} 