package com.msa.auth.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name ="user_role")
@IdClass(UserRoleEntityKey.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(exclude = {"user", "role"})
public class UserRoleEntity {
    @Id
    private Integer userSeq;
    @Id
    private Integer roleSeq;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="userSeq", referencedColumnName="seq", insertable = false, updatable = false)//상대 객체 검색 컬럼 name=, 내 객체 검색 컬럼 referencedColumnName=
    private UserEntity user;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="roleSeq", referencedColumnName="seq", insertable = false, updatable = false)//상대 객체 검색 컬럼 name=, 내 객체 검색 컬럼 referencedColumnName=
    private RoleEntity role;

}