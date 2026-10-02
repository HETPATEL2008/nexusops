package com.hetpatel.nexusops.identity.role.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.identity.role.dto.request.CreateRoleRequest;
import com.hetpatel.nexusops.identity.role.dto.request.UpdateRoleRequest;

import com.hetpatel.nexusops.identity.role.dto.response.RoleResponse;

import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleResponse createRole(CreateRoleRequest request);

    RoleResponse getRoleById(Long id);

    PageResponse<RoleResponse> getAllRoles(Pageable pageable);

    RoleResponse updateRole(Long id, UpdateRoleRequest request);

    void deleteRole(Long id);

    RoleResponse restoreRole(Long id);
}
