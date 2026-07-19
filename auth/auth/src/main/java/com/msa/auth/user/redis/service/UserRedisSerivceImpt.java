package com.msa.auth.user.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.auth.common.redis.service.CacheRedisService;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.redis.dto.RedisUserDto;
import com.msa.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRedisSerivceImpt {
	private final UserRepository userRepository;
	private final CacheRedisService cacheRedisService;
    private final String prefixUser= "user:";
    private final Integer durationOfMin= 60;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 저장
    public Boolean save(String userId, RedisUserDto value) {
        return cacheRedisService.save(prefixUser,userId, this.toJson(value) 
        												,Duration.ofMinutes(durationOfMin));
    }

    // 조회
    public RedisUserDto get(String userId) {
    	String jsonUser = cacheRedisService.get(prefixUser, userId);
    	return this.fromJson(jsonUser);
    }

    // 삭제
    public Boolean delete(String userId) {
        return cacheRedisService.delete(prefixUser, userId);
    }

    // 존재 여부 확인
    public Boolean exists(String userId) {
        return cacheRedisService.exists(prefixUser, userId);
    }
    
    public RedisUserDto findByUserId(String id) {
    	Boolean exists =  this.exists(id); // 또는 this.exists(prefixUser + id)
    	
        if (Boolean.FALSE.equals(exists)) {
        	UserDto userDto = userRepository.getUserById(id);
        	RedisUserDto redisUserDto = RedisUserDto.builder()
								                    .userSeq(userDto.getSeq())
								                    .addressSeq(userDto.getAddressSeq())
								                    .name(userDto.getName())
								                    .Status(userDto.getStatus())
								                    .build();
        	
        	Boolean result = this.save(id, redisUserDto);
            if (result) {
                return redisUserDto;
            } else {
                throw new RuntimeException("Failed to save to Redis");
            }
        }
        
        return this.get(id);
       
    }
    
    public RedisUserDto fromJson(String json) {
        try {
            return objectMapper.readValue(json, RedisUserDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed JSON to Object", e);
        }
    }
    
    public String toJson(RedisUserDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed Object to JSON", e);
        }
    }
}
