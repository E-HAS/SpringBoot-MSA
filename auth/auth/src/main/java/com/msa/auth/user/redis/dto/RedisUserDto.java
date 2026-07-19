package com.msa.auth.user.redis.dto;

import com.msa.auth.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@RedisHash("user_cache")
public class RedisUserDto implements Serializable{
	private Integer userSeq;
	private Integer addressSeq;
	private String roleSeq;
	
	private String name;
	private Integer Status;
	
	@Builder.Default
	private List<String> roles = new ArrayList<String>();
	
    public UserDto convertUserDto(String id) {
    	return UserDto.builder()
    				  .seq(this.getUserSeq())
    				  .id(id)
    				  .addressSeq(this.getAddressSeq())
    				  .name(this.getName())
    				  .status(this.getStatus())
    				  .roles(this.getRoles())
    				  .build();
    }
}
