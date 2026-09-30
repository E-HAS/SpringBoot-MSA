package com.msa.auth.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessExceptionErrorCode {

    // 4xx Client Error

    BAD_REQUEST(
            HttpStatus.BAD_REQUEST,
            "Bad request."
    ),

    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "Authentication is required."
    ),

    FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "Access is forbidden."
    ),

    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "Resource not found."
    ),

    NOT_ACCEPTABLE(
            HttpStatus.NOT_ACCEPTABLE,
            "The requested resource cannot provide an acceptable response."
    ),

    REQUEST_TIMEOUT(
            HttpStatus.REQUEST_TIMEOUT,
            "The request timed out."
    ),

    CONFLICT(
            HttpStatus.CONFLICT,
            "The request conflicts with the current state of the resource."
    ),

    PAYLOAD_TOO_LARGE(
            HttpStatus.PAYLOAD_TOO_LARGE,
            "The request payload is too large."
    ),

    // 5xx Server Error

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred."
    ),

    NOT_IMPLEMENTED(
            HttpStatus.NOT_IMPLEMENTED,
            "The requested functionality is not implemented."
    ),

    BAD_GATEWAY(
            HttpStatus.BAD_GATEWAY,
            "The server received an invalid response from an upstream server."
    ),

    SERVICE_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "The service is temporarily unavailable."
    ),

    GATEWAY_TIMEOUT(
            HttpStatus.GATEWAY_TIMEOUT,
            "The upstream server did not respond in time."
    );

    private final HttpStatus status;
    private final String message;

    BusinessExceptionErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

/*
400 BAD_REQUEST	        잘못된 요청
401 UNAUTHORIZED	인증 필요
403 FORBIDDEN	        접근 금지
404 NOT_FOUND	        리소스 없음
406 NOT_ACCEPTABLE	응답 형식 협상 실패 (서버가 클라이언트가 요구한 응답 형식을 제공할 수 없음)
408 REQUEST_TIMEOUT	요청 시간 초과
409 CONFLICT	        상태 충돌 (이미 존재, 중복)
413 PAYLOAD_TOO_LARGE	        요청 데이터가 너무 큼

500 INTERNAL_SERVER_ERROR	서버 내부 오류
501 NOT_IMPLEMENTED	        구현되지 않음
502 BAD_GATEWAY	upstream        응답 오류
503 SERVICE_UNAVAILABLE	        서비스 이용 불가
504 GATEWAY_TIMEOUT	        upstream 응답 시간 초과
*/