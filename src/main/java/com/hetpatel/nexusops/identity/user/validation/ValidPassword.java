package com.hetpatel.nexusops.identity.user.validation;

import jakarta.validation.Constraint;

import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default
            "Password must be 12-128 characters and contain no leading or trailing whitespace";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
