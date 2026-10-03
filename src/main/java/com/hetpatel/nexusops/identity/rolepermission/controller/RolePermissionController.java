package com.hetpatel.nexusops.identity.rolepermission.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;

import com.hetpatel.nexusops.identity.rolepermission.dto.request.AssignPermissionRequest;

import com.hetpatel.nexusops.identity.rolepermission.dto.response.RolePermissionResponse;

import com.hetpatel.nexusops.identity.rolepermission.service.RolePermissionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> assignPermission(
            @PathVariable Long roleId,
            @RequestBody @Valid AssignPermissionRequest request) {

        RolePermissionResponse response = rolePermissionService.assignPermission(roleId, request);

        ApiResponse<RolePermissionResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "Permission assigned successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getRolePermissions(
            @PathVariable Long roleId) {

        List<RolePermissionResponse> responses = rolePermissionService.getRolePermissions(roleId);

        ApiResponse<List<RolePermissionResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Role permissions retrieved successfully.",
                        responses
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> removePermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {

        rolePermissionService.removePermission(roleId, permissionId);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Permission removed successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
