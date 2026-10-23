package com.hetpatel.nexusops.supplier.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierResponse;

import org.springframework.data.domain.Pageable;

public interface SupplierService {

    SupplierResponse createSupplier(CreateSupplierRequest request);

    SupplierResponse getSupplierById(Long id);

    PageResponse<SupplierResponse> searchSuppliers(
            String search,
            Pageable pageable
    );

    SupplierResponse updateSupplier(
            Long id,
            UpdateSupplierRequest request
    );

    void deleteSupplier(Long id);

    SupplierResponse restoreSupplier(Long id);

    SupplierResponse verifySupplier(Long id);

    SupplierResponse rejectSupplier(Long id);
}
