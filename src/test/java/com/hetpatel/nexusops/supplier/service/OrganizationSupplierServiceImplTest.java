package com.hetpatel.nexusops.supplier.service;

import com.hetpatel.nexusops.common.api.PageResponse;
import com.hetpatel.nexusops.common.exception.BusinessException;
import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.organization.entity.Organization;
import com.hetpatel.nexusops.organization.repository.OrganizationRepository;
import com.hetpatel.nexusops.organization.service.OrganizationService;

import com.hetpatel.nexusops.supplier.dto.request.CreateOrganizationSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateOrganizationSupplierRequest;
import com.hetpatel.nexusops.supplier.dto.response.OrganizationSupplierResponse;
import com.hetpatel.nexusops.supplier.dto.response.SupplierResponse;

import com.hetpatel.nexusops.supplier.entity.OrganizationSupplier;
import com.hetpatel.nexusops.supplier.entity.OrganizationSupplierStatus;
import com.hetpatel.nexusops.supplier.entity.Supplier;
import com.hetpatel.nexusops.supplier.entity.SupplierRegistrationStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierRiskLevel;

import com.hetpatel.nexusops.supplier.mapper.OrganizationSupplierMapper;

import com.hetpatel.nexusops.supplier.repository.OrganizationSupplierRepository;
import com.hetpatel.nexusops.supplier.repository.SupplierRepository;

import com.hetpatel.nexusops.supplier.service.impl.OrganizationSupplierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
class OrganizationSupplierServiceImplTest {

    @Mock
    private OrganizationSupplierRepository organizationSupplierRepository;

    @Mock
    private OrganizationSupplierMapper organizationSupplierMapper;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private OrganizationSupplierServiceImpl organizationSupplierService;

    private Organization organization;
    private Supplier supplier;

    private OrganizationSupplier association;
    private OrganizationSupplierResponse associationResponse;
    private SupplierResponse supplierResponse;

    private static final Long ORGANIZATION_ID = 1L;
    private static final Long SUPPLIER_ID = 2L;
    private static final Long ASSOCIATION_ID = 3L;

    @BeforeEach
    void setUp() {

        organization = new Organization();
        organization.setId(ORGANIZATION_ID);

        supplier = new Supplier();
        supplier.setId(SUPPLIER_ID);
        supplier.setRegistrationStatus(
                SupplierRegistrationStatus.VERIFIED
        );

        association = new OrganizationSupplier();

        association.setId(ASSOCIATION_ID);
        association.setOrganization(organization);
        association.setSupplier(supplier);
        association.setStatus(OrganizationSupplierStatus.PENDING);
        association.setRiskLevel(SupplierRiskLevel.LOW);
        association.setNotes("Test association");

        associationResponse = new OrganizationSupplierResponse();

        associationResponse.setId(ASSOCIATION_ID);
        associationResponse.setOrganizationId(ORGANIZATION_ID);
        associationResponse.setSupplierId(SUPPLIER_ID);
        associationResponse.setStatus(OrganizationSupplierStatus.PENDING);
        associationResponse.setRiskLevel(SupplierRiskLevel.LOW);
        associationResponse.setNotes("Test association");

        supplierResponse = new SupplierResponse();

        supplierResponse.setId(SUPPLIER_ID);
        supplierResponse.setRegistrationStatus(
                SupplierRegistrationStatus.VERIFIED
        );
    }

    // CREATE

    @Test
    void createAssociation_shouldCreateAssociationSuccessfully() {

        CreateOrganizationSupplierRequest request =
                new CreateOrganizationSupplierRequest();

        request.setOrganizationId(ORGANIZATION_ID);
        request.setSupplierId(SUPPLIER_ID);
        request.setNotes("Test association");

        when(organizationRepository.findById(ORGANIZATION_ID))
                .thenReturn(Optional.of(organization));

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(supplier));

        when(organizationSupplierRepository
                .findDeletedStatusByOrganizationIdAndSupplierId(
                        ORGANIZATION_ID,
                        SUPPLIER_ID
                )).thenReturn(Optional.empty());

        when(organizationSupplierMapper.toEntity(request))
                .thenReturn(association);

