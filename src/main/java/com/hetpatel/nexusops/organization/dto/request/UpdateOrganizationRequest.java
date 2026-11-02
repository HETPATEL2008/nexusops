package com.hetpatel.nexusops.organization.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import com.hetpatel.nexusops.organization.entity.OrganizationStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateOrganizationRequest {

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Size(max = 150, message = ValidationMessages.MAX_LENGTH)
    private String name;

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH)
    private String code;

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Email(message = ValidationMessages.INVALID_EMAIL)
    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String email;

    private OrganizationStatus status;
}
