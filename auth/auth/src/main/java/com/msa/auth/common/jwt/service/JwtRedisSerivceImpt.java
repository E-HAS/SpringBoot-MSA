package com.msa.auth.common.jwt.service;

import com.msa.auth.common.jwt.base.JwtTokenBase;
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

    // refreshToken 저장
	public Boolean addRefreshToken(String token, long durationOfDay){
		return cacheRedisService.save(JwtTokenBase.PREFIX_REFRESH_TOKEN,token,"", Duration.ofDays(durationOfDay));
	}

    // refreshToken 삭제
	public Boolean deleteRefreshToken(String token){
		return cacheRedisService.delete(JwtTokenBase.PREFIX_REFRESH_TOKEN,token);
	}

    // refreshToken 존재 여부 확인
	public Boolean existsRefreshToken(String token){ return cacheRedisService.exists(JwtTokenBase.PREFIX_REFRESH_TOKEN, token);}

    // blacklist <- token 저장
    public Boolean addBlacklistToken( String token, long remain) {return cacheRedisService.save(JwtTokenBase.PREFIX_BLACKLIST_TOKEN,token,"",Duration.ofMillis(remain));}

    // blacklist <- token 존재 여부 확인
    public Boolean existsBlacklistToken(String token) {return cacheRedisService.exists(JwtTokenBase.PREFIX_BLACKLIST_TOKEN, token);}
}
