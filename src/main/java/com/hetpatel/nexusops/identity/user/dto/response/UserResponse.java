package com.hetpatel.nexusops.identity.user.dto.response;

import com.hetpatel.nexusops.identity.user.entity.UserStatus;

import lombok.Data;

import java.time.Instant;

@Data
public class UserResponse {

    private Long id;
    private Long organizationId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
