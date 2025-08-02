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
        // 重新启用认证拦截器，但正确配置排除路径
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/users/login",         // 登录接口不拦截
                        "/users/logout",        // 登出接口不拦截
                        "/users/session-debug", // Session调试接口不拦截
                        "/users",               // 用户注册接口不拦截（POST请求）
                        "/hdfs/upload",         // HDFS文件上传接口不拦截
                        "/corpus/upload",       // 语料库文件上传接口不拦截
                        "/login.html",          // 登录页面不拦截
                        "/register.html",       // 注册页面不拦截
                        "/test-login.html",     // 测试页面不拦截
                        "/test-upload.html",    // 文件上传测试页面不拦截
                        "/js/**",               // JS资源不拦截
                        "/css/**",              // CSS资源不拦截
                        "/images/**",           // 图片资源不拦截
                        "/assets/**",           // 前端资源文件不拦截
                        "/static/**",           // 静态资源不拦截
                        "/error",               // 错误页面不拦截
                        "/favicon.ico",         // 网站图标不拦截
                        "/",                    // 根路径不拦截
                        "/index.html",          // 首页不拦截
                        "/login",               // Vue前端登录路由不拦截
                        "/register"             // Vue前端注册路由不拦截
                );
    }
} 