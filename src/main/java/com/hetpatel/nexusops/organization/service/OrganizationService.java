package com.hetpatel.nexusops.organization.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.organization.dto.request.CreateOrganizationRequest;
import com.hetpatel.nexusops.organization.dto.request.UpdateOrganizationRequest;

import com.hetpatel.nexusops.organization.dto.response.OrganizationResponse;

import org.springframework.data.domain.Pageable;

public interface OrganizationService {

    OrganizationResponse createOrganization(CreateOrganizationRequest request);

    OrganizationResponse getOrganizationById(Long id);

    PageResponse<OrganizationResponse> getAllOrganizations(Pageable pageable);

    OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest request);

    void deleteOrganization(Long id);

    OrganizationResponse restoreOrganization(Long id);
}
