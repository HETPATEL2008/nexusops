package com.hetpatel.nexusops.identity.user.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import com.hetpatel.nexusops.identity.user.validation.ValidPassword;
import com.hetpatel.nexusops.identity.user.validation.ValidUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateUserRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long organizationId;

    @ValidUsername
    @NotBlank(message = ValidationMessages.REQUIRED)
    private String username;

    @Email(message = ValidationMessages.INVALID_EMAIL)
    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String email;

    @ValidPassword
    @NotBlank(message = ValidationMessages.REQUIRED)
    private String password;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String firstName;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String lastName;
}
