package com.computerscience.hdfsapi.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信验证码服务
 * 负责验证码的生成、发送和验证
 */
@Service
public class SmsVerificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsVerificationService.class);
    
    // 验证码存储结构
    private static class VerificationCode {
        private String code;
        private LocalDateTime createdAt;
        private int attempts;  // 验证尝试次数
        
        public VerificationCode(String code) {
            this.code = code;
            this.createdAt = LocalDateTime.now();
            this.attempts = 0;
        }
        
        public String getCode() { return code; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getAttempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(createdAt.plusMinutes(5)); // 5分钟有效期
        }
        
        public boolean isExceededMaxAttempts() {
            return attempts >= 3; // 最多允许3次验证尝试
        }
    }
    
    // 内存存储验证码 (实际项目中建议使用Redis)
    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    
    // 发送频率限制 (每个手机号每分钟最多发送一次)
    private final Map<String, LocalDateTime> sendLimitMap = new ConcurrentHashMap<>();
    
    /**
     * 发送验证码到指定手机号
     * @param phone 手机号
     * @return 发送结果信息
     */
    public SendResult sendVerificationCode(String phone) {
        logger.info("开始为手机号 {} 发送验证码", phone);
        
        // 验证手机号格式
        if (!isValidPhoneNumber(phone)) {
            logger.warn("手机号格式不正确: {}", phone);
            return new SendResult(false, "手机号格式不正确");
        }
        
        // 检查发送频率限制
        if (isRateLimited(phone)) {
            logger.warn("手机号 {} 发送过于频繁", phone);
            return new SendResult(false, "发送过于频繁，请稍后再试");
        }
        
        // 生成6位数字验证码
        String code = generateVerificationCode();
        
        // 存储验证码
        verificationCodes.put(phone, new VerificationCode(code));
        
        // 更新发送时间
        sendLimitMap.put(phone, LocalDateTime.now());
        
        // 模拟发送短信 (实际项目中调用短信服务商API)
        boolean sent = simulateSendSms(phone, code);
        
        if (sent) {
            logger.info("验证码已发送到手机号: {}", phone);
            return new SendResult(true, "验证码已发送");
        } else {
            // 发送失败，清除存储的验证码
            verificationCodes.remove(phone);
            sendLimitMap.remove(phone);
            logger.error("验证码发送失败: {}", phone);
            return new SendResult(false, "验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 验证结果
     */
    public VerifyResult verifyCode(String phone, String code) {
        logger.info("开始验证手机号 {} 的验证码", phone);
        
        if (phone == null || code == null) {
            return new VerifyResult(false, "手机号和验证码不能为空");
        }
        
        VerificationCode storedCode = verificationCodes.get(phone);
        if (storedCode == null) {
            logger.warn("手机号 {} 没有对应的验证码", phone);
            return new VerifyResult(false, "请先获取验证码");
        }
        
        // 检查是否已过期
        if (storedCode.isExpired()) {
            verificationCodes.remove(phone);
            logger.warn("手机号 {} 的验证码已过期", phone);
            return new VerifyResult(false, "验证码已过期，请重新获取");
        }
        
        // 检查尝试次数
        if (storedCode.isExceededMaxAttempts()) {
            verificationCodes.remove(phone);
            logger.warn("手机号 {} 验证码尝试次数过多", phone);
            return new VerifyResult(false, "验证失败次数过多，请重新获取验证码");
        }
        
        // 增加尝试次数
        storedCode.incrementAttempts();
        
        // 验证码比对
        if (storedCode.getCode().equals(code)) {
            // 验证成功，清除验证码
            verificationCodes.remove(phone);
            logger.info("手机号 {} 验证码验证成功", phone);
            return new VerifyResult(true, "验证成功");
        } else {
            logger.warn("手机号 {} 验证码不正确，当前尝试次数: {}", phone, storedCode.getAttempts());
            return new VerifyResult(false, "验证码不正确");
        }
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999的随机数
        return String.valueOf(code);
    }
    
    /**
     * 验证手机号格式 (简单验证，实际项目中可以更严格)
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 检查发送频率限制
     */
    private boolean isRateLimited(String phone) {
        LocalDateTime lastSendTime = sendLimitMap.get(phone);
        if (lastSendTime == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(lastSendTime.plusMinutes(1));
    }
    
    /**
     * 模拟发送短信 (实际项目中替换为真实的短信服务商API调用)
     */
    private boolean simulateSendSms(String phone, String code) {
        // 在开发环境中，直接在控制台打印验证码用于测试
        logger.info("=== 模拟短信发送 ===");
        logger.info("手机号: {}", phone);
        logger.info("验证码: {}", code);
        logger.info("==================");
        
        // 模拟发送成功 (实际中根据短信服务商API返回结果判断)
        return true;
    }
    
    /**
     * 清理过期的验证码 (定期清理任务)
     */
    public void cleanExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().isExpired());
        
        // 清理过期的发送限制记录
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        sendLimitMap.entrySet().removeIf(entry -> entry.getValue().isBefore(oneHourAgo));
    }
    
    /**
     * 发送结果类
     */
    public static class SendResult {
        private boolean success;
        private String message;
        
        public SendResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    /**
     * 验证结果类
     */
    public static class VerifyResult {
        private boolean success;
        private String message;
        
        public VerifyResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
} 