package com.hetpatel.nexusops.identity.permission.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;
import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.identity.permission.dto.response.PermissionResponse;

import com.hetpatel.nexusops.identity.permission.service.PermissionService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable Long id) {

        PermissionResponse response = permissionService.getPermissionById(id);

        ApiResponse<PermissionResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Permission fetched successfully.",
                        response
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PermissionResponse>>> getAllPermissions(Pageable pageable) {

        PageResponse<PermissionResponse> response = permissionService.getAllPermissions(pageable);

        ApiResponse<PageResponse<PermissionResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Permissions fetched successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionByName(
            @PathVariable String name) {

        PermissionResponse response =
                permissionService.getPermissionByName(name);

        ApiResponse<PermissionResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Permission fetched successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
