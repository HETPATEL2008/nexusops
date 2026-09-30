package com.hetpatel.nexusops.identity.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 12;
    private static final int MAX_LENGTH = 128;

    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context) {

        if (password == null || password.isEmpty()) {
            return true;
        }

        if (password.length() < MIN_LENGTH
                || password.length() > MAX_LENGTH) {
            return false;
        }

        return !Character.isWhitespace(password.charAt(0))
                && !Character.isWhitespace(password.charAt(password.length() - 1));
    }
}
