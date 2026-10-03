package com.msa.auth.user.service;

import com.msa.auth.common.exception.code.BusinessExceptionErrorCode;
import com.msa.auth.common.exception.error.BusinessException;
import com.msa.auth.common.utill.validation;
import com.msa.auth.role.dto.UserRoleDto;
import com.msa.auth.role.entity.UserRoleEntity;
import com.msa.auth.role.service.UserRoleServiceImpt;
import com.msa.auth.user.converter.UserStatus;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.entity.UserEntity;
import com.msa.auth.user.redis.service.UserRedisSerivceImpt;
import com.msa.auth.user.repository.UserRepository;
import com.msa.auth.user.specification.UserSpecifications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpt {

    private final UserRepository userRepository;
    private final UserRoleServiceImpt userRoleServiceImpt;
    private final UserRedisSerivceImpt userRedisSerivceImpt;

    @Transactional(rollbackFor = { Exception.class })
    public Boolean add(UserDto user){
        try {
            UserEntity userEntity = userRepository.save(UserEntity.builder()
                                                                    .id(user.getId())
                                                                    .password(user.getPassword())
                                                                    .name(user.getName())
                                                                    .addressSeq(user.getAddressSeq())
                                                                    .status(UserStatus.ACTIVE)
                                                                    .registeredDate(LocalDateTime.now())
                                                                    .build());

            UserRoleEntity roleEntity = userRoleServiceImpt.add(UserRoleDto.builder()
                                                                            .userSeq(userEntity.getSeq())
                                                                            .roleSeq(user.getRoleSeq())
                                                                            .build());

            return true;
        }catch(Exception e) {
            log.error("[Fail] User Add Error : "+e.getMessage());
            throw new BusinessException(BusinessExceptionErrorCode.BAD_REQUEST,"User Add Failed");
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean update(UserDto userDto){
        try {
            UserEntity findUserEntity = this.findByUserId(userDto.getId());

            if(!validation.isStringNullOrEmpty(userDto.getName())) {
                findUserEntity.setName(userDto.getName());
            }
            if(!validation.isStringNullOrEmpty(userDto.getPassword())) {
                findUserEntity.setPassword(userDto.getPassword());
                findUserEntity.setPasswordUpdatedDate(LocalDateTime.now());
            }
            if(userDto.getStatus() != null) {
                findUserEntity.setStatus(UserStatus.getString(userDto.getStatus()));
            }
            if( userDto.getAddressSeq() != null ) {
                findUserEntity.setAddressSeq(userDto.getAddressSeq());
            }
            findUserEntity.setUpdatedDate(LocalDateTime.now());

            return this.updateBySeq(findUserEntity);
        }catch(Exception e) {
            log.error("[Fail] User Update Failed Error : "+e.getMessage());
            throw new BusinessException(BusinessExceptionErrorCode.BAD_REQUEST,"User Update Failed ");
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean updateBySeq(UserEntity entity){
        int dbResult = userRepository.updateBySeq(entity.getSeq()
                                                    ,entity.getName()
                                                    ,entity.getPassword()
                                                    ,entity.getStatus()
                                                    ,entity.getAddressSeq()
                                                    ,entity.getPasswordUpdatedDate()
                                                    ,entity.getUpdatedDate()
                                                    ,entity.getDeletedDate());
        
        if(dbResult == 0){
            log.error("[Fail] User Update Failed Error in DB ");
            throw new BusinessException(BusinessExceptionErrorCode.BAD_REQUEST,"User Update Failed ");
        }

        Boolean redisResult = userRedisSerivceImpt.save(entity.getId(), entity.convertRedisUserDto(entity.getId()));

        if(!redisResult){
            log.error("[Fail] User Update Failed Error in Redis ");
            throw new BusinessException(BusinessExceptionErrorCode.BAD_REQUEST,"User Update Failed ");
        }

        return true;
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean delete(String userId){
        try {
            UserEntity findUserEntity = this.findByUserId(userId);
            //UserRepository.delete(findUserEntity);

            findUserEntity.setStatus(UserStatus.INACTIVE);
            findUserEntity.setDeletedDate(LocalDateTime.now());
            return this.updateBySeq(findUserEntity);
        }catch(Exception e) {
            log.error("[Fail] User Delete Error : "+e.getMessage());
            throw new BusinessException(BusinessExceptionErrorCode.BAD_REQUEST,"User Delete Error ");
        }
    }

    public Page<UserDto> findBySpecAndPageable( Integer status,
                                                Integer seq,
                                                String id,
                                                String name,
                                                String stDt,
                                                String enDt,
                                                Pageable pageable){

        Specification<UserEntity> spec = UserSpecifications.findWith(status, seq, id, name, stDt, enDt);

         Page<UserDto> lists = userRepository.findAll(spec,pageable).map(UserEntity::convertToUserDto);                                            
        
        if (lists.isEmpty()) {
            throw new BusinessException(BusinessExceptionErrorCode.NOT_FOUND,"Users not found.");
        }

        return lists;
    }

    public UserEntity findByUserSeq(Integer seq){
        return userRepository.findByUserSeq(seq);
    }

    public UserEntity findByUserId(String id){
        UserEntity entity = userRepository.findByUserId(id);
        if(entity == null){
            throw new BusinessException(BusinessExceptionErrorCode.NOT_FOUND,"User not found.");
        }

        userRedisSerivceImpt.save(id, entity.convertRedisUserDto(id));
        
        return entity;
    }

    public UserDto findByUserIdInRedis(String id){
        return userRedisSerivceImpt.findByUserId(id).convertUserDto(id);
    }
}