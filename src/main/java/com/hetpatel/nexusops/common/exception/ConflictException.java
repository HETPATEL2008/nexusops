package com.hetpatel.nexusops.common.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.CONFLICT;

    public ConflictException(String message) {
        super(message);
    }
}
