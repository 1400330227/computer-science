package com.computerscience.hdfsapi.utils;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * RSA加密解密工具类
 */
public class RSAUtils {
    
    private static final String RSA_ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 2048;
    
    /**
     * 生成RSA密钥对
     * @return 密钥对
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }
    
    /**
     * 获取公钥的Base64编码字符串
     * @param publicKey 公钥
     * @return Base64编码的公钥字符串
     */
    public static String getPublicKeyString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    /**
     * 获取私钥的Base64编码字符串
     * @param privateKey 私钥
     * @return Base64编码的私钥字符串
     */
    public static String getPrivateKeyString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
    
    /**
     * 从Base64字符串恢复公钥
     * @param publicKeyStr Base64编码的公钥字符串
     * @return 公钥对象
     */
    public static PublicKey getPublicKeyFromString(String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("恢复公钥失败", e);
        }
    }
    
    /**
     * 从Base64字符串恢复私钥
     * @param privateKeyStr Base64编码的私钥字符串
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKeyFromString(String privateKeyStr) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("恢复私钥失败", e);
        }
    }
    
    /**
     * 使用公钥加密数据
     * @param data 要加密的数据
     * @param publicKey 公钥
     * @return 加密后的数据（Base64编码）
     */
    public static String encrypt(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }
    
    /**
     * 使用私钥解密数据
     * @param encryptedData 加密的数据（Base64编码）
     * @param privateKey 私钥
     * @return 解密后的原始数据
     */
    public static String decrypt(String encryptedData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }
    
    /**
     * 获取PEM格式的公钥（用于前端）
     * @param publicKey 公钥
     * @return PEM格式的公钥字符串
     */
    public static String getPublicKeyPEM(PublicKey publicKey) {
        String publicKeyBase64 = getPublicKeyString(publicKey);
        return "-----BEGIN PUBLIC KEY-----\n" + 
               formatBase64String(publicKeyBase64, 64) + "\n" +
               "-----END PUBLIC KEY-----";
    }
    
    /**
     * 格式化Base64字符串（每64个字符换行）
     * @param base64String 原始Base64字符串
     * @param lineLength 每行字符数
     * @return 格式化后的字符串
     */
    private static String formatBase64String(String base64String, int lineLength) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < base64String.length(); i += lineLength) {
            formatted.append(base64String.substring(i, Math.min(i + lineLength, base64String.length())));
            if (i + lineLength < base64String.length()) {
                formatted.append('\n');
            }
        }
        return formatted.toString();
    }
} 