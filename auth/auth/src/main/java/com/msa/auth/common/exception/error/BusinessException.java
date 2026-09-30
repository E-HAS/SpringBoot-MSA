package com.msa.auth.common.exception.error;

import com.msa.auth.common.exception.code.BusinessExceptionErrorCode;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessExceptionErrorCode errorCode;
    private final String message;

    public BusinessException(BusinessExceptionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
        // 직접 message 지정
    public BusinessException(BusinessExceptionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}