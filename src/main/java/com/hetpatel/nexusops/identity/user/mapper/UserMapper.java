package com.hetpatel.nexusops.identity.user.mapper;

import com.hetpatel.nexusops.identity.user.dto.request.CreateUserRequest;
import com.hetpatel.nexusops.identity.user.dto.request.UpdateUserRequest;

import com.hetpatel.nexusops.identity.user.dto.response.UserResponse;

import com.hetpatel.nexusops.identity.user.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void updateEntity(
            UpdateUserRequest updateUserRequest,
            @MappingTarget User user
    );

    @Mapping(target = "organizationId", source = "organization.id")
    UserResponse toResponse(User user);
}
