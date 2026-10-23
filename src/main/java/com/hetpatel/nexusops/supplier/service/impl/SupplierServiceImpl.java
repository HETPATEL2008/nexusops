package com.hetpatel.nexusops.supplier.service.impl;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierResponse;

import com.hetpatel.nexusops.supplier.entity.Supplier;
import com.hetpatel.nexusops.supplier.entity.SupplierRegistrationStatus;

import com.hetpatel.nexusops.supplier.mapper.SupplierMapper;

import com.hetpatel.nexusops.supplier.repository.SupplierRepository;

import com.hetpatel.nexusops.supplier.service.SupplierService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request) {

        if (supplierRepository.existsBySupplierCode(request.getSupplierCode())) {
            log.warn(
                    "Supplier creation failed: code already exists, code={}",
                    request.getSupplierCode()
            );

            throw new ConflictException(
                    "Supplier with code '" + request.getSupplierCode() + "' already exists.");
        }

        Supplier supplier = supplierMapper.toEntity(request);

        supplier.setRegistrationStatus(SupplierRegistrationStatus.PENDING_REVIEW);

        Supplier savedSupplier = supplierRepository.save(supplier);

        log.info(
                "Supplier created successfully: id={}, code={}",
                savedSupplier.getId(),
                savedSupplier.getSupplierCode()
        );

        return supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier =
                supplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier not found: id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier not found with id: " + id
                            );
                        });

        log.info(
                "Supplier retrieved successfully: id={}, code={}",
                supplier.getId(),
                supplier.getSupplierCode()
        );

        return supplierMapper.toResponse(supplier);
    }

    @Override
    public PageResponse<SupplierResponse> searchSuppliers(String search, Pageable pageable) {

        String searchTerm = search == null
                ? ""
                : search.trim();

        Specification<Supplier> specification = (root, query, criteriaBuilder) -> {
            if (searchTerm.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase(Locale.ROOT) + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("legalName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("displayName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("supplierCode")),
                            pattern
                    )
            );
        };

        Page<Supplier> supplierPage =
                supplierRepository.findAll(specification, pageable);

        List<SupplierResponse> content = supplierPage
                .getContent()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();

        log.info(
                "Suppliers retrieved: searchProvided={}, page={}, size={}, totalElements={}",
                !searchTerm.isBlank(),
                supplierPage.getNumber(),
                supplierPage.getSize(),
                supplierPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                supplierPage.getNumber(),
                supplierPage.getSize(),
                supplierPage.getTotalElements(),
                supplierPage.getTotalPages(),
                supplierPage.isLast()
        );
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(
            Long id,
            UpdateSupplierRequest request) {

        Supplier supplier =
                supplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier update failed: supplier not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier not found with id: " + id
                            );
                        });

        supplierMapper.updateEntity(request, supplier);

        Supplier updatedSupplier = supplierRepository.save(supplier);

        log.info(
                "Supplier updated successfully: id={}, code={}",
                updatedSupplier.getId(),
                updatedSupplier.getSupplierCode()
        );

        return supplierMapper.toResponse(updatedSupplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier =
                supplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier deletion failed: supplier not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier not found with id: " + id
                            );
                        });

        supplierRepository.delete(supplier);

        log.info(
                "Supplier soft deleted successfully: id={}, code={}",
                supplier.getId(),
                supplier.getSupplierCode()
        );
    }

    @Override
    @Transactional
    public SupplierResponse restoreSupplier(Long id) {

        int restoredRows = supplierRepository.restoreById(id);

        if (restoredRows == 0) {
            throw new ResourceNotFoundException(
                    "Deleted supplier not found with id: " + id
            );
        }

        Supplier restoredSupplier =
                supplierRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Restored supplier not found with id: " + id
                                )
                        );

        log.info(
                "Supplier restored successfully: id={}, code={}",
                restoredSupplier.getId(),
                restoredSupplier.getSupplierCode()
        );

        return supplierMapper.toResponse(restoredSupplier);
    }

    @Override
    @Transactional
    public SupplierResponse verifySupplier(Long id) {
        Supplier supplier =
                supplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier verification failed: supplier not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier not found with id: " + id
                            );
                        });

        if (supplier.getRegistrationStatus()
                != SupplierRegistrationStatus.PENDING_REVIEW) {

            log.warn(
                    "Supplier verification failed: invalid registration status, id={}, status={}",
                    id,
                    supplier.getRegistrationStatus()
            );

            throw new ConflictException(
                    "Only suppliers pending review can be verified."
            );
        }

        supplier.setRegistrationStatus(SupplierRegistrationStatus.VERIFIED);

        Supplier verifiedSupplier = supplierRepository.save(supplier);

        log.info(
                "Supplier verified successfully: id={}, code={}",
                verifiedSupplier.getId(),
                verifiedSupplier.getSupplierCode()
        );

        return supplierMapper.toResponse(verifiedSupplier);
    }

    @Override
    @Transactional
    public SupplierResponse rejectSupplier(Long id) {
        Supplier supplier =
                supplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier rejection failed: supplier not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier not found with id: " + id
                            );
                        });

        if (supplier.getRegistrationStatus()
                != SupplierRegistrationStatus.PENDING_REVIEW) {

            log.warn(
                    "Supplier rejection failed: invalid registration status, id={}, status={}",
                    id,
                    supplier.getRegistrationStatus()
            );

            throw new ConflictException(
                    "Only suppliers pending review can be rejected."
            );
        }

        supplier.setRegistrationStatus(SupplierRegistrationStatus.REJECTED);

        Supplier rejectedSupplier = supplierRepository.save(supplier);

        log.info(
                "Supplier rejected successfully: id={}, code={}",
                rejectedSupplier.getId(),
                rejectedSupplier.getSupplierCode()
        );

        return supplierMapper.toResponse(rejectedSupplier);
    }
}
