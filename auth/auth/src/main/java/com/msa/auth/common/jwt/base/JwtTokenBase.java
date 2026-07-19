package com.msa.auth.common.jwt.base;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
public class JwtTokenBase {
    public final String HEADER_PREFIX = "Bearer ";
    protected final String PERMISSIONS_KEY = "permissions";
    protected final String TOKEN_ID_KEY = "tokenId";

    @Value("${jwt.secretkey}")
    protected String secret;

    @Value("${jwt.access-Token-expiration}")
    protected long accessTokenexpirationTime;

    @Value("${jwt.refresh-token-expiration}")
    protected long refreshTokenExpirationTime;

    protected SecretKey secretKey;

    @PostConstruct
    public void init() {
        //var secret = Base64.getEncoder().encodeToString(this.secret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Authorization 헤더에서 토큰 추출
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(HEADER_PREFIX.length());
        }
        return null;
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) throws Exception {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("[validateToken] Invalid JWT signature: {}", e.getMessage());
            throw new Exception("Invalid JWT signature: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("[validateToken] JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("[validateToken] JWT token is unsupported: {}", e.getMessage());
            throw new Exception("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("[validateToken] JWT claims string is empty: {}", e.getMessage());
            throw new Exception("JWT claims string is empty: " + e.getMessage());
        } catch (JwtException e) {
            log.error("[validateToken] Invalid JWT token: {}", e.getMessage());
            throw new Exception("Invalid JWT token: " + e.getMessage());
        }
    }

    // 토큰 데이터 추출
    public Claims getPayloadToken(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.error("[getPayloadToken] JWT token is expired: {}", e.getMessage());
            return e.getClaims();
        } catch (JwtException e) {
            log.error("[getPayloadToken ]Invalid JWT token: {}", e.getMessage());
        }
        return null;
    }

    // 토큰 생성
    public String createTokenByPayload(Claims payload, Date now, Date expiry) {
        return Jwts.builder()
                .claims(payload)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // AccessToken 만료시간
    public long getAccessTokenexpirationTime() {
        return accessTokenexpirationTime;
    }
    // RefreshToken 만료시간
    public long getRefreshTokenexpirationTime() {
        return refreshTokenExpirationTime;
    }

}
