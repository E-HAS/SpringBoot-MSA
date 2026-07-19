package com.msa.auth.user.api;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.user.dto.RoleDto;
import com.msa.auth.user.entity.RoleEntity;
import com.msa.auth.user.service.RoleServiceImpt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleServiceImpt roleServiceImpt;

    @GetMapping
    public ResponseEntity<ResponseDto> getRoles() {
        try {
            List<RoleEntity> roles = roleServiceImpt.findAll();  // 동기 메서드로 변경 필요
            if (!roles.isEmpty()) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(HttpStatus.OK.value())
                                .message(HttpStatus.OK.getReasonPhrase())
                                .data(Map.of("Role", roles))
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build()
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDto> addRole(@RequestBody RoleDto roleDto) {
        try {
            Boolean added = roleServiceImpt.add(roleDto);
            if (added) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                        ResponseDto.builder()
                                .status(HttpStatus.CREATED.value())
                                .message(HttpStatus.CREATED.getReasonPhrase())
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build()
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDto> modifyRole(@RequestBody RoleDto roleDto) {
        try {
            Boolean modified = roleServiceImpt.UpdateBySeq(roleDto);
            if (modified) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(HttpStatus.OK.value())
                                .message(HttpStatus.OK.getReasonPhrase())
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build()
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping("/{seq}")
    public ResponseEntity<ResponseDto> deleteRole(@PathVariable("seq") Integer seq) {
        try {
            boolean deleted = roleServiceImpt.delete(seq);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        ResponseDto.builder()
                                .status(HttpStatus.NO_CONTENT.value())
                                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build()
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}
