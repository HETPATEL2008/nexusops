package com.hetpatel.nexusops.supplier.dto.response;

import com.hetpatel.nexusops.supplier.entity.OrganizationSupplierStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierRiskLevel;

import lombok.Data;

import java.time.Instant;

@Data
public class OrganizationSupplierResponse {

    private Long id;
    private Long organizationId;
    private Long supplierId;
    private OrganizationSupplierStatus status;
    private SupplierRiskLevel riskLevel;
    private String notes;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long lastUpdatedBy;
}
