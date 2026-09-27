package com.msa.auth.user.service;

import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.dto.UserRoleDto;
import com.msa.auth.user.entity.UserRoleEntity;
import com.msa.auth.user.entity.UserRoleEntityKey;
import com.msa.auth.user.repository.UserRepository;
import com.msa.auth.user.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpt {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(rollbackFor = { Exception.class })
    public UserRoleEntity add(UserRoleDto userRoleDto){
        return userRoleRepository.save(UserRoleEntity.builder()
                .userSeq(userRoleDto.getUserSeq())
                .roleSeq(userRoleDto.getRoleSeq())
                .build());
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean delete(UserRoleDto userRoleDto){
        try {
            userRoleRepository.deleteById(UserRoleEntityKey.builder()
                    .userSeq(userRoleDto.getUserSeq())
                    .roleSeq(userRoleDto.getRoleSeq())
                    .build());
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean deleteRoleByUserIdAndRoleSeq(String userId, Integer roleSeq){
        try {
            userRoleRepository.deleteByUserIdAndRoleSeq(userId, roleSeq);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    public List<UserRoleDto> findByUserId(String userId){
        UserDto userDto = (UserDto) userRepository.getUserById(userId);

        return userRoleRepository.findByUserSeq(userDto.getSeq()).stream()
                .map(v-> UserRoleDto.builder()
                        .userSeq(v.getUserSeq())
                        .roleSeq(v.getRoleSeq())
                        .roleName(v.getRole().getRoleName())
                        .roleDept(v.getRole().getRoleDept())
                        .build())
                .toList();
    }

}