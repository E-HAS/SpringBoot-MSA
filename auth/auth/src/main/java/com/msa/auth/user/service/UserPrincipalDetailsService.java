package com.msa.auth.user.service;


import com.msa.auth.user.entity.UserDetail;
import com.msa.auth.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service("UserUserDetailService")
@RequiredArgsConstructor
public class UserPrincipalDetailsService implements UserDetailsService {

    private final UserServiceImpt userServiceImpt;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userServiceImpt.findByUserId(username);

        UserDetail userDetail = new UserDetail(user);
        userDetail.setRoles(user.getRoles().stream()
                .map(v->v.getRole().getRoleName())
                .toList());

        return userDetail;
    }
}