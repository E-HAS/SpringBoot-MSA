package com.msa.auth.user.repository;

import com.msa.auth.user.entity.UserRoleEntity;
import com.msa.auth.user.entity.UserRoleEntityKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleEntityKey>{
    @Query("""
    	    SELECT ur
    	    FROM UserRoleEntity ur
    	    LEFT JOIN FETCH ur.role
    	    WHERE ur.userSeq = :seq
    		""")
    List<UserRoleEntity> findByUserSeq(@Param("seq")Integer seq);

    @Transactional
    @Modifying
    @Query(value="""
			DELETE FROM user_role 
			WHERE user_seq = (SELECT seq 
								FROM user 
							   WHERE id = :userId)
			  AND role_seq = :roleSeq
			""", nativeQuery = true)
    int deleteByUserIdAndRoleSeq(String userId, Integer roleSeq);
}
