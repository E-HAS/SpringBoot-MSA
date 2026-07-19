package com.msa.auth.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.msa.auth.user.converter.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "password", "status", "passwordUpdatedDate", "registeredDate", "updatedDate", "deletedDate", "authorities" })
public class UserDetail implements UserDetails {
    private Integer seq;

    private String id;
    private String password;

    private Integer addressSeq;
    private String name;
    private UserStatus status;

    private LocalDateTime passwordUpdatedDate;
    private LocalDateTime registeredDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;

    @Builder.Default
    private List<String> roles = new ArrayList<String>();

    public UserDetail(UserEntity userEntity) {
        this.seq = userEntity.getSeq();

        this.id = userEntity.getId();
        this.password = userEntity.getPassword();

        this.addressSeq = userEntity.getAddressSeq();
        this.name = userEntity.getName();
        this.status = userEntity.getStatus();

        this.passwordUpdatedDate = userEntity.getPasswordUpdatedDate();
        this.registeredDate = userEntity.getRegisteredDate();
        this.updatedDate = userEntity.getUpdatedDate();
        this.deletedDate = userEntity.getDeletedDate();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  roles
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // 계정 만료
    @Override
    public boolean isAccountNonExpired() {
        if( this.status == UserStatus.ACCOUNT_EXPIRED){
            return false;
        }
        return true;
    }

    // 잠긴 계정
    @Override
    public boolean isAccountNonLocked() {
        if( this.status == UserStatus.ACCOUNT_LOCKED){
            return false;
        }
        return true;
    }

    // 패스워드 만료
    @Override
    public boolean isCredentialsNonExpired() {
        if( this.status == UserStatus.CREDENTIALS_EXPIRED){
            return false;
        }
        return true;
    }
    @Override
    public boolean isEnabled() {
        if(isCredentialsNonExpired() && isAccountNonExpired() && isAccountNonLocked()){
            return true;
        }
        return false;
    }

}