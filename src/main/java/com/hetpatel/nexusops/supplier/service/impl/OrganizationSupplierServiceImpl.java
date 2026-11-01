package com.hetpatel.nexusops.supplier.service.impl;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.BusinessException;
import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.organization.service.OrganizationService;

import com.hetpatel.nexusops.supplier.dto.request.CreateOrganizationSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateOrganizationSupplierRequest;

import com.hetpatel.nexusops.supplier.dto.response.OrganizationSupplierResponse;
import com.hetpatel.nexusops.supplier.dto.response.SupplierResponse;

import com.hetpatel.nexusops.supplier.entity.*;

import com.hetpatel.nexusops.supplier.mapper.OrganizationSupplierMapper;

import com.hetpatel.nexusops.supplier.repository.OrganizationSupplierRepository;

import com.hetpatel.nexusops.supplier.service.OrganizationSupplierService;
import com.hetpatel.nexusops.supplier.service.SupplierService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrganizationSupplierServiceImpl implements OrganizationSupplierService {

    private final OrganizationSupplierRepository organizationSupplierRepository;
    private final OrganizationSupplierMapper organizationSupplierMapper;

    private final OrganizationService organizationService;
    private final SupplierService supplierService;

    @Override
    @Transactional
    public OrganizationSupplierResponse createAssociation(CreateOrganizationSupplierRequest request) {

        if (request == null) {
            throw new BusinessException("Request must not be null");
        }

        Long organizationId = request.getOrganizationId();
        Long supplierId = request.getSupplierId();

        organizationService.getOrganizationById(organizationId);
        SupplierResponse supplier = supplierService.getSupplierById(supplierId);

        if (supplier.getRegistrationStatus() != SupplierRegistrationStatus.VERIFIED) {
            log.warn(
                    "Cannot associate supplier with organization: supplierId={}, registrationStatus={}",
                    supplierId,
                    supplier.getRegistrationStatus()
            );

            throw new BusinessException(
                    "Only verified suppliers can be associated with an organization"
            );
        }

        Optional<Boolean> deletedStatus =
                organizationSupplierRepository.findDeletedStatusByOrganizationIdAndSupplierId(
                        organizationId,
                        supplierId
                );

        if (deletedStatus.isPresent()) {
            if (deletedStatus.get()) {
                log.warn(
                        "Cannot create association because a soft-deleted association exists: organizationId={}, supplierId={}",
                        organizationId,
                        supplierId
                );

                throw new ConflictException(
                        "A deleted association already exists. Restore the existing association instead"
                );
            }

            log.warn(
                    "Organization-supplier association already exists: organizationId={}, supplierId={}",
                    organizationId,
                    supplierId
            );

            throw new ConflictException(
                    "Supplier is already associated with this organization"
            );
        }

        OrganizationSupplier association = organizationSupplierMapper.toEntity(request);

        association.setStatus(OrganizationSupplierStatus.PENDING);
        association.setRiskLevel(SupplierRiskLevel.LOW);

        OrganizationSupplier savedAssociation = organizationSupplierRepository.saveAndFlush(association);

        log.info(
                "Organization-supplier association created successfully: associationId={}, organizationId={}, supplierId={}",
                savedAssociation.getId(),
                organizationId,
                supplierId
        );

        return organizationSupplierMapper.toResponse(savedAssociation);
    }

    @Override
    public OrganizationSupplierResponse getAssociationById(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found: id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        log.info(
                "Organization-supplier association retrieved successfully: id={}",
                organizationSupplier.getId()
        );

        return organizationSupplierMapper.toResponse(organizationSupplier);
    }

    @Override
    public OrganizationSupplierResponse getAssociationByOrganizationAndSupplier(Long organizationId, Long supplierId) {

        if (organizationId == null || organizationId <= 0) {
            throw new BusinessException("Organization Id must be a positive value.");
        }

        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException("Supplier Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findByOrganizationIdAndSupplierId(organizationId, supplierId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found: organizationId={}, supplierId={}",
                                    organizationId,
                                    supplierId
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with organizationId: " + organizationId + " and supplierId: " + supplierId
                            );
                        });

        log.info(
                "Organization-supplier association retrieved successfully: organizationId={}, supplierId={}",
                organizationId,
                supplierId
        );

        return organizationSupplierMapper.toResponse(organizationSupplier);
    }

    @Override
    public PageResponse<OrganizationSupplierResponse> getAssociationsByOrganization(Long organizationId, int page, int size) {

        if (organizationId == null || organizationId <= 0) {
            throw new BusinessException("Organization Id must be a positive value");
        }

        if (page < 0) {
            throw new BusinessException("Page number must not be negative.");
        }

        if (size <= 0 || size > 100) {
            throw new BusinessException("Page size must be between 1 and 100.");
        }

        organizationService.getOrganizationById(organizationId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<OrganizationSupplier> associationPage =
                organizationSupplierRepository.findAllByOrganizationId(
                        organizationId,
                        pageable
                );

        List<OrganizationSupplierResponse> content =
                associationPage
                        .getContent()
                        .stream()
                        .map(organizationSupplierMapper::toResponse)
                        .toList();

        log.info(
                "Organization-supplier associations retrieved: organizationId={}, page={}, size={}, totalElements={}",
                organizationId,
                associationPage.getNumber(),
                associationPage.getSize(),
                associationPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                associationPage.getNumber(),
                associationPage.getSize(),
                associationPage.getTotalElements(),
                associationPage.getTotalPages(),
                associationPage.isLast()
        );
    }

    @Override
    public PageResponse<OrganizationSupplierResponse> getAssociationsBySupplier(Long supplierId, int page, int size) {

        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException("Supplier Id must be a positive value.");
        }

        if (page < 0) {
            throw new BusinessException("Page number must not be negative.");
        }

        if (size <= 0 || size > 100) {
            throw new BusinessException("Page size must be between 1 and 100.");
        }

        supplierService.getSupplierById(supplierId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<OrganizationSupplier> associationPage =
                organizationSupplierRepository.findAllBySupplierId(
                        supplierId,
                        pageable
                );

        List<OrganizationSupplierResponse> content =
                associationPage
                        .getContent()
                        .stream()
                        .map(organizationSupplierMapper::toResponse)
                        .toList();

        log.info(
                "Organization-supplier associations retrieved: supplierId={}, page={}, size={}, totalElements={}",
                supplierId,
                associationPage.getNumber(),
                associationPage.getSize(),
                associationPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                associationPage.getNumber(),
                associationPage.getSize(),
                associationPage.getTotalElements(),
                associationPage.getTotalPages(),
                associationPage.isLast()
        );
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse updateAssociation(Long id, UpdateOrganizationSupplierRequest request) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        if (request == null) {
            throw new BusinessException("Request must not be null.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association update failed: association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        organizationSupplierMapper.updateEntity(request, organizationSupplier);

        OrganizationSupplier updatedAssociation = organizationSupplierRepository.save(organizationSupplier);

        log.info(
                "Organization-supplier association updated successfully: id={}, organizationId={}, supplierId={}",
                updatedAssociation.getId(),
                updatedAssociation.getOrganizationId(),
                updatedAssociation.getSupplierId()
        );

        return organizationSupplierMapper.toResponse(updatedAssociation);
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse activateAssociation(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        OrganizationSupplierStatus currentStatus = organizationSupplier.getStatus();

        if (currentStatus != OrganizationSupplierStatus.PENDING
                && currentStatus != OrganizationSupplierStatus.INACTIVE) {

            log.warn(
                    "Organization-supplier association activation rejected: id={}, currentStatus={}",
                    id,
                    currentStatus
            );

            throw new ConflictException(
                    "Only PENDING or INACTIVE associations can be activated."
            );
        }

        organizationSupplier.setStatus(OrganizationSupplierStatus.ACTIVE);

        OrganizationSupplier activatedAssociation = organizationSupplierRepository.save(organizationSupplier);

        log.info(
                "Organization-supplier association activated successfully: id={}, organizationId={}, supplierId={}",
                activatedAssociation.getId(),
                activatedAssociation.getOrganizationId(),
                activatedAssociation.getSupplierId()
        );

        return organizationSupplierMapper.toResponse(activatedAssociation);
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse deactivateAssociation(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        OrganizationSupplierStatus currentStatus = organizationSupplier.getStatus();

        if (currentStatus != OrganizationSupplierStatus.ACTIVE) {

            log.warn(
                    "Organization-supplier association deactivation rejected: id={}, currentStatus={}",
                    id,
                    currentStatus
            );

            throw new ConflictException(
                    "Only ACTIVE associations can be deactivated."
            );
        }

        organizationSupplier.setStatus(OrganizationSupplierStatus.INACTIVE);

        OrganizationSupplier deactivatedAssociation = organizationSupplierRepository.save(organizationSupplier);

        log.info(
                "Organization-supplier association deactivated successfully: id={}, organizationId={}, supplierId={}",
                deactivatedAssociation.getId(),
                deactivatedAssociation.getOrganizationId(),
                deactivatedAssociation.getSupplierId()
        );

        return organizationSupplierMapper.toResponse(deactivatedAssociation);
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse suspendAssociation(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        OrganizationSupplierStatus currentStatus = organizationSupplier.getStatus();

        if (currentStatus != OrganizationSupplierStatus.ACTIVE) {

            log.warn(
                    "Organization-supplier association suspension rejected: id={}, currentStatus={}",
                    id,
                    currentStatus
            );

            throw new ConflictException(
                    "Only ACTIVE associations can be suspended."
            );
        }

        organizationSupplier.setStatus(OrganizationSupplierStatus.SUSPENDED);

        OrganizationSupplier suspendedAssociation = organizationSupplierRepository.save(organizationSupplier);

        log.info(
                "Organization-supplier association suspended successfully: id={}, organizationId={}, supplierId={}",
                suspendedAssociation.getId(),
                suspendedAssociation.getOrganizationId(),
                suspendedAssociation.getSupplierId()
        );

        return organizationSupplierMapper.toResponse(suspendedAssociation);
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse blockAssociation(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        OrganizationSupplierStatus currentStatus = organizationSupplier.getStatus();

        if (currentStatus == OrganizationSupplierStatus.BLOCKED) {

            log.warn(
                    "Organization-supplier association blocking rejected: id={}, currentStatus={}",
                    id,
                    currentStatus
            );

            throw new ConflictException(
                    "Association is already blocked."
            );
        }

        organizationSupplier.setStatus(OrganizationSupplierStatus.BLOCKED);

        OrganizationSupplier blockedAssociation =
                organizationSupplierRepository.save(organizationSupplier);

        log.info(
                "Organization-supplier association blocked successfully: id={}, organizationId={}, supplierId={}",
                blockedAssociation.getId(),
                blockedAssociation.getOrganizationId(),
                blockedAssociation.getSupplierId()
        );

        return organizationSupplierMapper.toResponse(blockedAssociation);
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse updateRiskLevel(Long id, SupplierRiskLevel riskLevel) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        if (riskLevel == null) {
            throw new BusinessException("Risk level must not be null.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        OrganizationSupplierStatus currentStatus = organizationSupplier.getStatus();

        if (currentStatus == OrganizationSupplierStatus.BLOCKED) {

            log.warn(
                    "Organization-supplier association risk-level update rejected: id={}, currentStatus={}",
                    id,
                    currentStatus
            );

            throw new ConflictException(
                    "Association is blocked."
            );
        }

        organizationSupplier.setRiskLevel(riskLevel);

        OrganizationSupplier updatedRiskLevel =
                organizationSupplierRepository.save(organizationSupplier);

        log.info(
                "Organization-supplier association risk level updated successfully: id={}, organizationId={}, supplierId={}, riskLevel={}",
                updatedRiskLevel.getId(),
                updatedRiskLevel.getOrganizationId(),
                updatedRiskLevel.getSupplierId(),
                updatedRiskLevel.getRiskLevel()
        );

        return organizationSupplierMapper.toResponse(updatedRiskLevel);
    }

    @Override
    @Transactional
    public void deleteAssociation(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        OrganizationSupplier organizationSupplier =
                organizationSupplierRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization-supplier association deletion failed: association not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization-supplier association not found with id: " + id
                            );
                        });

        organizationSupplierRepository.delete(organizationSupplier);

        log.info(
                "Organization-supplier association deleted successfully: id={}",
                id
        );
    }

    @Override
    @Transactional
    public OrganizationSupplierResponse restoreAssociation(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("Id must be a positive value.");
        }

        int restoredRows = organizationSupplierRepository.restoreById(id);

        if (restoredRows == 0) {
            log.warn(
                    "Organization-supplier association restoration failed: deleted association not found, id={}",
                    id
            );

            throw new ResourceNotFoundException(
                    "Deleted organization-supplier association not found with id: " + id
            );
        }

        OrganizationSupplier restoredOrganizationSupplier =
                organizationSupplierRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Restored organization-supplier association not found with id: " + id
                                )
                        );

        OrganizationSupplier savedAssociation =
                organizationSupplierRepository.save(restoredOrganizationSupplier);

        log.info(
                "Organization-supplier association restored successfully: id={}, organizationId={}, supplierId={}",
                savedAssociation.getId(),
                savedAssociation.getOrganizationId(),
                savedAssociation.getSupplierId()
        );

        return organizationSupplierMapper.toResponse(savedAssociation);
    }
}
