package com.msa.auth.role.api;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.role.dto.RoleDto;
import com.msa.auth.role.entity.RoleEntity;
import com.msa.auth.role.service.RoleServiceImpt;

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
        List<RoleEntity> roles = roleServiceImpt.findAll();  // 동기 메서드로 변경 필요

        return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(HttpStatus.OK.value())
                                .message(HttpStatus.OK.getReasonPhrase())
                                .data(Map.of("Role", roles))
                                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseDto> addRole(@RequestBody RoleDto roleDto) {
        roleServiceImpt.add(roleDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                        ResponseDto.builder()
                                .status(HttpStatus.CREATED.value())
                                .message(HttpStatus.CREATED.getReasonPhrase())
                                .build());
    }

    @PutMapping
    public ResponseEntity<ResponseDto> modifyRole(@RequestBody RoleDto roleDto) {
        roleServiceImpt.UpdateBySeq(roleDto);

        return ResponseEntity.ok(
                ResponseDto.builder()
                        .status(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build()
        );
    }

    @DeleteMapping("/{seq}")
    public ResponseEntity<ResponseDto> deleteRole(@PathVariable("seq") Integer seq) {
        roleServiceImpt.delete(seq);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        ResponseDto.builder()
                                .status(HttpStatus.NO_CONTENT.value())
                                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                                .build()
        );
    }
}
