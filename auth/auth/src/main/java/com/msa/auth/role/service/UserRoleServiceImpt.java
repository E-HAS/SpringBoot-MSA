package com.msa.auth.role.service;

import com.msa.auth.common.exception.code.BusinessExceptionErrorCode;
import com.msa.auth.common.exception.error.BusinessException;
import com.msa.auth.role.dto.UserRoleDto;
import com.msa.auth.role.entity.UserRoleEntity;
import com.msa.auth.role.entity.UserRoleEntityKey;
import com.msa.auth.role.repository.UserRoleRepository;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.repository.UserRepository;

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
        try{
        return userRoleRepository.save(UserRoleEntity.builder()
                                                        .userSeq(userRoleDto.getUserSeq())
                                                        .roleSeq(userRoleDto.getRoleSeq())
                                                        .build());
        }catch (Exception e){
            throw new BusinessException(BusinessExceptionErrorCode.INTERNAL_SERVER_ERROR,"User Role Add Failed");
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public void delete(UserRoleDto userRoleDto){
        try {
            userRoleRepository.deleteById(UserRoleEntityKey.builder()
                    .userSeq(userRoleDto.getUserSeq())
                    .roleSeq(userRoleDto.getRoleSeq())
                    .build());
        }catch(Exception e) {
            throw new BusinessException(BusinessExceptionErrorCode.INTERNAL_SERVER_ERROR,"User Role Delete Failed");
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public void deleteRoleByUserIdAndRoleSeq(String userId, Integer roleSeq){
        try {
            userRoleRepository.deleteByUserIdAndRoleSeq(userId, roleSeq);
        }catch(Exception e) {
            throw new BusinessException(BusinessExceptionErrorCode.INTERNAL_SERVER_ERROR,"User Role Delete Failed");
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