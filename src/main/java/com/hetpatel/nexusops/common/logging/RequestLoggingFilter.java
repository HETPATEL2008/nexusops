package com.hetpatel.nexusops.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;

import org.slf4j.MDC;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);

        } finally {

            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            String message = String.format(
                    "HTTP %s %s - Status: %d - Duration: %d ms - correlationId: %s",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration,
                    MDC.get("correlationId")
            );

            if (status >= 500) {
                log.error(message);
            } else if (status >= 400) {
                log.warn(message);
            } else {
                log.info(message);
            }
        }
    }
}
