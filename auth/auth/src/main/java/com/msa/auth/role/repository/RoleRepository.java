package com.msa.auth.role.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.msa.auth.role.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>{
    @Transactional
    @Modifying
    @Query(value="""
	        UPDATE role 
	        SET 
	            role_name = :roleName,
	            role_dept = :roleDept
	        WHERE seq = :seq
		    """, nativeQuery = true)
    int updateByseq(
            @Param("seq") Integer seq,
            @Param("roleName") String roleName,
            @Param("roleDept") String roleDept
    );
}