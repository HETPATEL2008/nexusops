package com.hetpatel.nexusops.identity.role.mapper;

import com.hetpatel.nexusops.identity.role.dto.request.CreateRoleRequest;
import com.hetpatel.nexusops.identity.role.dto.request.UpdateRoleRequest;

import com.hetpatel.nexusops.identity.role.dto.response.RoleResponse;

import com.hetpatel.nexusops.identity.role.entity.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "organization", ignore = true)
    Role toEntity(CreateRoleRequest createRoleRequest);

    @Mapping(target = "organization", ignore = true)
    void updateEntity(
            UpdateRoleRequest updateRoleRequest,
            @MappingTarget Role role
    );

    @Mapping(target = "organizationId", source = "organization.id")
    RoleResponse toResponse(Role role);
}
