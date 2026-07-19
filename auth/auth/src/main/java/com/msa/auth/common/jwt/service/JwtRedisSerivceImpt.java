package com.msa.auth.common.jwt.service;

import com.msa.auth.common.redis.service.CacheRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRedisSerivceImpt {
	private final CacheRedisService cacheRedisService;
	
    private final String prefixRefreshToken= "refreshToken:";
    private final Integer durationOfDay= 7;
    
    private final String prefixBlacklistToken= "blacklistToken:";
    private final Integer durationOfMin= 60;

    // refreshToken 저장
	public Boolean addRefreshToken(String token){
		return cacheRedisService.save(prefixRefreshToken,token,"", Duration.ofDays(durationOfDay));
	}

    // refreshToken 삭제
	public Boolean deleteRefreshToken(String token){
		return cacheRedisService.delete(prefixRefreshToken,token);
	}

    // refreshToken 존재 여부 확인
	public Boolean existsRefreshToken(String token){ return cacheRedisService.exists(prefixRefreshToken, token);}

    // blacklist <- token 저장
    public Boolean addBlacklistToken( String token, long remain) {return cacheRedisService.save(prefixBlacklistToken,token,"",Duration.ofMillis(remain));}

    // blacklist <- token 존재 여부 확인
    public Boolean existsBlacklistToken(String token) {return cacheRedisService.exists(prefixBlacklistToken, token);}
}
