package com.hetpatel.nexusops.organization.mapper;

import com.hetpatel.nexusops.organization.dto.request.CreateOrganizationRequest;
import com.hetpatel.nexusops.organization.dto.request.UpdateOrganizationRequest;

import com.hetpatel.nexusops.organization.dto.response.OrganizationResponse;

import com.hetpatel.nexusops.organization.entity.Organization;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Organization toEntity(CreateOrganizationRequest createOrganizationRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(
            UpdateOrganizationRequest updateOrganizationRequest,
            @MappingTarget Organization organization
    );

    OrganizationResponse toResponse(Organization organization);
}
