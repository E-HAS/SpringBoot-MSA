package com.msa.auth.common.exception.handler;

import com.msa.auth.common.dto.ErrorResponseDto;
import com.msa.auth.common.exception.error.BusinessException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception e) {
        String errorDetail = extractErrorDetail(e);
        log.error(errorDetail, e);

        ErrorResponseDto response = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleAllIllegalArgumentException(Exception e) {
        String errorDetail = extractErrorDetail(e);
        log.error("[handleAllIllegalArgumentException]", e);
        log.error(errorDetail, e);

        ErrorResponseDto response = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        String errorDetail = extractErrorDetail(e);
        log.error("[handleBusinessException]", e);
        log.error(errorDetail, e);

        return ResponseEntity.status(e.getErrorCode().getStatus()).body(e.getErrorCode().getMessage());
    }
    
    private String extractErrorDetail(Exception e) {
        if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
            StackTraceElement element = e.getStackTrace()[0];
            return String.format("[Exception] Location : %s.%s(%s:%d) Error : %s Message : %s",
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber(),
                    e.getClass().getName(),
                    e.getMessage()
            );
        }
        return "Unknown Location";
    }
}
