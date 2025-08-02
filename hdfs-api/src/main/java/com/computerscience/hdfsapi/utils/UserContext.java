package com.computerscience.hdfsapi.utils;

import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 用户上下文工具类，用于在各个组件中获取当前登录用户信息
 */
@Component
public class UserContext {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        UserContext.userService = userService;
    }

    /**
     * 获取当前请求
     * @return HttpServletRequest对象
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取当前会话
     * @return HttpSession对象
     */
    public static HttpSession getCurrentSession() {
        HttpServletRequest request = getCurrentRequest();
        // 使用getSession(true)确保Session存在，如果不存在则创建
        return request != null ? request.getSession(true) : null;
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID，如果未登录则返回null
     */
    public static Integer getCurrentUserId() {
        // 调试信息
        System.out.println("=== UserContext.getCurrentUserId() 调试 ===");
        
        // 首先尝试从Session获取
        HttpServletRequest request = getCurrentRequest();
        HttpSession session = getCurrentSession();
        
        System.out.println("HttpServletRequest: " + (request != null ? "存在" : "null"));
        System.out.println("HttpSession: " + (session != null ? "存在" : "null"));
        
        if (session != null) {
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session 属性数量: " + java.util.Collections.list(session.getAttributeNames()).size());
            for (String attrName : java.util.Collections.list(session.getAttributeNames())) {
                System.out.println("Session 属性: " + attrName + " = " + session.getAttribute(attrName));
            }
        }
        
        Integer userId = session != null ? (Integer) session.getAttribute("currentUser") : null;
        System.out.println("从Session获取的用户ID: " + userId);
        
        // 如果Session中没有，尝试从Cookie获取
        if (userId == null) {
            System.out.println("Session中无用户ID，尝试从Cookie获取");
            if (request != null && request.getCookies() != null) {
                System.out.println("Cookie数量: " + request.getCookies().length);
                for (Cookie cookie : request.getCookies()) {
                    System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
                    if ("currentUser".equals(cookie.getName()) && cookie.getValue() != null) {
                        try {
                            userId = Integer.parseInt(cookie.getValue());
                            System.out.println("从Cookie获取的用户ID: " + userId);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Cookie值转换失败: " + cookie.getValue());
                        }
                    }
                }
            } else {
                System.out.println("无Cookie信息");
            }
        }
        
        System.out.println("最终用户ID: " + userId);
        System.out.println("=======================================");
        
        return userId;
    }

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户对象，如果未登录则返回null
     */
    public static User getCurrentUser() {
        Integer userId = getCurrentUserId();
        return userId != null ? userService.getById(userId) : null;
    }

    /**
     * 检查用户是否已登录
     * @return 如果用户已登录则返回true，否则返回false
     */
    public static boolean isUserLoggedIn() {
        return getCurrentUserId() != null;
    }
} 