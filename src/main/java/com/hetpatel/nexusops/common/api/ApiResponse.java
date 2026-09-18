package com.hetpatel.nexusops.common.api;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final ApiStatus status;
    private final String message;
    private final Instant timestamp;
    private final T data;

    public ApiResponse(boolean success, ApiStatus status, String message, T data) {
        this.timestamp = Instant.now();
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
