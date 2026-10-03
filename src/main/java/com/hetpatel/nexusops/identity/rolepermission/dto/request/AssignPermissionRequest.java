package com.hetpatel.nexusops.identity.rolepermission.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AssignPermissionRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long permissionId;
}
