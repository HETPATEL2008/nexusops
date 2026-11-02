package com.hetpatel.nexusops.identity.user.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import com.hetpatel.nexusops.identity.user.entity.UserStatus;

import com.hetpatel.nexusops.identity.user.validation.ValidUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateUserRequest {

    @ValidUsername
    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    private String username;

    @Email(message = ValidationMessages.INVALID_EMAIL)
    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String email;

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String firstName;

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String lastName;

    private UserStatus status;
}
