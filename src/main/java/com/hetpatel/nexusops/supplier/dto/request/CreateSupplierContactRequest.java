package com.hetpatel.nexusops.supplier.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class CreateSupplierContactRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    @Positive(message = ValidationMessages.POSITIVE)
    private Long supplierId;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String firstName;

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String lastName;

    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String designation;

    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String department;

    @Email(message = ValidationMessages.INVALID_EMAIL)
    @Size(max = 255, message = ValidationMessages.MAX_LENGTH)
    private String email;

    @Size(max = 5, message = ValidationMessages.MAX_LENGTH)
    private String phoneCountryCode;

    @Size(max = 20, message = ValidationMessages.MAX_LENGTH)
    private String phoneNumber;

    @Size(max = 5, message = ValidationMessages.MAX_LENGTH)
    private String alternatePhoneCountryCode;

    @Size(max = 20, message = ValidationMessages.MAX_LENGTH)
    private String alternatePhoneNumber;

    @NotNull(message = ValidationMessages.REQUIRED)
    private SupplierContactType contactType;

    private Boolean isPrimary;

    private String notes;
}
