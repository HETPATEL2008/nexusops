package com.hetpatel.nexusops.supplier.mapper;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierResponse;

import com.hetpatel.nexusops.supplier.entity.Supplier;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(CreateSupplierRequest createSupplierRequest);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            UpdateSupplierRequest updateSupplierRequest,
            @MappingTarget Supplier supplier);

    SupplierResponse toResponse(Supplier supplier);
}
