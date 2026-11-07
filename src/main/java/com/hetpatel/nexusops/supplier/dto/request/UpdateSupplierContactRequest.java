package com.hetpatel.nexusops.supplier.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateSupplierContactRequest {

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH)
    private String firstName;

    @Pattern(
            regexp = ".*\\S.*",
            message = ValidationMessages.REQUIRED
    )
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

    private SupplierContactType contactType;

    private Boolean isPrimary;

    private String notes;
}
