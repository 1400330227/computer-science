package com.computerscience.hdfsapi.service;

import com.computerscience.hdfsapi.utils.RSAUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 加密服务类
 * 负责RSA密钥管理和密码加密解密
 */
@Service
public class CryptoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    /**
     * 应用启动时初始化RSA密钥对
     */
    @PostConstruct
    public void init() {
        try {
            logger.info("开始生成RSA密钥对...");
            this.keyPair = RSAUtils.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            logger.info("RSA密钥对生成成功");
            
            // 打印公钥信息（仅用于调试）
            logger.debug("公钥: {}", RSAUtils.getPublicKeyString(publicKey));
        } catch (Exception e) {
            logger.error("RSA密钥对生成失败", e);
            throw new RuntimeException("初始化加密服务失败", e);
        }
    }
    
    /**
     * 获取PEM格式的公钥（供前端使用）
     * @return PEM格式的公钥字符串
     */
    public String getPublicKeyPEM() {
        if (publicKey == null) {
            throw new RuntimeException("RSA公钥未初始化");
        }
        return RSAUtils.getPublicKeyPEM(publicKey);
    }
    
    /**
     * 获取Base64格式的公钥
     * @return Base64编码的公钥字符串
     */
    public String getPublicKeyBase64() {
        if (publicKey == null) {
            throw new RuntimeException("RSA公钥未初始化");
        }
        return RSAUtils.getPublicKeyString(publicKey);
    }
    
    /**
     * 使用RSA私钥解密数据
     * @param encryptedData 加密的数据
     * @return 解密后的原始数据
     */
    public String decryptWithRSA(String encryptedData) {
        if (privateKey == null) {
            throw new RuntimeException("RSA私钥未初始化");
        }
        
        try {
            return RSAUtils.decrypt(encryptedData, privateKey);
        } catch (Exception e) {
            logger.error("RSA解密失败", e);
            throw new RuntimeException("密码解密失败", e);
        }
    }
    
    /**
     * 使用BCrypt加密密码
     * @param plainPassword 明文密码
     * @return BCrypt哈希后的密码
     */
    public String encryptPasswordWithBCrypt(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        try {
            return passwordEncoder.encode(plainPassword);
        } catch (Exception e) {
            logger.error("BCrypt密码加密失败", e);
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证BCrypt密码
     * @param plainPassword 明文密码
     * @param hashedPassword BCrypt哈希密码
     * @return 密码是否匹配
     */
    public boolean verifyPasswordWithBCrypt(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return passwordEncoder.matches(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.error("BCrypt密码验证失败", e);
            return false;
        }
    }
    
    /**
     * 解密RSA加密的密码，然后用BCrypt验证
     * @param encryptedPassword RSA加密的密码
     * @param storedHashedPassword 存储的BCrypt哈希密码
     * @return 密码是否匹配
     */
    public boolean verifyEncryptedPassword(String encryptedPassword, String storedHashedPassword) {
        try {
            // 1. 使用RSA私钥解密密码
            String plainPassword = decryptWithRSA(encryptedPassword);
            
            // 2. 使用BCrypt验证密码
            return verifyPasswordWithBCrypt(plainPassword, storedHashedPassword);
        } catch (Exception e) {
            logger.error("加密密码验证失败", e);
            return false;
        }
    }
    
    /**
     * 处理用户密码（解密RSA，然后用BCrypt加密存储）
     * @param encryptedPassword RSA加密的密码
     * @return BCrypt哈希后的密码，可存储到数据库
     */
    public String processPasswordForStorage(String encryptedPassword) {
        // 1. 使用RSA私钥解密密码
        String plainPassword = decryptWithRSA(encryptedPassword);
        
        // 2. 使用BCrypt加密密码
        return encryptPasswordWithBCrypt(plainPassword);
    }
} 