package com.hetpatel.nexusops.identity.role.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateRoleRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long organizationId;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String name;

    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String description;
}
