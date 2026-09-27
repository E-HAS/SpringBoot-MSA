package com.msa.auth.user.api;

import java.time.Duration;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.common.jwt.dto.JwtToken;
import com.msa.auth.common.jwt.service.JwtRedisSerivceImpt;
import com.msa.auth.user.dto.UserDto;
import com.msa.auth.user.jwt.service.UserJwtTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AuthRestController {
    private final UserJwtTokenService userJwtTokenService;
    private final JwtRedisSerivceImpt jwtRedisSerivceImpt;
    private final AuthenticationManager authenticationManager;

    public AuthRestController(UserJwtTokenService userJwtTokenService,
                              JwtRedisSerivceImpt jwtRedisSerivceImpt,
                              @Qualifier("UserAuthenticationProvider") DaoAuthenticationProvider authenticationProvider) {
        this.userJwtTokenService = userJwtTokenService;
        this.jwtRedisSerivceImpt = jwtRedisSerivceImpt;
        this.authenticationManager = new ProviderManager(authenticationProvider);
    }

    @PostMapping("/login/{userId}")
    public ResponseEntity<ResponseDto> loginUser(@PathVariable("userId") String userId,
                                                 @RequestBody UserDto userDto,
                                                 HttpServletResponse response) {
        try {
            log.info("[Request] User Login : "+userDto);

            // 1. 유저 Id, Password 조회
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
            cookie.setMaxAge((int) Duration.ofDays(userJwtTokenService.getRefreshTokenexpirationTime()).getSeconds());
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

    @DeleteMapping("/logout/{userId}")
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

            log.info("Delete Logout RefreshToken : "+refreshToken);
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
            log.info("Delete Logout AccessToken : "+accessToken);
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
    @PostMapping("/jwt/reissue")
    public ResponseEntity<ResponseDto> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            // RefreshToken 검증
            userJwtTokenService.validdateRefreshToken(request, response);
            // AccessToken 재생성
            userJwtTokenService.recreateAccessToken(request, response);

            return ResponseEntity.ok()
                    .body(ResponseDto.builder()
                            .status(HttpStatus.OK.value())
                            .message("Access token reissued successfully")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Authentication failed: " + e.getMessage())
                            .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Failed to reissue the access token")
                            .build());
        }
    }
}