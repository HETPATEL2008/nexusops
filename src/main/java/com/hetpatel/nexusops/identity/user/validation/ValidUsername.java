package com.hetpatel.nexusops.identity.user.validation;

import jakarta.validation.Constraint;

import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {

    String message() default "Username must contain only letters, numbers, '.', '_' or '-'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
