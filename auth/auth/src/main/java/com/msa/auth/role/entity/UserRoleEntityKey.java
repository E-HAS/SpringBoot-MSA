package com.msa.auth.role.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRoleEntityKey implements Serializable {

    private Integer userSeq;
    private Integer roleSeq;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleEntityKey)) return false;
        UserRoleEntityKey userRoleEntity = (UserRoleEntityKey) o;
        return Objects.equals(this.userSeq, userRoleEntity.getUserSeq())
                && Objects.equals(this.roleSeq, userRoleEntity.getRoleSeq());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userSeq, this.roleSeq);
    }
}