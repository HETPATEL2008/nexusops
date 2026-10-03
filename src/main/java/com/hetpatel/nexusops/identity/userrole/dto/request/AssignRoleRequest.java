package com.hetpatel.nexusops.identity.userrole.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class AssignRoleRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long roleId;
}
