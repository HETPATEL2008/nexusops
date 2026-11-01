package com.hetpatel.nexusops.supplier.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateOrganizationSupplierRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    @Positive(message = ValidationMessages.POSITIVE)
    private Long organizationId;

    @NotNull(message = ValidationMessages.REQUIRED)
    @Positive(message = ValidationMessages.POSITIVE)
    private Long supplierId;

    @Size(max = 5000, message = ValidationMessages.MAX_LENGTH)
    private String notes;
}
