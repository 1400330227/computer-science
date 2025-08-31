package com.computerscience.hdfsapi.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * 自定义Double反序列化器，用于正确处理小数值
 */
public class DoubleDeserializer extends JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        if (node.isNull()) {
            return null;
        }
        
        if (node.isTextual()) {
            String text = node.asText();
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                System.err.println("无法解析Double值: " + text + ", 错误: " + e.getMessage());
                return null;
            }
        }
        
        if (node.isNumber()) {
            return node.asDouble();
        }
        
        // 如果是其他类型，尝试转换为字符串再解析
        try {
            String text = node.asText();
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.err.println("无法解析Double值: " + node.toString() + ", 错误: " + e.getMessage());
            return null;
        }
    }
} 