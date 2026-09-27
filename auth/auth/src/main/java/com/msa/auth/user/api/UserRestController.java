package com.msa.auth.user.api;

import java.util.Map;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.service.UserServiceImpt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
//@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpt userServiceImpt;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserServiceImpt userServiceImpt,
                              PasswordEncoder passwordEncoder) {
        this.userServiceImpt = userServiceImpt;
        this.passwordEncoder = passwordEncoder;
    }

    //GET 유저 목록 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getUsers(@RequestParam(value="seq", required=false) Integer seq,
                                                @RequestParam(value="status", required=false) Integer status,
                                                @RequestParam(value="id", required=false) String id,
                                                @RequestParam(value="name", required=false) String name,
                                                @RequestParam(value="stDt", required=false) String stDt,
                                                @RequestParam(value="enDt", required=false) String enDt,
                                                @RequestParam(value="page", defaultValue = "0") Integer page,
                                                @RequestParam(value="size", defaultValue = "10") Integer size,
                                                @RequestParam(value="sort", defaultValue = "seq,desc") String[] sort) {
        try {
            Sort.Order direction = sort[1].equalsIgnoreCase("desc") ? Sort.Order.desc(sort[0]) : Sort.Order.asc(sort[0]);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction));

            Page<UserDto> lists = userServiceImpt.findBySpecAndPageable(status,seq, id, name, stDt, enDt, pageable);
            if (lists != null) {
                log.info("GET /users User Lists: "+lists);
                return ResponseEntity.ok(ResponseDto.builder()
                        .status(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .data(Map.of("user", lists))
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build());
            }
        } catch (Exception e) {
            log.error("[Fail] GET /users User Lists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    //POST 유저 등록
    @PostMapping
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserDto userDto) {
        try {
            log.info("[Request] POST /users User Add : "+userDto);
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            boolean result = userServiceImpt.add(userDto);

            if (result) {
                log.info("POST /users User Add : "+userDto);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.CREATED.value())
                                .message(HttpStatus.CREATED.getReasonPhrase())
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build());
            }
        } catch (Exception e) {
            log.error("[Fail] POST /users User Add Failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    //GET 유저 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto> getUser(@PathVariable("userId") String userId) {
        try {
            log.info("[Request] Get /User/"+userId+" User Get");
            UserDto user = userServiceImpt.findByUserId(userId).convertToUserDto();
            if (user != null) {
                log.info("Get User : "+user);
                return ResponseEntity.ok(ResponseDto.builder()
                        .status(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .data(Map.of("user", user))
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build());
            }
        } catch (Exception e) {
            log.error("[Fail] Get /User/"+userId+" User Get");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    //PUT 유저 수정
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable("userId") String userId,
                                                  @RequestBody UserDto userDto) {
        try {
            log.info("[Request] Put /users/"+userId+" User Update : "+userDto);

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            boolean updated = userServiceImpt.update(userDto);
            if (updated) {
                log.info("Put /users/"+userId+" User Update : "+userDto);
                return ResponseEntity.ok(ResponseDto.builder()
                        .status(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build());
            }
        } catch (Exception e) {
            log.info("[Fail] Put /users/"+userId+" User Update : "+userDto);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    //DELETE 유저 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable("userId") String userId) {
        try {
            log.info("[Request] Delete /users/"+userId+" User Delete");

            boolean deleted = userServiceImpt.delete(userId);
            if (deleted) {
                log.info("Delete /users/"+userId+" User Delete");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.NO_CONTENT.value())
                                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .build());
            }
        } catch (Exception e) {
            log.info("[Fail] Delete /users/"+userId+" User Delete");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build());
        }
    }
}
