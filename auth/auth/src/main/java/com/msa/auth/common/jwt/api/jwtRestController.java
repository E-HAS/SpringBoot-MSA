package com.msa.auth.common.jwt.api;

import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.user.jwt.service.UserJwtTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/jwt")
public class jwtRestController {
    private final UserJwtTokenService userJwtTokenService;

    public jwtRestController(UserJwtTokenService userJwtTokenService){
        this.userJwtTokenService = userJwtTokenService;
    }
    @PostMapping("/reissue")
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
