package com.computerscience.hdfsapi.interceptor;

import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.utils.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String ADMIN_USER_TYPE = "admin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("=== AdminAuthInterceptor 调试 ===");
        System.out.println("请求路径: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());
        
        User user = UserContext.getCurrentUser();
        System.out.println("当前用户: " + (user != null ? user.getAccount() : "null"));
        if (user != null) {
            System.out.println("用户ID: " + user.getUserId());
            System.out.println("用户类型: " + user.getUserType());
            System.out.println("用户类型是否为null: " + (user.getUserType() == null));
            System.out.println("用户类型字符串长度: " + (user.getUserType() != null ? user.getUserType().length() : "null"));
        }
        System.out.println("是否为管理员: " + (user != null && ADMIN_USER_TYPE.equals(user.getUserType())));

        if (user == null) {
            System.out.println("权限验证失败：用户为null");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: User not found.");
            return false;
        }
        
        if (!ADMIN_USER_TYPE.equals(user.getUserType())) {
            System.out.println("权限验证失败：用户类型不是admin，实际类型：'" + user.getUserType() + "'");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Administrative privileges required.");
            return false;
        }

        System.out.println("权限验证通过");
        System.out.println("===============================");
        return true;
    }
} 