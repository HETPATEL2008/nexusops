package com.hetpatel.nexusops.identity.permission.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.identity.permission.dto.response.PermissionResponse;

import org.springframework.data.domain.Pageable;

public interface PermissionService {

    PermissionResponse getPermissionById(Long id);

    PageResponse<PermissionResponse> getAllPermissions(Pageable pageable);

    PermissionResponse getPermissionByName(String name);
}
