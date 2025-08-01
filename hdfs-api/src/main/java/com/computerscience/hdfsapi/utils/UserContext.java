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
        return request != null ? request.getSession(false) : null;
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID，如果未登录则返回null
     */
    public static Integer getCurrentUserId() {
        // 首先尝试从Session获取
        HttpSession session = getCurrentSession();
        Integer userId = session != null ? (Integer) session.getAttribute("currentUser") : null;
        
        // 如果Session中没有，尝试从Cookie获取
        if (userId == null) {
            HttpServletRequest request = getCurrentRequest();
            if (request != null && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("currentUser".equals(cookie.getName()) && cookie.getValue() != null) {
                        try {
                            userId = Integer.parseInt(cookie.getValue());
                            break;
                        } catch (NumberFormatException e) {
                            // 忽略转换错误
                        }
                    }
                }
            }
        }
        
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