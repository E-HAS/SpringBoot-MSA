package com.msa.gateway.jwt.base;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class JwtTokenValidBase {
    protected static final String HEADER_PREFIX = "Bearer ";
    protected static final String PERMISSIONS_KEY = "permissions";
    public static final String REFRESH_COOKIE_NAME = "refreshToken";

    @Value("${jwt.secretkey}")
    private String secret;

    @Value("${jwt.access-Token-expiration}")
    private long accessTokenexpirationTime;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationTime;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Authorization 헤더에서 토큰 추출
    public Mono<String> resolveAccessToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(bearerToken -> bearerToken.startsWith(HEADER_PREFIX))
                .map(bearerToken -> bearerToken.substring(HEADER_PREFIX.length()));
    }

    // 토큰 유효성 검사
    public Mono<Claims> validateTokenAndGetClaims(String token) {
        return Mono.fromCallable(() ->
                        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                )
            .onErrorMap(SignatureException.class, e -> {
                log.error("Invalid JWT signature: {}", e.getMessage());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid JWT signature",e);
            })
            .onErrorMap(ExpiredJwtException.class, e -> {
                log.error("JWT token is expired: {}", e.getMessage());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"JWT token is expired",e);
            })
            .onErrorMap(UnsupportedJwtException.class, e -> {
                log.error("JWT token is unsupported: {}", e.getMessage());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"JWT token is unsupported",e);
            })
            .onErrorMap(IllegalArgumentException.class, e -> {
                log.error("JWT claims string is empty: {}", e.getMessage());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"JWT claims string is empty",e);
            })
            .onErrorMap(JwtException.class, e -> {
                log.error("Invalid JWT token: {}", e.getMessage());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid JWT token",e);
            });
    }
}