package com.hetpatel.nexusops.organization.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateOrganizationRequest {

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 150, message = ValidationMessages.MAX_LENGTH)
    private String name;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH)
    private String code;

    @Email(message = ValidationMessages.INVALID_EMAIL)
    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String email;
}
