package com.hetpatel.nexusops.identity.userrole.mapper;

import com.hetpatel.nexusops.identity.userrole.dto.response.UserRoleResponse;

import com.hetpatel.nexusops.identity.userrole.entity.UserRole;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roleId", source = "role.id")
    UserRoleResponse toResponse(UserRole userRole);
}
