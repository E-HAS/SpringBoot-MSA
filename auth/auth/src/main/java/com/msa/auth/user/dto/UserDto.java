package com.msa.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Transient
    @Builder.Default
    private List<String> roles = new ArrayList<String>();

    public interface UserData {
        String getSeq();
        String getName();
        String getAddressSeq();
    }
}
