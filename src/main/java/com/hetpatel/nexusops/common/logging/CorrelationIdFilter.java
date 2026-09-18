package com.hetpatel.nexusops.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.security.SecureRandom;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_ATTRIBUTE = "correlationId";
    private static final String MDC_CORRELATION_ID = "correlationId";

    private static final String CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private static final int CORRELATION_ID_LENGTH = 8;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId = generateCorrelationId();

        try {
            MDC.put(MDC_CORRELATION_ID, correlationId);
            request.setAttribute(CORRELATION_ID_ATTRIBUTE, correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    private String generateCorrelationId() {

        StringBuilder correlationId = new StringBuilder(CORRELATION_ID_LENGTH);

        for (int i = 0; i < CORRELATION_ID_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            correlationId.append(CHARACTERS.charAt(index));
        }

        return correlationId.toString();
    }
}
