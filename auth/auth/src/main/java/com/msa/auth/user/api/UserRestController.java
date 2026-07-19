package com.msa.auth.user.api;

import java.time.Duration;
import java.util.Map;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.common.jwt.dto.JwtToken;
import com.msa.auth.common.jwt.service.JwtRedisSerivceImpt;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.jwt.service.UserJwtTokenService;
import com.msa.auth.user.service.UserServiceImpt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
//@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpt userServiceImpt;
    private final UserJwtTokenService userJwtTokenService;
    private final JwtRedisSerivceImpt jwtRedisSerivceImpt;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserRestController(UserServiceImpt userServiceImpt,
                              UserJwtTokenService userJwtTokenService,
                              JwtRedisSerivceImpt jwtRedisSerivceImpt,
                              PasswordEncoder passwordEncoder,
                              @Qualifier("UserAuthenticationProvider") DaoAuthenticationProvider authenticationProvider) {
        this.userServiceImpt = userServiceImpt;
        this.userJwtTokenService = userJwtTokenService;
        this.jwtRedisSerivceImpt = jwtRedisSerivceImpt;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = new ProviderManager(authenticationProvider);
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

    //DELETE 유저 로그아웃
    @DeleteMapping
    public ResponseEntity<ResponseDto> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1. Secure Cookie에서 RefreshToken 가져오기
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                throw new RuntimeException("No Cookies Found");
            }
            String refreshToken = null;
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }

            log.info("Delete /users User Logout RefreshToken : "+refreshToken);
            if (refreshToken == null) {
                throw new RuntimeException("Refresh Token Not Found In Cookies.");
            }

            //2. Redis <- RefreshToken 제거
            boolean deleted = jwtRedisSerivceImpt.deleteRefreshToken(refreshToken);
            if(!deleted){
                throw new RuntimeException("Refresh Token Not Found In Cookies.");
            }

            //3. Secure Cookie에서 RefreshToken 만료
            Cookie deleteCookie = new Cookie("refreshToken", "");
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(true);
            deleteCookie.setPath("/");
            deleteCookie.setMaxAge(0); // 즉시 만료
            // SameSite 설정은 Servlet API 4.0 이상 또는 별도 필터 필요 (생략 가능)
            response.addCookie(deleteCookie);

            //4. Header에서 Access Token 가져오기, Redis <- Access Token 블랙리스트 추가
            String accessToken = userJwtTokenService.resolveAccessToken(request);
            log.info("Delete /users User Logout AccessToken : "+accessToken);
            if(accessToken != null) {
                userJwtTokenService.addBlacklist(accessToken);
            }else {
                throw new RuntimeException("Access token not found");
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.NO_CONTENT.value())
                            .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                            .build());
        } catch (Exception e) {
            log.error("[Fail] Delete /users User Logout Failed");
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

    //POST 유저 로그인
    @PostMapping("/{userId}")
    public ResponseEntity<ResponseDto> loginUser(@PathVariable("userId") String userId,
                                                 @RequestBody UserDto userDto,
                                                 HttpServletResponse response) {
        try {
            log.info("[Request] POST /users/"+userId+" User Login : "+userDto);

            // 1. 유저 Id, Password 검사
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getId(), userDto.getPassword()));

            // 2. JWT 토큰 생성 (accessToken, refreshToken)
            JwtToken token = userJwtTokenService.createJwtToken(authentication);

            // 3. Authorization 헤더에 accessToken 추가
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());

            // 4. HttpOnly Secure 쿠키에 refreshToken 추가
            Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) Duration.ofDays(7).getSeconds());
            // SameSite 속성은 Servlet API가 지원하지 않으면 별도 설정 필요 (생략 가능)
            response.addCookie(cookie);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.CREATED.value())
                            .message(HttpStatus.CREATED.getReasonPhrase())
                            .build());

        } catch (Exception e) {
            log.info("[Fail] POST /users/"+userId+" User Login : "+userDto);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
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
