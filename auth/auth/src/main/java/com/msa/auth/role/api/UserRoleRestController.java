package com.msa.auth.role.api;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.common.exception.code.BusinessExceptionErrorCode;
import com.msa.auth.common.exception.error.BusinessException;
import com.msa.auth.role.dto.UserRoleDto;
import com.msa.auth.role.entity.UserRoleEntity;
import com.msa.auth.role.service.UserRoleServiceImpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRoleRestController {
    private final UserRoleServiceImpt userRoleServiceImpt;

    @GetMapping("/{userId}/roles")
    public ResponseEntity<ResponseDto> getRoles(@PathVariable("userId") String userId) {
        List<UserRoleDto> roles = userRoleServiceImpt.findByUserId(userId);

        if(roles.isEmpty()){
            throw new BusinessException(BusinessExceptionErrorCode.NOT_FOUND,"No roles found for user.");
        }

        return ResponseEntity.ok(ResponseDto.builder()
                            .status(HttpStatus.OK.value())
                            .message(HttpStatus.OK.getReasonPhrase())
                            .data(Map.of("role", roles))
                            .build());
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<ResponseDto> addRole(@PathVariable("userId") String userId,
                                               @RequestBody UserRoleDto userDto) {
                    
        userRoleServiceImpt.add(userDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.CREATED.value())
                            .message(HttpStatus.CREATED.getReasonPhrase())
                            .build());
    }

    @DeleteMapping("/{userId}/roles/{roleSeq}")
    public ResponseEntity<ResponseDto> deleteRole(@PathVariable("userId") String userId,
                                                  @PathVariable("roleSeq") Integer roleSeq) {
        userRoleServiceImpt.deleteRoleByUserIdAndRoleSeq(userId, roleSeq);        
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseDto.builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                        .build());
    }
}
