package com.hetpatel.nexusops.identity.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9](?:[a-zA-Z0-9._-]{1,98}[a-zA-Z0-9])?$";

    @Override
    public boolean isValid(
            String username,
            ConstraintValidatorContext context) {

        if (username == null || username.isBlank()) {
            return true;
        }

        return username.length() >= 3
                && username.length() <= 100
                && username.matches(USERNAME_PATTERN);
    }
}
