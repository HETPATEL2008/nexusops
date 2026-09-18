package com.hetpatel.nexusops.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.BUSINESS_ERROR;

    public BusinessException(String message) {
        super(message);
    }
}
