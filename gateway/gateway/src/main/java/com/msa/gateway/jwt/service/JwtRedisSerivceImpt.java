package com.msa.gateway.jwt.service;

import com.msa.gateway.redis.service.CacheRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRedisSerivceImpt {
    private final CacheRedisService cacheRedisService;

    private final String prefixRefreshToken = "refreshToken:";
    private final Integer durationOfDay = 7;

    private final String prefixBlacklistToken = "blacklistToken:";
    private final Integer durationOfMin = 60;

    // refreshToken 존재 확인
    public Mono<Boolean> existsRefreshToken(String token) {
        return cacheRedisService.exists(prefixRefreshToken,token)
                .defaultIfEmpty(false); // Redis에 값이 없으면 false 반환
    }

    // blacklist 존재 확인
    public Mono<Boolean> existsBlacklistToken(String token) {
        return cacheRedisService.exists(prefixBlacklistToken,token)
                .defaultIfEmpty(false); // Redis에 값이 없으면 false 반환
    }
}