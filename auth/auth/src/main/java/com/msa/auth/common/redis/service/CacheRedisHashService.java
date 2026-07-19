package com.msa.auth.common.redis.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class CacheRedisHashService {
    private final RedisTemplate<String, String> redisTemplate;

    public CacheRedisHashService(@Qualifier("cacheRedisTemplate")RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    // 필드-값 추가
    public Boolean addByKeyAndValue(String prefix, String mapKey, String field, String value, Duration ttl) {
    	try {
    		redisTemplate.opsForHash().put(prefix+mapKey, field, value);
    		return redisTemplate.expire(prefix+mapKey, ttl);
    	}catch(Exception e) {
    		return false;
    	}
    }
    // 필드-값 전체 추가
    public Boolean addAllByKeyAndMap(String prefix, String mapKey, Map<String, String> values, Duration ttl) {
    	try {
    		String fullKey = prefix + mapKey;
    		redisTemplate.opsForHash().putAll(fullKey, values);
    		return redisTemplate.expire(fullKey, ttl);
    	}catch(Exception e) {
    		return false;
    	}
    }
    
    // 특정 필드 조회
    public String findByKeyAndField(String prefix, String mapKey, String field) {
        return (String) redisTemplate.opsForHash().get(prefix+mapKey, field);
    }

    // 전체 가져오기 (Hash entries)
    public Map<String, String> findAllByKeyAndField(String prefix, String mapKey) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(prefix + mapKey);
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result.put(key, value);
        }

        return result;
    }

    // 특정 필드 삭제
    public Long deleteByKeyAndField(String prefix, String mapKey, String field) {
        return redisTemplate.opsForHash().delete(prefix + mapKey, field);
    }

    // 특정 key 전체 삭제
    public Boolean deleteByKey(String prefix, String key) {
        Boolean result = redisTemplate.delete(prefix + key);
        return result != null && result;
    }
    
    // 특정 Key 필드 존재 여부
    public Boolean existsByKeyAndField(String prefix, String mapKey, String field) {
        return redisTemplate.opsForHash().hasKey(prefix + mapKey, field);
    }
}