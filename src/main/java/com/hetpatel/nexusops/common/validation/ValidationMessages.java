package com.hetpatel.nexusops.common.validation;

public final class ValidationMessages {

    private ValidationMessages() {

    }

    public static final String REQUIRED =
            "This field is required.";

    public static final String INVALID_EMAIL =
            "Please provide a valid email address.";

    public static final String INVALID_LENGTH =
            "Field length is invalid.";

    public static final String MIN_LENGTH =
            "Field must meet the minimum length.";

    public static final String MAX_LENGTH =
            "Field exceeds the maximum allowed length.";

    public static final String POSITIVE =
            "Value must be greater than zero.";

    public static final String NON_NEGATIVE =
            "Value must not be negative.";

    public static final String INVALID_VALUE =
            "Invalid value.";

    public static final String INVALID_FORMAT =
            "Invalid format.";

    public static final String INVALID_DATE =
            "Please provide a valid date.";
}
