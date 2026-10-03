package com.hetpatel.nexusops.identity.rolepermission.service;

import com.hetpatel.nexusops.identity.rolepermission.dto.request.AssignPermissionRequest;

import com.hetpatel.nexusops.identity.rolepermission.dto.response.RolePermissionResponse;

import java.util.List;

public interface RolePermissionService {

    RolePermissionResponse assignPermission(
            Long roleId,
            AssignPermissionRequest assignPermissionRequest
    );

    List<RolePermissionResponse> getRolePermissions(Long roleId);

    void removePermission(Long roleId, Long permissionId);
}
