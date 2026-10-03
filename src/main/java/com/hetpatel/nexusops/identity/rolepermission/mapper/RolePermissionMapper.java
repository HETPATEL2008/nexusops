package com.hetpatel.nexusops.identity.rolepermission.mapper;

import com.hetpatel.nexusops.identity.rolepermission.dto.response.RolePermissionResponse;

import com.hetpatel.nexusops.identity.rolepermission.entity.RolePermission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolePermissionMapper {

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "permissionId", source = "permission.id")
    RolePermissionResponse toResponse(RolePermission rolePermission);
}
