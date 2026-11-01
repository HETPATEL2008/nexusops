package com.hetpatel.nexusops.supplier.dto.request;

import com.hetpatel.nexusops.common.validation.ValidationMessages;

import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateOrganizationSupplierRequest {

    @Size(max = 5000, message = ValidationMessages.MAX_LENGTH)
    private String notes;
}
