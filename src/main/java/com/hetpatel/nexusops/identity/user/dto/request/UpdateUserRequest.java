package com.hetpatel.nexusops.identity.user.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import com.hetpatel.nexusops.identity.user.entity.UserStatus;

import com.hetpatel.nexusops.identity.user.validation.ValidUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateUserRequest {

    @ValidUsername
    @NotBlank(message = ValidationMessages.REQUIRED)
    private String username;

    @Email(message = ValidationMessages.INVALID_EMAIL)
    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String email;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String firstName;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String lastName;

    @NotNull(message = ValidationMessages.REQUIRED)
    private UserStatus status;
}
