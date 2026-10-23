package com.hetpatel.nexusops.supplier.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateSupplierRequest {

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 200, message = ValidationMessages.MAX_LENGTH)
    private String legalName;

    @Size(max = 200, message = ValidationMessages.MAX_LENGTH)
    private String displayName;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH)
    private String supplierCode;

    @Size(max = 50, message = ValidationMessages.MAX_LENGTH)
    private String taxId;

    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String registrationNumber;

    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String website;

    @Size(max = 200, message = ValidationMessages.MAX_LENGTH)
    private String addressLine1;

    @Size(max = 200, message = ValidationMessages.MAX_LENGTH)
    private String addressLine2;

    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String city;

    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String state;

    @Size(max = 20, message = ValidationMessages.MAX_LENGTH)
    private String postalCode;

    @Size(min = 2, max = 2, message = ValidationMessages.INVALID_LENGTH)
    private String countryCode;

    private String notes;
}
