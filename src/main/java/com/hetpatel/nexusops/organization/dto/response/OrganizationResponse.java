package com.hetpatel.nexusops.organization.dto.response;

import com.hetpatel.nexusops.organization.entity.OrganizationStatus;

import lombok.Data;

import java.time.Instant;

@Data
public class OrganizationResponse {

    private Long id;
    private String name;
    private String code;
    private String email;
    private OrganizationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