        when(organizationSupplierRepository.saveAndFlush(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService.createAssociation(request);

        assertThat(result).isSameAs(associationResponse);
        assertThat(association.getOrganization().getId())
                .isEqualTo(ORGANIZATION_ID);
        assertThat(association.getSupplier().getId())
                .isEqualTo(SUPPLIER_ID);
        assertThat(association.getStatus())
                .isEqualTo(OrganizationSupplierStatus.PENDING);
        assertThat(association.getRiskLevel())
                .isEqualTo(SupplierRiskLevel.LOW);

        verify(organizationRepository).findById(ORGANIZATION_ID);
        verify(supplierRepository).findById(SUPPLIER_ID);
        verify(organizationSupplierRepository)
                .saveAndFlush(association);
        verify(organizationSupplierMapper).toResponse(association);
    }

    @Test
    void createAssociation_shouldThrowBusinessException_whenRequestIsNull() {

        assertThatThrownBy(
                () -> organizationSupplierService.createAssociation(null)
        ).isInstanceOf(BusinessException.class);

        verify(organizationSupplierRepository, never())
                .saveAndFlush(any());
    }

    @Test
    void createAssociation_shouldThrowBusinessException_whenSupplierIsNotVerified() {

        CreateOrganizationSupplierRequest request =
                new CreateOrganizationSupplierRequest();

        request.setOrganizationId(ORGANIZATION_ID);
        request.setSupplierId(SUPPLIER_ID);

        supplier.setRegistrationStatus(
                SupplierRegistrationStatus.PENDING_REVIEW
        );

        when(organizationRepository.findById(ORGANIZATION_ID))
                .thenReturn(Optional.of(organization));

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(supplier));

        assertThatThrownBy(
                () -> organizationSupplierService.createAssociation(request)
        ).isInstanceOf(BusinessException.class);

        verify(organizationSupplierRepository, never())
                .saveAndFlush(any());
    }

    @Test
    void createAssociation_shouldThrowConflict_whenActiveAssociationExists() {

        CreateOrganizationSupplierRequest request =
                new CreateOrganizationSupplierRequest();

        request.setOrganizationId(ORGANIZATION_ID);
        request.setSupplierId(SUPPLIER_ID);

        when(organizationRepository.findById(ORGANIZATION_ID))
                .thenReturn(Optional.of(organization));

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(supplier));

        when(organizationSupplierRepository
                .findDeletedStatusByOrganizationIdAndSupplierId(
                        ORGANIZATION_ID,
                        SUPPLIER_ID
                )).thenReturn(Optional.of(false));

