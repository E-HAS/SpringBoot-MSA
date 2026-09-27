package com.msa.auth.user.jwt.service;

import com.msa.auth.common.jwt.dto.JwtToken;
import com.msa.auth.user.jwt.provider.UserJwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class UserJwtTokenService{
    private final UserJwtTokenProvider userJwtTokenProvider;

    public UserJwtTokenService(UserJwtTokenProvider userJwtTokenProvider) {
    	this.userJwtTokenProvider = userJwtTokenProvider;
    }

    public String resolveAccessToken(HttpServletRequest request){
        return this.userJwtTokenProvider.resolveAccessToken(request);
    }

	// AccessToken, RefreshToken 생성
    public JwtToken createJwtToken(Authentication authentication) {
    	JwtToken accessToken = userJwtTokenProvider.createAccessToken(authentication);
    	JwtToken refreshToken = userJwtTokenProvider.createRefreshToken(authentication);
    	
    	Boolean created = userJwtTokenProvider.addRefreshToken( refreshToken.getRefreshToken());

    	if(!created) {
    		new RuntimeException("Failed to store tokens in Redis");
    	}
    	
    	return JwtToken.builder()
	    			.prefix(accessToken.getPrefix())
	    			.accessToken(accessToken.getAccessToken())
	    			.refreshToken(refreshToken.getRefreshToken())
	    			.build();
    }

    // RefreshToken 유효성 검사
    public void validdateRefreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception{
        userJwtTokenProvider.validdateRefreshToken(request, response);
    }
    // AccessToken 재생성
    public String recreateAccessToken(HttpServletRequest request, HttpServletResponse response){
        return userJwtTokenProvider.recreateAccessToken(request, response);
    }

    // token 블랙리스트 등록
    public Boolean addBlacklist(String token){
    	log.info("Request Redis <- BlackToken : "+token);
        Claims payload = userJwtTokenProvider.getPayloadToken(token);

        Date expiration = payload.getExpiration();
        long now = new Date().getTime();
        long remainExpiration = expiration.getTime() - now;
        
        if(remainExpiration>0) {
        	return userJwtTokenProvider.addBlacklistToken(token, remainExpiration);
        }
        return true;
    }
    
    // token 블랙리스트 존재여부
    public Boolean existsBlacklist(String token) throws Exception {
    	return userJwtTokenProvider.existsBlacklist(token);
    }

    public long getAccessTokenexpirationTime(){
        return userJwtTokenProvider.getAccessTokenexpirationTime();
    }
    public long getRefreshTokenexpirationTime() {
        return userJwtTokenProvider.getRefreshTokenexpirationTime();
    }
}
