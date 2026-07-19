package com.msa.auth.user.service;

import com.msa.auth.user.dto.RoleDto;
import com.msa.auth.user.entity.RoleEntity;
import com.msa.auth.user.repository.RoleRepository;
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
    public Boolean add(RoleDto roleDto){
        try {
            RoleRepository.save(RoleEntity.builder()
                    .roleName(roleDto.getRoleName())
                    .roleDept(roleDto.getRoleDept())
                    .build());
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean UpdateBySeq(RoleDto roleDto){
        try {
            RoleRepository.updateByseq(roleDto.getSeq()
                    ,roleDto.getRoleName()
                    ,roleDto.getRoleDept());
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Transactional(rollbackFor = { Exception.class })
    public Boolean delete(Integer seq){
        try {
            RoleRepository.deleteById(seq);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    public RoleEntity findById(Integer seq){
        return RoleRepository.findById(seq).orElse(null);
    }

    public List<RoleEntity> findAll(){
        return RoleRepository.findAll();
    }

}