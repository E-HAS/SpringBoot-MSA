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
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class JwtTokenValidBase {
    public static final String HEADER_PREFIX = "Bearer ";
    protected static final String PERMISSIONS_KEY = "permissions";
    protected static final String TOKEN_ID_KEY = "tokenId";

    @Value("${jwt.secretkey}")
    protected String secret;

    @Value("${jwt.access-Token-expiration}")
    protected long accessTokenexpirationTime;

    @Value("${jwt.refresh-token-expiration}")
    protected long refreshTokenExpirationTime;

    protected SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getPayloadToken(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return e.getClaims();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return null;
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
                    return new Exception("Invalid JWT signature");
                })
                .onErrorMap(ExpiredJwtException.class, e -> {
                    log.error("JWT token is expired: {}", e.getMessage());
                    return e;
                })
                .onErrorMap(UnsupportedJwtException.class, e -> {
                    log.error("JWT token is unsupported: {}", e.getMessage());
                    return new Exception("JWT token is unsupported");
                })
                .onErrorMap(IllegalArgumentException.class, e -> {
                    log.error("JWT claims string is empty: {}", e.getMessage());
                    return new Exception("JWT claims string is empty");
                })
                .onErrorMap(JwtException.class, e -> {
                    log.error("Invalid JWT token: {}", e.getMessage());
                    return new Exception("Invalid JWT token");
                });
    }
    // 쿠키에서 RefreshToken 추출
    public Mono<String> extractRefreshToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getCookies().getFirst("refreshToken"))
                .switchIfEmpty(Mono.error(new Exception("Refresh token not found in cookies.")))
                .map(HttpCookie::getValue);
    }
}