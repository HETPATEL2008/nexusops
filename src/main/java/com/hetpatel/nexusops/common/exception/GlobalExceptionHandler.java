package com.hetpatel.nexusops.common.exception;

import com.hetpatel.nexusops.common.api.ApiError;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        ApiError apiError = new ApiError(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                getCorrelationId(),
                getPath(request),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request) {

        ApiError apiError = new ApiError(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                getCorrelationId(),
                getPath(request),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(
            ConflictException exception,
            HttpServletRequest request) {

        ApiError apiError = new ApiError(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.CONFLICT,
                getCorrelationId(),
                getPath(request),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value",
                        (existing, replacement) -> existing
                ));

        ApiError apiError = new ApiError(
                ErrorCode.VALIDATION_FAILED,
                "Validation failed.",
                HttpStatus.BAD_REQUEST,
                getCorrelationId(),
                getPath(request),
                validationErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        log.error(
                "Unexpected error. CorrelationId: {}",
                getCorrelationId(),
                exception
        );

        ApiError apiError = new ApiError(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Something went wrong, please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                getCorrelationId(),
                getPath(request),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    private String getCorrelationId() {
        return MDC.get("correlationId");
    }

    private String getPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
