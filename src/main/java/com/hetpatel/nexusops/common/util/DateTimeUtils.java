package com.hetpatel.nexusops.common.util;

import java.time.Instant;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static Instant now() {
        return Instant.now();
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static boolean isBefore(Instant first, Instant second) {
        return first.isBefore(second);
    }

    public static boolean isAfter(Instant first, Instant second) {
        return first.isAfter(second);
    }

    public static boolean isEqual(Instant first, Instant second) {
        return first.equals(second);
    }

    public static Instant plusDays(Instant instant, long days) {
        return instant.plus(days, ChronoUnit.DAYS);
    }

    public static Instant minusDays(Instant instant, long days) {
        return instant.minus(days, ChronoUnit.DAYS);
    }
}
