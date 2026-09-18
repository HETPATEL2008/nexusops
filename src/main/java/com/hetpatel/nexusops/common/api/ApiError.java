package com.hetpatel.nexusops.common.api;

import com.hetpatel.nexusops.common.exception.ErrorCode;

import lombok.Getter;

import org.springframework.http.HttpStatus;

import java.time.Instant;

import java.util.Map;

@Getter
public class ApiError {

    private final ErrorCode code;
    private final String message;
    private final HttpStatus status;
    private final Instant timestamp;
    private final String correlationId;
    private final String path;                               // API endpoint where the error occurred
    private final Map<String, String> validationErrors;

    public ApiError(
            ErrorCode code,
            String message,
            HttpStatus status,
            String correlationId,
            String path,
            Map<String, String> validationErrors
    ) {
        this.timestamp = Instant.now();
        this.code = code;
        this.message = message;
        this.status = status;
        this.correlationId = correlationId;
        this.path = path;
        this.validationErrors = validationErrors;
    }
}
