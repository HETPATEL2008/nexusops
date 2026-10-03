package com.hetpatel.nexusops.identity.userrole.service;

import com.hetpatel.nexusops.identity.userrole.dto.request.AssignRoleRequest;

import com.hetpatel.nexusops.identity.userrole.dto.response.UserRoleResponse;

import java.util.List;

public interface UserRoleService {

    UserRoleResponse assignRole(Long userId, AssignRoleRequest assignRoleRequest);

    List<UserRoleResponse> getUserRoles(Long userId);

    void removeRole(Long userId, Long roleId);
}
