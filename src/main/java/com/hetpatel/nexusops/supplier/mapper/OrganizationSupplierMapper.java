package com.hetpatel.nexusops.supplier.mapper;

import com.hetpatel.nexusops.supplier.dto.request.CreateOrganizationSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateOrganizationSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.OrganizationSupplierResponse;

import com.hetpatel.nexusops.supplier.entity.OrganizationSupplier;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationSupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "riskLevel", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    OrganizationSupplier toEntity(CreateOrganizationSupplierRequest organizationSupplierRequest);


    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "riskLevel", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    void updateEntity(
            UpdateOrganizationSupplierRequest updateOrganizationSupplierRequest,
            @MappingTarget OrganizationSupplier organizationSupplier
    );


    OrganizationSupplierResponse toResponse(OrganizationSupplier organizationSupplier);
}
