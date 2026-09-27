package com.msa.auth.user.jwt.provider;

import com.msa.auth.common.jwt.base.JwtTokenBase;
import com.msa.auth.common.jwt.dto.JwtToken;
import com.msa.auth.common.jwt.service.JwtRedisSerivceImpt;
import com.msa.auth.user.entity.UserDetail;
import com.msa.auth.user.service.UserPrincipalDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserJwtTokenProvider extends JwtTokenBase {
	private final UserDetailsService userDetailsService;
    private final JwtRedisSerivceImpt jwtRedisSerivceImpt;

    public UserJwtTokenProvider( UserPrincipalDetailsService userDetailsService
    							, JwtRedisSerivceImpt jwtRedisSerivceImpt) {
    	this.userDetailsService = userDetailsService;
        this.jwtRedisSerivceImpt = jwtRedisSerivceImpt;
    }
    // Authorization 헤더에서 토큰 추출
    @Override
    public String resolveAccessToken(HttpServletRequest request) {
    	return super.resolveAccessToken(request);
    }

    // 토큰 유효성 검사
    @Override
    public boolean validateToken(String token) throws Exception {
    	return super.validateToken(token);
    }

    // 토큰 데이터 추출
    @Override
    public Claims getPayloadToken(String token){
        return super.getPayloadToken(token);
    }

    // 토큰 생성
    @Override
    public String createTokenByPayload(Claims payload, Date now, Date expiry){
        return super.createTokenByPayload(payload, now, expiry);
    }

    // AccessToken 만료시간
    @Override
    public long getAccessTokenexpirationTime(){
        return super.getAccessTokenexpirationTime();
    }

    // RefreshToken 만료시간
    @Override
    public long getRefreshTokenexpirationTime() {
        return super.getRefreshTokenexpirationTime();
    }

    // AccessToken -> Authentication 생성 ( 인증된 사용자로 처리 )
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();

        /*
        Object authoritiesClaim = claims.get(PERMISSIONS_KEY);
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
        */

        UserDetails userDetail = userDetailsService.loadUserByUsername(claims.getSubject());
        Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();

        return new UsernamePasswordAuthenticationToken(userDetail, token, authorities);
    }

    // AccessToken 재생성
    public String extendAccessToken(String token){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.getAccessTokenexpirationTime());
        Claims payload = this.getPayloadToken(token);
        if(payload == null) {
            new Exception("Failed Extend AccessToken");
        }
        return this.createTokenByPayload(payload, now, expiryDate);
    }
    // Authentication -> AccessToken 생성
    public JwtToken createAccessToken(Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        String permissions = authentication.getAuthorities().stream()	//Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessTokenId = UUID.randomUUID().toString();

        Claims claims = Jwts.claims()
                .subject(userDetail.getId())
                .add(PERMISSIONS_KEY, permissions)
                .add(TOKEN_ID_KEY, accessTokenId)
                .add("name", userDetail.getName())
                .add("address", userDetail.getAddressSeq())
                .build();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenexpirationTime);

        String accessToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        return JwtToken.builder()
                .prefix(HEADER_PREFIX)
                .accessTokenId(accessTokenId)
                .accessToken(accessToken)
                .build();
    }
    // Authentication -> RefreshToken 생성
    public JwtToken createRefreshToken(Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        String refresTokenId = UUID.randomUUID().toString();

        Claims claims = Jwts.claims()
                .subject(userDetail.getId())
                .add(TOKEN_ID_KEY, refresTokenId)
                .add("name", userDetail.getName())
                .build();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationTime);

        String refreshToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        return JwtToken.builder()
                .prefix(HEADER_PREFIX)
                .refreshTokenId(refresTokenId)
                .refreshToken(refreshToken)
                .build();
    }

    // 쿠키에서 RefreshToken 추출
    public String extractRefreshToken(HttpServletRequest request) throws Exception{
        // 1. 쿠키에서 refreshToken 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new Exception("Refresh token not found in cookies.");
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new Exception("Refresh token not found in cookies.");
        }

        return refreshToken;
    }

    // RefreshToken 유효성검사
    public void validdateRefreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // RefreshToken 추출
        String refreshToken = this.extractRefreshToken( request);

        if(refreshToken == null || refreshToken.isEmpty()){
            throw new IllegalArgumentException("Refresh token not found in Cookies");
        }
        // 토큰 유효성 검사
        if (!this.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        // Redis에 존재하는지 확인
        if (!jwtRedisSerivceImpt.existsRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token not found");
        }
    }
    public String recreateAccessToken(HttpServletRequest request, HttpServletResponse response){
        // Header에서 AccessToken 추출
        String accessToken = this.resolveAccessToken(request);
        if(accessToken == null || accessToken.isEmpty()){
            throw new IllegalArgumentException("Refresh token not found in Authorization header");
        }
        // AccessToken 재생성
        String recreatedAccessToken = this.extendAccessToken(accessToken);

        // 응답 헤더에 액세스 토큰 추가
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + recreatedAccessToken);

        return recreatedAccessToken;
    }

    // redis <- refreshToken 저장
    public Boolean addRefreshToken(String token){
        return jwtRedisSerivceImpt.addRefreshToken(token, getRefreshTokenexpirationTime());
    }
    // token 블랙리스트 저장
    public Boolean addBlacklistToken(String token, long remain){
        return jwtRedisSerivceImpt.addBlacklistToken(token,remain);
    }
    // token 블랙리스트 존재여부
    public Boolean existsBlacklist(String token) throws Exception {
        try {
            return jwtRedisSerivceImpt.existsBlacklistToken(token);
        }catch (Exception e) {
            log.error("[existsBlacklist] Invalid JWT token");
            throw new Exception("Invalid JWT token");
        }
    }

}