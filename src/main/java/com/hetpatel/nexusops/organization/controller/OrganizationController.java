package com.hetpatel.nexusops.organization.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;
import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.organization.dto.request.CreateOrganizationRequest;
import com.hetpatel.nexusops.organization.dto.request.UpdateOrganizationRequest;

import com.hetpatel.nexusops.organization.dto.response.OrganizationResponse;

import com.hetpatel.nexusops.organization.service.OrganizationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {

        OrganizationResponse response = organizationService.createOrganization(request);

        ApiResponse<OrganizationResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "Organization created successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganizationById(@PathVariable Long id) {

        OrganizationResponse response = organizationService.getOrganizationById(id);

        ApiResponse<OrganizationResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrganizationResponse>>>
        getAllOrganizations(Pageable pageable) {

        PageResponse<OrganizationResponse> response = organizationService.getAllOrganizations(pageable);

        ApiResponse<PageResponse<OrganizationResponse>>
                apiResponse = new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "All organizations retrieved successfully.",
                        response
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrganizationRequest request) {

        OrganizationResponse response = organizationService.updateOrganization(id, request);

        ApiResponse<OrganizationResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization updated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable Long id) {

        organizationService.deleteOrganization(id);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization deleted successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<OrganizationResponse>> restoreOrganization(@PathVariable Long id) {

        OrganizationResponse response =
                organizationService.restoreOrganization(id);

        ApiResponse<OrganizationResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization restored successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
