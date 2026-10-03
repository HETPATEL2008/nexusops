package com.hetpatel.nexusops.identity.rolepermission.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class RolePermissionResponse {

    private Long id;
    private Long roleId;
    private Long permissionId;
    private Instant createdAt;
    private Long createdBy;
}
