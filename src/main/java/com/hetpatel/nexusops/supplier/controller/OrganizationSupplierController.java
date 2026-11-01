package com.hetpatel.nexusops.supplier.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;
import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.supplier.dto.request.CreateOrganizationSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateOrganizationSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.OrganizationSupplierResponse;

import com.hetpatel.nexusops.supplier.entity.SupplierRiskLevel;

import com.hetpatel.nexusops.supplier.service.OrganizationSupplierService;

import jakarta.validation.Valid;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organization-suppliers")
@Validated
@RequiredArgsConstructor
public class OrganizationSupplierController {

    private final OrganizationSupplierService organizationSupplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> createAssociation(
            @Valid @RequestBody CreateOrganizationSupplierRequest request) {

        OrganizationSupplierResponse response = organizationSupplierService.createAssociation(request);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "Organization-supplier association created successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> getAssociationById(
            @PathVariable @Positive Long id) {

        OrganizationSupplierResponse response = organizationSupplierService.getAssociationById(id);

        ApiResponse <OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> getAssociationByOrganizationAndSupplier(
        @RequestParam @Positive Long organizationId,
        @RequestParam @Positive Long supplierId
    ) {

        OrganizationSupplierResponse response =
                organizationSupplierService.getAssociationByOrganizationAndSupplier(
                        organizationId,
                        supplierId
                );

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }


    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<PageResponse<OrganizationSupplierResponse>>> getAssociationsByOrganization(
            @PathVariable @Positive Long organizationId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive @Max(100) int size) {

        PageResponse<OrganizationSupplierResponse> response =
                organizationSupplierService.getAssociationsByOrganization(
                        organizationId,
                        page,
                        size
                );

        ApiResponse<PageResponse<OrganizationSupplierResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier associations retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<PageResponse<OrganizationSupplierResponse>>> getAssociationsBySupplier(
            @PathVariable @Positive Long supplierId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive @Max(100) int size) {

        PageResponse<OrganizationSupplierResponse> response =
                organizationSupplierService.getAssociationsBySupplier(
                        supplierId,
                        page,
                        size
                );

        ApiResponse<PageResponse<OrganizationSupplierResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier associations retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> updateAssociation(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateOrganizationSupplierRequest request) {

        OrganizationSupplierResponse response =
                organizationSupplierService.updateAssociation(id, request);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association updated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> activateAssociation(
            @PathVariable @Positive Long id) {

        OrganizationSupplierResponse response = organizationSupplierService.activateAssociation(id);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association activated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> deactivateAssociation(
            @PathVariable @Positive Long id) {

        OrganizationSupplierResponse response = organizationSupplierService.deactivateAssociation(id);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association deactivated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> suspendAssociation(
            @PathVariable @Positive Long id) {

        OrganizationSupplierResponse response = organizationSupplierService.suspendAssociation(id);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association suspended successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> blockAssociation(
            @PathVariable @Positive Long id) {

        OrganizationSupplierResponse response = organizationSupplierService.blockAssociation(id);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association blocked successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/risk-level")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> updateRiskLevel(
            @PathVariable @Positive Long id,
            @RequestParam SupplierRiskLevel riskLevel
    ) {

        OrganizationSupplierResponse response = organizationSupplierService.updateRiskLevel(id, riskLevel);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association risk-level updated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssociation(@PathVariable @Positive Long id) {

        organizationSupplierService.deleteAssociation(id);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association deleted successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<OrganizationSupplierResponse>> restoreAssociation(
            @PathVariable @Positive Long id) {

        OrganizationSupplierResponse response = organizationSupplierService.restoreAssociation(id);

        ApiResponse<OrganizationSupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Organization-supplier association restored successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
