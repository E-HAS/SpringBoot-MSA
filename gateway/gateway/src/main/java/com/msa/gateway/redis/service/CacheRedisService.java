package com.msa.gateway.redis.service;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class CacheRedisService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public CacheRedisService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 단일 키-값 저장 + TTL 설정
    public Mono<Boolean> save(String prefix, String key, String value, Duration ttl) {
        return redisTemplate.opsForValue().set(prefix + key, value, ttl);
    }

    // 여러 키-값 저장 + TTL 설정
    public Mono<Boolean> saves(String prefix, Map<String, String> values, Duration ttl) {
        return Flux.fromIterable(values.entrySet())
                .flatMap(entry -> redisTemplate.opsForValue().set(prefix + entry.getKey(), entry.getValue(), ttl))
                .then(Mono.just(true))
                .onErrorReturn(false);
    }

    // 단일 키 조회
    public Mono<String> get(String prefix, String key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    // 단일 키 삭제
    public Mono<Boolean> delete(String prefix, String key) {
        return redisTemplate.delete(prefix + key).map(count -> count > 0);
    }

    // 키 존재 여부 확인
    public Mono<Boolean> exists(String prefix, String key) {
        return redisTemplate.hasKey(prefix + key);
    }
}