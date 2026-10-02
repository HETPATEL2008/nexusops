package com.hetpatel.nexusops.identity.role.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class RoleResponse {

    private Long id;
    private Long organizationId;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
