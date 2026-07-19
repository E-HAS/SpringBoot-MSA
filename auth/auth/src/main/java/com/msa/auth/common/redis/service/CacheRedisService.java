package com.msa.auth.common.redis.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class CacheRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public CacheRedisService(@Qualifier("cacheRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 단일 키-값 저장 + TTL 설정
    public boolean save(String prefix, String key, String value, Duration ttl) {
        String fullKey = prefix + key;
        try {
            redisTemplate.opsForValue().set(fullKey, value, ttl);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 여러 키-값 저장 + TTL 설정
    public boolean saves(String prefix, Map<String, String> values, Duration ttl) {
        boolean result = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String fullKey = prefix + entry.getKey();
            try {
                redisTemplate.opsForValue().set(fullKey, entry.getValue(), ttl);
            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }

    // 단일 키 조회
    public String get(String prefix, String key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    // 단일 키 삭제
    public boolean delete(String prefix, String key) {
        Boolean deleted = redisTemplate.delete(prefix + key);
        return deleted != null && deleted;
    }

    // 키 존재 여부 확인
    public boolean exists(String prefix, String key) {
        Boolean exists = redisTemplate.hasKey(prefix + key);
        return exists != null && exists;
    }
}
