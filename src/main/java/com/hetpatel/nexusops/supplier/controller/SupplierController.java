package com.hetpatel.nexusops.supplier.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;
import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierResponse;

import com.hetpatel.nexusops.supplier.service.SupplierService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request) {

        SupplierResponse response = supplierService.createSupplier(request);

        ApiResponse<SupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "Supplier created successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {

        SupplierResponse response = supplierService.getSupplierById(id);

        ApiResponse<SupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponse>>> searchSuppliers(
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<SupplierResponse> response = supplierService.searchSuppliers(search, pageable);

        ApiResponse<PageResponse<SupplierResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Suppliers retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierRequest request) {

        SupplierResponse response =
                supplierService.updateSupplier(id, request);

        ApiResponse<SupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier updated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {

        supplierService.deleteSupplier(id);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier deleted successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<SupplierResponse>> restoreSupplier(@PathVariable Long id) {

        SupplierResponse response = supplierService.restoreSupplier(id);

        ApiResponse<SupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier restored successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<SupplierResponse>> verifySupplier(@PathVariable Long id) {

        SupplierResponse response = supplierService.verifySupplier(id);

        ApiResponse<SupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier verified successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<SupplierResponse>> rejectSupplier(@PathVariable Long id) {

        SupplierResponse response = supplierService.rejectSupplier(id);

        ApiResponse<SupplierResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "Supplier rejected successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
