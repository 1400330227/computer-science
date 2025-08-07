package com.computerscience.hdfsapi.interceptor;

import com.computerscience.hdfsapi.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证拦截器，用于检查用户是否已登录
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("=== AuthInterceptor 调试 ===");
        System.out.println("请求路径: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());
        
        // 检查用户是否已登录
        boolean isLoggedIn = UserContext.isUserLoggedIn();
        System.out.println("用户是否已登录: " + isLoggedIn);
        
        if (!isLoggedIn) {
            System.out.println("AuthInterceptor: 用户未登录，拒绝访问");
            // 判断是否是AJAX请求
            if (isAjaxRequest(request)) {
                // 对于AJAX请求，返回JSON格式的未授权错误
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "You are not logged in or your login has expired. Please log in again.");
                errorResponse.put("code", HttpStatus.UNAUTHORIZED.value());
                
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            } else {
                // 对于普通请求，重定向到登录页面
                response.sendRedirect("/login");
            }
            return false;
        }
        
        System.out.println("AuthInterceptor: 验证通过，继续处理");
        System.out.println("==============================");
        return true;
    }
    
    /**
     * 判断是否是AJAX请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        
        return (requestedWith != null && "XMLHttpRequest".equals(requestedWith)) ||
               (contentType != null && contentType.contains("application/json")) ||
               (accept != null && accept.contains("application/json"));
    }
} 