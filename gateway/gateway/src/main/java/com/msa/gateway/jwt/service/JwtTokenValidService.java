package com.msa.gateway.jwt.service;

import com.msa.gateway.jwt.base.JwtTokenValidBase;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class JwtTokenValidService extends JwtTokenValidBase {
    private final JwtRedisSerivceImpt jwtRedisSerivceImpt;
    public JwtTokenValidService(JwtRedisSerivceImpt jwtRedisSerivceImpt) {
        this.jwtRedisSerivceImpt = jwtRedisSerivceImpt;
    }

    // Authorization 헤더에서 토큰 추출
    @Override
    public Mono<String> resolveAccessToken(ServerHttpRequest request) {
        return super.resolveAccessToken(request);
    }

    // 토큰 유효성 검사
    @Override
    public Mono<Claims> validateTokenAndGetClaims(String token) {
        return super.validateTokenAndGetClaims(token);
    }

    // AccessToken, RefreshToken 블랙리스트 존재 여부
    public Mono<Boolean> existsBlacklist(String token) {
        return jwtRedisSerivceImpt.existsBlacklistToken(token) // 블랙리스트 존재 여부
                .flatMap(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        return Mono.error(new Exception("Token is blacklisted"));
                    }
                    return Mono.just(true);
                });
    }
    // RefreshToken 유효성 검사
    public Mono<Boolean> validateRefreshToken(String refreshToken) {
        return jwtRedisSerivceImpt.existsRefreshToken(refreshToken) // RefreshToken 존재 여부
                .flatMap(exists -> {
                    if (!Boolean.TRUE.equals(exists)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or invalid"));
                    }
                    try {
                        validateTokenAndGetClaims(refreshToken); // RefreshToken 유효성검사
                        return Mono.just(true);
                    } catch (Exception e) {
                        return Mono.error(new Exception("Invalid refresh token: " + e.getMessage()));
                    }
                });
    }
}
