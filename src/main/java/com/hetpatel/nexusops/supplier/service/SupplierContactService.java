package com.hetpatel.nexusops.supplier.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierContactRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierContactRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierContactResponse;

import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

public interface SupplierContactService {

    SupplierContactResponse createSupplierContact(
            CreateSupplierContactRequest request
    );

    SupplierContactResponse getSupplierContactById(Long id);

    PageResponse<SupplierContactResponse> getSupplierContactsBySupplier(
            Long supplierId,
            int page,
            int size
    );

    PageResponse<SupplierContactResponse> searchSupplierContacts(
            Long supplierId,
            SupplierContactType contactType,
            SupplierContactStatus status,
            Boolean isPrimary,
            String search,
            int page,
            int size
    );

    SupplierContactResponse updateSupplierContact(
            Long id,
            UpdateSupplierContactRequest request
    );

    void deleteSupplierContact(Long id);

    SupplierContactResponse restoreSupplierContact(Long id);

    SupplierContactResponse setPrimaryContact(Long id);

    SupplierContactResponse activateSupplierContact(Long id);

    SupplierContactResponse deactivateSupplierContact(Long id);
}