        assertThatThrownBy(
                () -> organizationSupplierService.createAssociation(request)
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never())
                .saveAndFlush(any());
    }

    @Test
    void createAssociation_shouldThrowConflict_whenDeletedAssociationExists() {

        CreateOrganizationSupplierRequest request =
                new CreateOrganizationSupplierRequest();

        request.setOrganizationId(ORGANIZATION_ID);
        request.setSupplierId(SUPPLIER_ID);

        when(organizationRepository.findById(ORGANIZATION_ID))
                .thenReturn(Optional.of(organization));

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(supplier));

        when(organizationSupplierRepository
                .findDeletedStatusByOrganizationIdAndSupplierId(
                        ORGANIZATION_ID,
                        SUPPLIER_ID
                )).thenReturn(Optional.of(true));

        assertThatThrownBy(
                () -> organizationSupplierService.createAssociation(request)
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never())
                .saveAndFlush(any());
    }

    // GET BY ID

    @Test
    void getAssociationById_shouldReturnAssociation_whenAssociationExists() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService.getAssociationById(ASSOCIATION_ID);

        assertThat(result).isSameAs(associationResponse);

        verify(organizationSupplierRepository).findById(ASSOCIATION_ID);
        verify(organizationSupplierMapper).toResponse(association);
    }

    @Test
    void getAssociationById_shouldThrowNotFound_whenAssociationDoesNotExist() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> organizationSupplierService.getAssociationById(
                        ASSOCIATION_ID
                )
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(organizationSupplierMapper, never()).toResponse(any());
    }

    // GET BY ORGANIZATION AND SUPPLIER

    @Test
    void getAssociationByOrganizationAndSupplier_shouldReturnAssociation() {

        when(organizationSupplierRepository
                .findByOrganization_IdAndSupplier_Id(
                        ORGANIZATION_ID,
                        SUPPLIER_ID
                )).thenReturn(Optional.of(association));

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService
                        .getAssociationByOrganizationAndSupplier(
                                ORGANIZATION_ID,
                                SUPPLIER_ID
                        );

        assertThat(result).isSameAs(associationResponse);
    }

    @Test
    void getAssociationByOrganizationAndSupplier_shouldThrowNotFound_whenAssociationDoesNotExist() {

        when(organizationSupplierRepository
                .findByOrganization_IdAndSupplier_Id(
                        ORGANIZATION_ID,
                        SUPPLIER_ID
                )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> organizationSupplierService
                        .getAssociationByOrganizationAndSupplier(
                                ORGANIZATION_ID,
                                SUPPLIER_ID
                        )
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    // GET ASSOCIATIONS BY ORGANIZATION

    @Test
    void getAssociationsByOrganization_shouldReturnPagedAssociations() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<OrganizationSupplier> page =
                new PageImpl<>(List.of(association), pageable, 1);

        when(organizationSupplierRepository
                .findAllByOrganization_Id(ORGANIZATION_ID, pageable))
                .thenReturn(page);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        PageResponse<OrganizationSupplierResponse> result =
                organizationSupplierService
                        .getAssociationsByOrganization(
                                ORGANIZATION_ID,
                                0,
                                10
                        );

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0))
                .isSameAs(associationResponse);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.last()).isTrue();

        verify(organizationService).getOrganizationById(ORGANIZATION_ID);
        verify(organizationSupplierMapper).toResponse(association);
    }

    // GET ASSOCIATIONS BY SUPPLIER

    @Test
    void getAssociationsBySupplier_shouldReturnPagedAssociations() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<OrganizationSupplier> page =
                new PageImpl<>(List.of(association), pageable, 1);

        when(organizationSupplierRepository
                .findAllBySupplier_Id(SUPPLIER_ID, pageable))
                .thenReturn(page);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        PageResponse<OrganizationSupplierResponse> result =
                organizationSupplierService
                        .getAssociationsBySupplier(
                                SUPPLIER_ID,
                                0,
                                10
                        );

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0))
                .isSameAs(associationResponse);
        assertThat(result.totalElements()).isEqualTo(1);

        verify(supplierService).getSupplierById(SUPPLIER_ID);
        verify(organizationSupplierMapper).toResponse(association);
    }

    // UPDATE

    @Test
    void updateAssociation_shouldUpdateSuccessfully() {

        UpdateOrganizationSupplierRequest request =
                new UpdateOrganizationSupplierRequest();

        request.setNotes("Updated notes");

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService.updateAssociation(
                        ASSOCIATION_ID,
                        request
                );

        assertThat(result).isSameAs(associationResponse);

        verify(organizationSupplierMapper)
                .updateEntity(request, association);

        verify(organizationSupplierRepository).save(association);
    }

    @Test
    void updateAssociation_shouldThrowNotFound_whenAssociationDoesNotExist() {

        UpdateOrganizationSupplierRequest request =
                new UpdateOrganizationSupplierRequest();

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> organizationSupplierService.updateAssociation(
                        ASSOCIATION_ID,
                        request
                )
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(organizationSupplierRepository, never()).save(any());
        verify(organizationSupplierMapper, never())
                .updateEntity(any(), any());
    }

    // ACTIVATE

    @Test
    void activateAssociation_shouldActivatePendingAssociation() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService.activateAssociation(
                        ASSOCIATION_ID
                );

        assertThat(result).isSameAs(associationResponse);
        assertThat(association.getStatus())
                .isEqualTo(OrganizationSupplierStatus.ACTIVE);
    }

    @Test
    void activateAssociation_shouldActivateInactiveAssociation() {

        association.setStatus(OrganizationSupplierStatus.INACTIVE);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        organizationSupplierService.activateAssociation(ASSOCIATION_ID);

        assertThat(association.getStatus())
                .isEqualTo(OrganizationSupplierStatus.ACTIVE);
    }

    @Test
    void activateAssociation_shouldThrowConflict_whenAssociationCannotBeActivated() {

        association.setStatus(OrganizationSupplierStatus.BLOCKED);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        assertThatThrownBy(
                () -> organizationSupplierService.activateAssociation(
                        ASSOCIATION_ID
                )
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never()).save(any());
    }

    // DEACTIVATE

    @Test
    void deactivateAssociation_shouldDeactivateActiveAssociation() {

        association.setStatus(OrganizationSupplierStatus.ACTIVE);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        organizationSupplierService.deactivateAssociation(ASSOCIATION_ID);

        assertThat(association.getStatus())
                .isEqualTo(OrganizationSupplierStatus.INACTIVE);
    }

    @Test
    void deactivateAssociation_shouldThrowConflict_whenAssociationIsNotActive() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        assertThatThrownBy(
                () -> organizationSupplierService.deactivateAssociation(
                        ASSOCIATION_ID
                )
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never()).save(any());
    }

    // SUSPEND

    @Test
    void suspendAssociation_shouldSuspendActiveAssociation() {

        association.setStatus(OrganizationSupplierStatus.ACTIVE);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        organizationSupplierService.suspendAssociation(ASSOCIATION_ID);

        assertThat(association.getStatus())
                .isEqualTo(OrganizationSupplierStatus.SUSPENDED);
    }

    @Test
    void suspendAssociation_shouldThrowConflict_whenAssociationIsNotActive() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        assertThatThrownBy(
                () -> organizationSupplierService.suspendAssociation(
                        ASSOCIATION_ID
                )
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never()).save(any());
    }

    // BLOCK

    @Test
    void blockAssociation_shouldBlockAssociation() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        organizationSupplierService.blockAssociation(ASSOCIATION_ID);

        assertThat(association.getStatus())
                .isEqualTo(OrganizationSupplierStatus.BLOCKED);
    }

    @Test
    void blockAssociation_shouldThrowConflict_whenAlreadyBlocked() {

        association.setStatus(OrganizationSupplierStatus.BLOCKED);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        assertThatThrownBy(
                () -> organizationSupplierService.blockAssociation(
                        ASSOCIATION_ID
                )
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never()).save(any());
    }

    // UPDATE RISK LEVEL

    @Test
    void updateRiskLevel_shouldUpdateRiskLevelSuccessfully() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService.updateRiskLevel(
                        ASSOCIATION_ID,
                        SupplierRiskLevel.HIGH
                );

        assertThat(result).isSameAs(associationResponse);
        assertThat(association.getRiskLevel())
                .isEqualTo(SupplierRiskLevel.HIGH);
    }

    @Test
    void updateRiskLevel_shouldThrowConflict_whenAssociationIsBlocked() {

        association.setStatus(OrganizationSupplierStatus.BLOCKED);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        assertThatThrownBy(
                () -> organizationSupplierService.updateRiskLevel(
                        ASSOCIATION_ID,
                        SupplierRiskLevel.HIGH
                )
        ).isInstanceOf(ConflictException.class);

        verify(organizationSupplierRepository, never()).save(any());
    }

    // DELETE

    @Test
    void deleteAssociation_shouldDeleteSuccessfully() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        organizationSupplierService.deleteAssociation(ASSOCIATION_ID);

        verify(organizationSupplierRepository).delete(association);
    }

    @Test
    void deleteAssociation_shouldThrowNotFound_whenAssociationDoesNotExist() {

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> organizationSupplierService.deleteAssociation(
                        ASSOCIATION_ID
                )
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(organizationSupplierRepository, never())
                .delete(any(OrganizationSupplier.class));
    }

    // RESTORE

    @Test
    void restoreAssociation_shouldRestoreSuccessfully() {

        when(organizationSupplierRepository.restoreById(ASSOCIATION_ID))
                .thenReturn(1);

        when(organizationSupplierRepository.findById(ASSOCIATION_ID))
                .thenReturn(Optional.of(association));

        when(organizationSupplierRepository.save(association))
                .thenReturn(association);

        when(organizationSupplierMapper.toResponse(association))
                .thenReturn(associationResponse);

        OrganizationSupplierResponse result =
                organizationSupplierService.restoreAssociation(
                        ASSOCIATION_ID
                );

        assertThat(result).isSameAs(associationResponse);

        verify(organizationSupplierRepository).restoreById(ASSOCIATION_ID);
        verify(organizationSupplierRepository).findById(ASSOCIATION_ID);
        verify(organizationSupplierMapper).toResponse(association);
    }

    @Test
    void restoreAssociation_shouldThrowNotFound_whenAssociationCannotBeRestored() {

        when(organizationSupplierRepository.restoreById(999L))
                .thenReturn(0);

        assertThatThrownBy(
                () -> organizationSupplierService.restoreAssociation(999L)
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(organizationSupplierRepository, never()).findById(anyLong());
        verify(organizationSupplierMapper, never()).toResponse(any());
    }
}
