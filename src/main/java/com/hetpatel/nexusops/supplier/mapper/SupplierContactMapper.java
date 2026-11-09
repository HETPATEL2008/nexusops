package com.hetpatel.nexusops.supplier.mapper;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierContactRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierContactRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierContactResponse;

import com.hetpatel.nexusops.supplier.entity.SupplierContact;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierContactMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    SupplierContact toEntity(CreateSupplierContactRequest supplierContactRequest);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(
            UpdateSupplierContactRequest updateSupplierContactRequest,
            @MappingTarget SupplierContact supplierContact
    );

    @Mapping(target = "supplierId", source = "supplier.id")
    SupplierContactResponse toResponse(SupplierContact supplierContact);
}
