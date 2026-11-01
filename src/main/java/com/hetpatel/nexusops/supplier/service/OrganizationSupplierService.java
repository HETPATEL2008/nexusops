package com.hetpatel.nexusops.supplier.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.supplier.dto.request.CreateOrganizationSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateOrganizationSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.OrganizationSupplierResponse;

import com.hetpatel.nexusops.supplier.entity.SupplierRiskLevel;

public interface OrganizationSupplierService {

    OrganizationSupplierResponse createAssociation(
            CreateOrganizationSupplierRequest request
    );

    OrganizationSupplierResponse getAssociationById(Long id);

    OrganizationSupplierResponse getAssociationByOrganizationAndSupplier(
            Long organizationId,
            Long supplierId
    );

    PageResponse<OrganizationSupplierResponse> getAssociationsByOrganization(
            Long organizationId,
            int page,
            int size
    );

    PageResponse<OrganizationSupplierResponse> getAssociationsBySupplier(
            Long supplierId,
            int page,
            int size
    );

    OrganizationSupplierResponse updateAssociation(
            Long id,
            UpdateOrganizationSupplierRequest request
    );

    OrganizationSupplierResponse activateAssociation(Long id);

    OrganizationSupplierResponse deactivateAssociation(Long id);

    OrganizationSupplierResponse suspendAssociation(Long id);

    OrganizationSupplierResponse blockAssociation(Long id);

    OrganizationSupplierResponse updateRiskLevel(
            Long id,
            SupplierRiskLevel riskLevel
    );

    void deleteAssociation(Long id);

    OrganizationSupplierResponse restoreAssociation(Long id);
}
