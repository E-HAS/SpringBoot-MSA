package com.msa.gateway.jwt.service;

import com.msa.gateway.jwt.base.JwtTokenValidBase;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

    // AccessToken 토큰, 블랙리스트 검사
    public Mono<Boolean> validateAccessToken(String token) {
            return validateTokenAndGetClaims(token)
                .flatMap(claims -> jwtRedisSerivceImpt.existsBlacklistToken(token))
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is blacklisted"));
                    }

                    return Mono.just(true);
                });
    }
}
