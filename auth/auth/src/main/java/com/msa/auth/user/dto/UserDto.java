package com.msa.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msa.auth.user.redis.dto.RedisUserDto;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {
    private Integer seq;
    private String id;
    private String name;
    private String password;
    private Integer status;

    private Integer addressSeq;
    private Integer roleSeq;

    private LocalDateTime passwordUpdatedDate;
    private LocalDateTime registeredDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;

    @Transient
    @Builder.Default
    private List<String> roles = new ArrayList<String>();

    public interface UserData {
        String getSeq();
        String getName();
        String getAddressSeq();
    }

    public RedisUserDto convertRedisUserDto(String id) {
    	return RedisUserDto.builder()
                            .userSeq(this.getSeq())
                            .addressSeq(this.getAddressSeq())
                            .roleSeq(this.getRoleSeq())
                            .roles(this.getRoles())
                            .id(id)
                            .name(this.getName())
                            .Status(this.getStatus())
                            .passwordUpdatedDate(this.getPasswordUpdatedDate())
                            .registeredDate(this.getRegisteredDate())
                            .updatedDate(this.getUpdatedDate())
                            .deletedDate(this.getDeletedDate())
                            .build();
    }
}
