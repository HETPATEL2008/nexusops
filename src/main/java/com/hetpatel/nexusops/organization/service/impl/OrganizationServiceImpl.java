package com.hetpatel.nexusops.organization.service.impl;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.organization.dto.request.CreateOrganizationRequest;
import com.hetpatel.nexusops.organization.dto.request.UpdateOrganizationRequest;

import com.hetpatel.nexusops.organization.dto.response.OrganizationResponse;

import com.hetpatel.nexusops.organization.entity.Organization;
import com.hetpatel.nexusops.organization.entity.OrganizationStatus;

import com.hetpatel.nexusops.organization.mapper.OrganizationMapper;

import com.hetpatel.nexusops.organization.repository.OrganizationRepository;

import com.hetpatel.nexusops.organization.service.OrganizationService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {

        if (organizationRepository.existsByCode(request.getCode())) {
            log.warn(
                    "Organization creation failed: code already exists, code={}",
                    request.getCode()
            );

            throw new ConflictException(
                    "Organization with code '" + request.getCode() + "' already exists.");
        }

        Organization organization = organizationMapper.toEntity(request);

        organization.setStatus(OrganizationStatus.ACTIVE);

        Organization savedOrganization = organizationRepository.save(organization);

        log.info(
                "Organization created successfully: id={}, code={}",
                savedOrganization.getId(),
                savedOrganization.getCode()
        );

        return organizationMapper.toResponse(savedOrganization);
    }

    @Override
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization =
                organizationRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization not found: id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization not found with id: " + id
                            );
                        });

        return organizationMapper.toResponse(organization);
    }

    @Override
    public PageResponse<OrganizationResponse> getAllOrganizations(Pageable pageable) {

        Page<Organization> organizationPage = organizationRepository.findAll(pageable);

        List<OrganizationResponse> content =
                organizationPage
                        .getContent()
                        .stream()
                        .map(organizationMapper::toResponse)
                        .toList();

        log.info(
                "Organizations retrieved: page={}, size={}, totalElements={}",
                organizationPage.getNumber(),
                organizationPage.getSize(),
                organizationPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                organizationPage.getNumber(),
                organizationPage.getSize(),
                organizationPage.getTotalElements(),
                organizationPage.getTotalPages(),
                organizationPage.isLast()
        );
    }

    @Override
    public OrganizationResponse updateOrganization(
            Long id,
            UpdateOrganizationRequest request) {

        Organization organization =
                organizationRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization update failed: organization not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization not found with id: " + id
                            );
                        });

        if (!organization.getCode().equals(request.getCode())
                && organizationRepository.existsByCode(request.getCode())) {

            log.warn(
                    "Organization update failed: code already exists, id={}, code={}",
                    id,
                    request.getCode()
            );

            throw new ConflictException(
                    "Organization with code '" + request.getCode() + "' already exists."
            );
        }

        organizationMapper.updateEntity(request, organization);

        Organization updatedOrganization = organizationRepository.save(organization);

        log.info(
                "Organization updated successfully: id={}, code={}",
                updatedOrganization.getId(),
                updatedOrganization.getCode()
        );

        return organizationMapper.toResponse(updatedOrganization);
    }

    @Override
    public void deleteOrganization(Long id) {
        Organization organization =
                organizationRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Organization deletion failed: organization not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Organization not found with id: " + id
                            );
                        });

        organizationRepository.delete(organization);

        log.info(
                "Organization soft deleted successfully: id={}, code={}",
                organization.getId(),
                organization.getCode()
        );
    }

    @Override
    @Transactional
    public OrganizationResponse restoreOrganization(Long id) {

        int restoredRows = organizationRepository.restoreById(id);

        if (restoredRows == 0) {
            throw new ResourceNotFoundException(
                    "Deleted organization not found with id: " + id
            );
        }

        Organization restoredOrganization =
                organizationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found with id: " + id
                                )
                        );

        return organizationMapper.toResponse(restoredOrganization);
    }
}
