package com.computerscience.hdfsapi.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Session管理服务
 * 实现同一账号不能同时登录的功能
 */
@Service
public class SessionManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionManagementService.class);
    
    // 存储用户ID -> Session的映射
    private final Map<Integer, HttpSession> userSessionMap = new ConcurrentHashMap<>();
    
    // 存储SessionId -> 用户ID的反向映射
    private final Map<String, Integer> sessionUserMap = new ConcurrentHashMap<>();
    
    // 存储被踢出的SessionId -> 踢出原因的映射
    private final Map<String, String> kickedSessionMap = new ConcurrentHashMap<>();
    
    /**
     * 处理用户登录，实现单点登录控制
     * @param userId 用户ID
     * @param newSession 新的Session
     * @return 是否踢出了旧会话
     */
    public boolean handleUserLogin(Integer userId, HttpSession newSession) {
        boolean kickedOldSession = false;
        
        synchronized (this) {
            // 检查是否有旧会话
            HttpSession oldSession = userSessionMap.get(userId);
            
            if (oldSession != null && !oldSession.getId().equals(newSession.getId())) {
                try {
                    String oldSessionId = oldSession.getId();
                    
                    // 记录被踢出的会话信息（在销毁前记录）
                    kickedSessionMap.put(oldSessionId, "检测到您的账号在其他设备登录，为保障账号安全，当前登录已被强制下线");
                    
                    // 在旧会话中标记被踢出（如果session还有效的话）
                    try {
                        oldSession.setAttribute("kicked_out", true);
                        oldSession.setAttribute("kick_reason", "账号在其他地方登录");
                    } catch (Exception e) {
                        // session可能已经失效，忽略
                    }
                    
                    // 销毁旧会话
                    oldSession.invalidate();
                    kickedOldSession = true;
                    
                    // 清理旧会话映射
                    sessionUserMap.remove(oldSessionId);
                    
                    logger.info("用户{}的旧会话{}被踢出，新会话{}", userId, oldSessionId, newSession.getId());
                } catch (Exception e) {
                    logger.warn("踢出用户{}的旧会话时发生异常: {}", userId, e.getMessage());
                }
            }
            
            // 记录新的会话映射
            userSessionMap.put(userId, newSession);
            sessionUserMap.put(newSession.getId(), userId);
        }
        
        return kickedOldSession;
    }
    
    /**
     * 检查当前会话是否有效（未被踢出）
     * @param session 要检查的session
     * @return 会话是否有效
     */
    public boolean isSessionValid(HttpSession session) {
        if (session == null) {
            return false;
        }
        
        String sessionId = session.getId();
        
        // 首先检查是否在踢出记录中
        if (kickedSessionMap.containsKey(sessionId)) {
            return false;
        }
        
        // 检查是否被标记为踢出（兼容处理）
        try {
            Boolean kickedOut = (Boolean) session.getAttribute("kicked_out");
            if (Boolean.TRUE.equals(kickedOut)) {
                return false;
            }
        } catch (Exception e) {
            // session可能已失效，继续其他检查
        }
        
        // 检查映射关系是否一致
        Integer userId = sessionUserMap.get(sessionId);
        if (userId == null) {
            return false;
        }
        
        HttpSession currentSession = userSessionMap.get(userId);
        return session.equals(currentSession);
    }
    
    /**
     * 用户主动登出时清理会话映射
     * @param userId 用户ID
     * @param sessionId Session ID
     */
    public void handleUserLogout(Integer userId, String sessionId) {
        synchronized (this) {
            userSessionMap.remove(userId);
            sessionUserMap.remove(sessionId);
            // 清理踢出记录
            kickedSessionMap.remove(sessionId);
            logger.info("用户{}主动登出，清理会话{}", userId, sessionId);
        }
    }
    
    /**
     * 清理无效的会话映射（定时任务可调用）
     */
    public void cleanupInvalidSessions() {
        synchronized (this) {
            userSessionMap.entrySet().removeIf(entry -> {
                try {
                    HttpSession session = entry.getValue();
                    // 尝试访问session，如果已失效会抛出异常
                    session.getAttribute("test");
                    return false;
                } catch (Exception e) {
                    // Session已失效，需要清理
                    Integer userId = entry.getKey();
                    String sessionId = entry.getValue().getId();
                    sessionUserMap.remove(sessionId);
                    // 清理踢出记录
                    kickedSessionMap.remove(sessionId);
                    logger.debug("清理用户{}的失效会话{}", userId, sessionId);
                    return true;
                }
            });
        }
    }
    
    /**
     * 获取当前在线用户数量
     * @return 在线用户数
     */
    public int getOnlineUserCount() {
        return userSessionMap.size();
    }
    
    /**
     * 获取指定用户的当前会话ID
     * @param userId 用户ID
     * @return 会话ID，如果用户未登录返回null
     */
    public String getCurrentSessionId(Integer userId) {
        HttpSession session = userSessionMap.get(userId);
        return session != null ? session.getId() : null;
    }
    
    /**
     * 获取被踢出会话的原因
     * @param sessionId 会话ID
     * @return 踢出原因，如果没有被踢出返回null
     */
    public String getKickReason(String sessionId) {
        return kickedSessionMap.get(sessionId);
    }
    
    /**
     * 清理被踢出的会话记录（避免内存泄漏）
     * @param sessionId 会话ID
     */
    public void clearKickRecord(String sessionId) {
        kickedSessionMap.remove(sessionId);
    }
} 