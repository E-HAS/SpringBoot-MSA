package com.msa.auth.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.msa.auth.user.converter.UserStatus;
import com.msa.auth.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name ="USER")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(exclude = "roles")
@JsonIgnoreProperties({ "password", "status", "passwordUpdatedDate", "registeredDate", "updatedDate", "deletedDate", "authorities" })
public class UserEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(nullable = false, length = 100)
    private String id;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Convert(converter = UserStatus.UserStatusConverter.class)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    private Integer addressSeq;

    @Column(name = "password_updated_date", columnDefinition = "DATETIME(3)")
    private LocalDateTime passwordUpdatedDate;

    @Column(name = "registered_date", columnDefinition = "DATETIME(3)")
    private LocalDateTime registeredDate;

    @Column(name = "updated_date", columnDefinition = "DATETIME(3)")
    private LocalDateTime updatedDate;

    @Column(name = "deleted_date", columnDefinition = "DATETIME(3)")
    private LocalDateTime deletedDate;

    @JsonIgnore
    @Builder.Default
    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user") // mappedBy= 프로퍼티 이름
    private List<UserRoleEntity> roles = new ArrayList<UserRoleEntity>();

    public UserDto convertToUserDto() {
        List<String> roleNames = this.roles.stream()
                .map(UserRoleEntity::getRole)
                .filter(Objects::nonNull)
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());

        return UserDto.builder()
                .seq(this.seq)
                .id(this.id)
                .name(this.name)
                .addressSeq(this.addressSeq)
                .roles(roleNames)
                .build();
    }
}