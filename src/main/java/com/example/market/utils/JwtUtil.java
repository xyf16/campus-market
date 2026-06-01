package com.example.market.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 生成token（简单格式：userId_timestamp）
     */
    public String generateToken(Long userId, String username) {
        return userId + "_" + System.currentTimeMillis();
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserId(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }
            String[] parts = token.split("_");
            if (parts.length >= 1) {
                return Long.parseLong(parts[0]);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证token
     */
    public boolean verify(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            // 简单验证：token格式为 userId_timestamp
            String[] parts = token.split("_");
            return parts.length >= 2;
        } catch (Exception e) {
            return false;
        }
    }
}