package com.hetpatel.nexusops.identity.userrole.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;

import com.hetpatel.nexusops.common.api.ApiStatus;

import com.hetpatel.nexusops.identity.userrole.dto.request.AssignRoleRequest;

import com.hetpatel.nexusops.identity.userrole.dto.response.UserRoleResponse;

import com.hetpatel.nexusops.identity.userrole.service.UserRoleService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<UserRoleResponse>> assignRole(
            @PathVariable Long userId,
            @RequestBody @Valid AssignRoleRequest request) {

        UserRoleResponse response = userRoleService.assignRole(userId, request);

        ApiResponse<UserRoleResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "Role assigned successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<List<UserRoleResponse>>> getUserRoles(
            @PathVariable Long userId) {

        List<UserRoleResponse> responses = userRoleService.getUserRoles(userId);

        ApiResponse<List<UserRoleResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "User roles retrieved successfully.",
                        responses
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        userRoleService.removeRole(userId, roleId);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Role removed successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
