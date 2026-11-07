package com.hetpatel.nexusops.supplier.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;
import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierContactRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierContactRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierContactResponse;

import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import com.hetpatel.nexusops.supplier.service.SupplierContactService;

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
@RequestMapping("/api/v1/supplier-contacts")
@Validated
@RequiredArgsConstructor
public class SupplierContactController {

    private final SupplierContactService supplierContactService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierContactResponse>> createSupplierContact(
            @Valid @RequestBody CreateSupplierContactRequest request) {

        SupplierContactResponse response = supplierContactService.createSupplierContact(request);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "Supplier contact created successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierContactResponse>> getSupplierContactById(
            @PathVariable @Positive Long id) {

        SupplierContactResponse response = supplierContactService.getSupplierContactById(id);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contact retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<PageResponse<SupplierContactResponse>>> getSupplierContactsBySupplier(
            @PathVariable @Positive Long supplierId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive @Max(100) int size) {

        PageResponse<SupplierContactResponse> response =
                supplierContactService.getSupplierContactsBySupplier(
                        supplierId,
                        page,
                        size
                );

        ApiResponse<PageResponse<SupplierContactResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contacts retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping("/supplier/{supplierId}/search")
    public ResponseEntity<ApiResponse<PageResponse<SupplierContactResponse>>> searchSupplierContacts(
            @PathVariable @Positive Long supplierId,
            @RequestParam(required = false) SupplierContactType contactType,
            @RequestParam(required = false) SupplierContactStatus status,
            @RequestParam(required = false) Boolean isPrimary,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive @Max(100) int size) {

        PageResponse<SupplierContactResponse> response =
                supplierContactService.searchSupplierContacts(
                        supplierId,
                        contactType,
                        status,
                        isPrimary,
                        search,
                        page,
                        size
                );

        ApiResponse<PageResponse<SupplierContactResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contacts searched successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierContactResponse>> updateSupplierContact(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateSupplierContactRequest request) {

        SupplierContactResponse response = supplierContactService.updateSupplierContact(id, request);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contact updated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/primary")
    public ResponseEntity<ApiResponse<SupplierContactResponse>> setPrimaryContact(
            @PathVariable @Positive Long id) {

        SupplierContactResponse response = supplierContactService.setPrimaryContact(id);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier primary contact set successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<SupplierContactResponse>> activateSupplierContact(
            @PathVariable @Positive Long id) {

        SupplierContactResponse response = supplierContactService.activateSupplierContact(id);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contact activated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("e/{id}/deactivat")
    public ResponseEntity<ApiResponse<SupplierContactResponse>> deactivateSupplierContact(
            @PathVariable @Positive Long id) {

        SupplierContactResponse response = supplierContactService.deactivateSupplierContact(id);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contact deactivated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplierContact(
            @PathVariable @Positive Long id) {

        supplierContactService.deleteSupplierContact(id);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contact deleted successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<SupplierContactResponse>> restoreSupplierContact(
            @PathVariable @Positive Long id) {

        SupplierContactResponse response = supplierContactService.restoreSupplierContact(id);

        ApiResponse<SupplierContactResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier contact restored successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
