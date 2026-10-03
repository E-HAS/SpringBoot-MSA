package com.msa.auth.role.service;

import com.msa.auth.common.exception.code.BusinessExceptionErrorCode;
import com.msa.auth.common.exception.error.BusinessException;
import com.msa.auth.role.dto.RoleDto;
import com.msa.auth.role.entity.RoleEntity;
import com.msa.auth.role.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpt {
    private final RoleRepository RoleRepository;

    @Transactional(rollbackFor = { Exception.class })
    public void  add(RoleDto roleDto){
        try {
            RoleRepository.save(RoleEntity.builder()
                    .roleName(roleDto.getRoleName())
                    .roleDept(roleDto.getRoleDept())
                    .build());
        }catch(Exception e) {
            throw new BusinessException(BusinessExceptionErrorCode.INTERNAL_SERVER_ERROR,"Role Add Failed");
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public void UpdateBySeq(RoleDto roleDto){
        try {
            RoleRepository.updateByseq(roleDto.getSeq()
                    ,roleDto.getRoleName()
                    ,roleDto.getRoleDept());
        }catch(Exception e) {
            throw new BusinessException(BusinessExceptionErrorCode.INTERNAL_SERVER_ERROR,"Role Update Failed");
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public void delete(Integer seq){
        try {
            RoleRepository.deleteById(seq);
        }catch(Exception e) {
            throw new BusinessException(BusinessExceptionErrorCode.INTERNAL_SERVER_ERROR,"Role Delete Failed");
        }
    }

    public RoleEntity findById(Integer seq){
        return RoleRepository.findById(seq).orElse(null);
    }

    public List<RoleEntity> findAll(){
        List<RoleEntity> lists = RoleRepository.findAll();
        if(lists.isEmpty()){
            throw new BusinessException(BusinessExceptionErrorCode.NOT_FOUND,"Role Not Found");
        }
        return lists;
    }

}