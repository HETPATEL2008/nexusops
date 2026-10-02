package com.hetpatel.nexusops.identity.permission.mapper;

import com.hetpatel.nexusops.identity.permission.dto.response.PermissionResponse;

import com.hetpatel.nexusops.identity.permission.entity.Permission;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);
}
