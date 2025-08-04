package com.computerscience.hdfsapi.interceptor;

import com.computerscience.hdfsapi.service.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Session验证拦截器
 * 检查用户session是否有效，防止被踢出的session继续访问
 */
@Component
public class SessionValidationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private SessionManagementService sessionManagementService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 获取当前session
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 检查session是否有效（未被踢出）
            if (!sessionManagementService.isSessionValid(session)) {
                // Session已被踢出，返回401错误
                handleKickedOutSession(request, response, session);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 处理被踢出的session
     */
    private void handleKickedOutSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        
        // 获取踢出原因
        String kickReason = (String) session.getAttribute("kick_reason");
        if (kickReason == null) {
            kickReason = "会话已失效";
        }
        
        // 清理session
        try {
            session.invalidate();
        } catch (Exception e) {
            // session可能已经失效，忽略异常
        }
        
        // 设置响应
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        // 返回JSON错误信息
        String jsonResponse = String.format(
            "{\"success\": false, \"message\": \"%s\", \"code\": \"SESSION_KICKED_OUT\"}", 
            kickReason
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
} 