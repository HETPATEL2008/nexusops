package com.hetpatel.nexusops.organization.service;

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

import com.hetpatel.nexusops.organization.service.impl.OrganizationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private OrganizationProvisioningService organizationProvisioningService;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Organization organization;

    private OrganizationResponse organizationResponse;

    @BeforeEach
    void setUp() {

        organization = new Organization();

        organization.setId(1L);
        organization.setName("Test Organization");
        organization.setCode("TEST001");
        organization.setEmail("test@example.com");
        organization.setStatus(OrganizationStatus.ACTIVE);

        organizationResponse = new OrganizationResponse();

        organizationResponse.setId(1L);
        organizationResponse.setName("Test Organization");
        organizationResponse.setCode("TEST001");
        organizationResponse.setEmail("test@example.com");
        organizationResponse.setStatus(OrganizationStatus.ACTIVE);
    }

    @Test
    void createOrganization_shouldCreateOrganizationSuccessfully() {

        CreateOrganizationRequest request =
                new CreateOrganizationRequest();

        request.setName("Test Organization");
        request.setCode("TEST001");
        request.setEmail("test@example.com");

        when(organizationRepository.existsByCode("TEST001"))
                .thenReturn(false);

        when(organizationMapper.toEntity(request))
                .thenReturn(organization);

        when(organizationRepository.save(organization))
                .thenReturn(organization);

        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        OrganizationResponse result =
                organizationService.createOrganization(request);

        assertThat(result)
                .isSameAs(organizationResponse);

        assertThat(organization.getStatus())
                .isEqualTo(OrganizationStatus.ACTIVE);

        verify(organizationRepository)
                .existsByCode("TEST001");

        verify(organizationMapper)
                .toEntity(request);

        verify(organizationRepository)
                .save(organization);

        verify(organizationProvisioningService)
                .provisionDefaultRoles(organization);

        verify(organizationMapper)
                .toResponse(organization);
    }

    @Test
    void createOrganization_shouldThrowConflictException_whenCodeAlreadyExists() {

        CreateOrganizationRequest request =
                new CreateOrganizationRequest();

        request.setName("Test Organization");
        request.setCode("TEST001");
        request.setEmail("test@example.com");

        when(organizationRepository.existsByCode("TEST001"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                organizationService.createOrganization(request)
        )
                .isInstanceOf(ConflictException.class);

        verify(organizationRepository)
                .existsByCode("TEST001");

        verify(organizationMapper, never())
                .toEntity(any());

        verify(organizationRepository, never())
                .save(any());

        verify(organizationProvisioningService, never())
                .provisionDefaultRoles(any());
    }

    @Test
    void getOrganizationById_shouldReturnOrganization_whenOrganizationExists() {

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        OrganizationResponse result =
                organizationService.getOrganizationById(1L);

        assertThat(result)
                .isSameAs(organizationResponse);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationMapper)
                .toResponse(organization);
    }

    @Test
    void getOrganizationById_shouldThrowResourceNotFoundException_whenOrganizationDoesNotExist() {

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                organizationService.getOrganizationById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationMapper, never())
                .toResponse(any());
    }

    @Test
    void getAllOrganizations_shouldReturnPagedOrganizations() {

        PageRequest pageable =
                PageRequest.of(0, 10);

        Page<Organization> page =
                new PageImpl<>(
                        List.of(organization),
                        pageable,
                        1
                );

        when(organizationRepository.findAll(pageable))
                .thenReturn(page);

        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        PageResponse<OrganizationResponse> result =
                organizationService.getAllOrganizations(pageable);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().get(0))
                .isSameAs(organizationResponse);

        assertThat(result.getPage())
                .isEqualTo(0);

        assertThat(result.getSize())
                .isEqualTo(10);

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getTotalPages())
                .isEqualTo(1);

        assertThat(result.isLast())
                .isTrue();

        verify(organizationRepository)
                .findAll(pageable);

        verify(organizationMapper)
                .toResponse(organization);
    }

    @Test
    void updateOrganization_shouldUpdateOrganizationSuccessfully() {

        UpdateOrganizationRequest request =
                new UpdateOrganizationRequest();

        request.setName("Updated Organization");
        request.setCode("UPDATED001");
        request.setEmail("updated@example.com");
        request.setStatus(OrganizationStatus.INACTIVE);

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(organizationRepository.existsByCode("UPDATED001"))
                .thenReturn(false);

        when(organizationRepository.save(organization))
                .thenReturn(organization);

        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        OrganizationResponse result =
                organizationService.updateOrganization(1L, request);

        assertThat(result)
                .isSameAs(organizationResponse);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationRepository)
                .existsByCode("UPDATED001");

        verify(organizationMapper)
                .updateEntity(request, organization);

        verify(organizationRepository)
                .save(organization);

        verify(organizationMapper)
                .toResponse(organization);
    }

    @Test
    void updateOrganization_shouldThrowConflictException_whenNewCodeAlreadyExists() {

        UpdateOrganizationRequest request =
                new UpdateOrganizationRequest();

        request.setName("Updated Organization");
        request.setCode("EXISTING001");
        request.setEmail("updated@example.com");
        request.setStatus(OrganizationStatus.ACTIVE);

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(organizationRepository.existsByCode("EXISTING001"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                organizationService.updateOrganization(1L, request)
        )
                .isInstanceOf(ConflictException.class);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationRepository)
                .existsByCode("EXISTING001");

        verify(organizationMapper, never())
                .updateEntity(any(), any());

        verify(organizationRepository, never())
                .save(any());
    }

    @Test
    void updateOrganization_shouldThrowResourceNotFoundException_whenOrganizationDoesNotExist() {

        UpdateOrganizationRequest request =
                new UpdateOrganizationRequest();

        request.setName("Updated Organization");
        request.setCode("UPDATED001");
        request.setEmail("updated@example.com");
        request.setStatus(OrganizationStatus.ACTIVE);

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                organizationService.updateOrganization(1L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationRepository, never())
                .save(any());
    }

    @Test
    void deleteOrganization_shouldDeleteOrganizationSuccessfully() {

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        organizationService.deleteOrganization(1L);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationRepository)
                .delete(organization);
    }

    @Test
    void deleteOrganization_shouldThrowResourceNotFoundException_whenOrganizationDoesNotExist() {

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                organizationService.deleteOrganization(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationRepository, never())
                .delete(any());
    }

    @Test
    void restoreOrganization_shouldRestoreSuccessfully() {

        Organization organization =
                new Organization();

        organization.setId(1L);
        organization.setName("Deleted Organization");
        organization.setCode("DELETED001");
        organization.setEmail("deleted@example.com");
        organization.setStatus(OrganizationStatus.ACTIVE);
        organization.setDeleted(true);

        OrganizationResponse response =
                new OrganizationResponse();

        response.setId(1L);
        response.setName("Deleted Organization");
        response.setCode("DELETED001");
        response.setEmail("deleted@example.com");
        response.setStatus(OrganizationStatus.ACTIVE);

        when(organizationRepository.restoreById(1L))
                .thenReturn(1);

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(organizationMapper.toResponse(organization))
                .thenReturn(response);

        OrganizationResponse result =
                organizationService.restoreOrganization(1L);

        assertThat(result)
                .isSameAs(response);

        verify(organizationRepository)
                .restoreById(1L);

        verify(organizationRepository)
                .findById(1L);

        verify(organizationMapper)
                .toResponse(organization);
    }

    @Test
    void restoreOrganization_shouldThrowNotFound_whenDeletedOrganizationDoesNotExist() {

        when(organizationRepository.restoreById(999L))
                .thenReturn(0);

        assertThatThrownBy(() ->
                organizationService.restoreOrganization(999L)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(organizationRepository)
                .restoreById(999L);

        verify(organizationRepository, never())
                .findById(anyLong());

        verify(organizationMapper, never())
                .toResponse(any());
    }

    @Test
    void createOrganization_shouldPropagateException_whenRoleProvisioningFails() {

        CreateOrganizationRequest request =
                new CreateOrganizationRequest();

        request.setName("Test Organization");
        request.setCode("TEST001");
        request.setEmail("test@example.com");

        when(organizationRepository.existsByCode("TEST001"))
                .thenReturn(false);

        when(organizationMapper.toEntity(request))
                .thenReturn(organization);

        when(organizationRepository.save(organization))
                .thenReturn(organization);

        org.mockito.Mockito.doThrow(
                        new RuntimeException("Role provisioning failed")
                )
                .when(organizationProvisioningService)
                .provisionDefaultRoles(organization);

        assertThatThrownBy(() ->
                organizationService.createOrganization(request)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Role provisioning failed");

        verify(organizationRepository)
                .save(organization);

        verify(organizationProvisioningService)
                .provisionDefaultRoles(organization);

        verify(organizationMapper, never())
                .toResponse(organization);
    }
}
