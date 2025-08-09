package com.computerscience.hdfsapi.config;

import com.computerscience.hdfsapi.service.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class ScheduledConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledConfig.class);
    
    @Autowired
    private SmsVerificationService smsVerificationService;
    
    /**
     * 每5分钟清理一次过期的验证码
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void cleanExpiredVerificationCodes() {
        try {
            logger.debug("开始清理过期的验证码...");
            smsVerificationService.cleanExpiredCodes();
            logger.debug("过期验证码清理完成");
        } catch (Exception e) {
            logger.error("清理过期验证码时发生错误", e);
        }
    }
} 