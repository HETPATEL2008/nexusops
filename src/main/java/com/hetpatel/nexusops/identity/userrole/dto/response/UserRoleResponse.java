package com.hetpatel.nexusops.identity.userrole.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class UserRoleResponse {

    private Long id;
    private Long userId;
    private Long roleId;
    private Instant createdAt;
    private Long createdBy;
}
