package com.hetpatel.nexusops.supplier.dto.response;

import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import lombok.Data;

import java.time.Instant;

@Data
public class SupplierContactResponse {

    private Long id;
    private Long supplierId;
    private String firstName;
    private String lastName;
    private String designation;
    private String department;
    private String email;
    private String phoneCountryCode;
    private String phoneNumber;
    private String alternatePhoneCountryCode;
    private String alternatePhoneNumber;
    private SupplierContactType contactType;
    private Boolean isPrimary;
    private SupplierContactStatus status;
    private String notes;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long lastUpdatedBy;
}
