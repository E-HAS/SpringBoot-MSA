package com.msa.auth.user.repository;


import com.msa.auth.user.converter.UserStatus;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.entity.UserEntity;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {

    @EntityGraph(attributePaths = {
            "roles",         // UserRoleEntity
            "roles.role"     // RoleEntity
    })
    Page<UserEntity> findAll(@Nullable Specification<UserEntity> spec, Pageable pageable);

    @Query("""
    	    SELECT DISTINCT u
    	    FROM UserEntity u
    	    LEFT JOIN FETCH u.roles ur
    	    LEFT JOIN FETCH ur.role
    	    WHERE u.seq = :seq
    		""")
    UserEntity findByUserSeq(@Param("seq")Integer seq);

    @Query("""
    	    SELECT DISTINCT u
    	    FROM UserEntity u
    	    LEFT JOIN FETCH u.roles ur
    	    LEFT JOIN FETCH ur.role
    	    WHERE u.id = :id
    		""")
    UserEntity findByUserId(@Param("id")String id);

    @Transactional
    @Modifying
    @Query(value="""
	        UPDATE user 
	        SET 
	            name = :name,
	            password = :password,
	            status = :status,
	            address_seq = :addressSeq,
	            password_updated_date = :passwordUpdatedDate,
	            updated_date = :updatedDate,
	            deleted_date = :deletedDate
	        WHERE seq = :seq
		    """, nativeQuery = true)
    int updateBySeq(
            @Param("seq") Integer seq,
            @Param("name") String name,
            @Param("password") String password,
            @Param("status") UserStatus status,
            @Param("addressSeq") Integer addressSeq,
            @Param("passwordUpdatedDate") LocalDateTime passwordUpdatedDate,
            @Param("updatedDate") LocalDateTime updatedDate,
            @Param("deletedDate") LocalDateTime deletedDate
    );

    @Query(value="""
			SELECT SEQ
				  ,NAME
				  ,STATUS
				  ,ADDRESS_SEQ
			 FROM USER 
			WHERE id = :id
			""", nativeQuery = true)
    UserDto getUserById(@Param("id")String id);
}