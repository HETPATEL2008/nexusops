package com.hetpatel.nexusops.supplier.dto.response;

import com.hetpatel.nexusops.supplier.entity.SupplierRegistrationStatus;

import lombok.Data;

import java.time.Instant;

@Data
public class SupplierResponse {

    private Long id;
    private String legalName;
    private String displayName;
    private String supplierCode;
    private String taxId;
    private String registrationNumber;
    private String website;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private String notes;
    private SupplierRegistrationStatus registrationStatus;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long lastUpdatedBy;
}
