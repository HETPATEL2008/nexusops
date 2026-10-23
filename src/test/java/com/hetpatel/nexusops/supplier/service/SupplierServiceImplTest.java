package com.hetpatel.nexusops.supplier.service;

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

import com.hetpatel.nexusops.supplier.service.impl.SupplierServiceImpl;

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

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;
    private SupplierResponse supplierResponse;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();

        supplier.setId(1L);
        supplier.setLegalName("Test Supplier");
        supplier.setDisplayName("Test");
        supplier.setSupplierCode("SUP-TEST-001");
        supplier.setRegistrationStatus(
                SupplierRegistrationStatus.PENDING_REVIEW
        );

        supplierResponse = new SupplierResponse();

        supplierResponse.setId(1L);
        supplierResponse.setLegalName("Test Supplier");
        supplierResponse.setDisplayName("Test");
        supplierResponse.setSupplierCode("SUP-TEST-001");
        supplierResponse.setRegistrationStatus(
                SupplierRegistrationStatus.PENDING_REVIEW
        );
    }

    // CREATE

    @Test
    void createSupplier_shouldCreateSupplierSuccessfully() {
        CreateSupplierRequest request = new CreateSupplierRequest();
        request.setLegalName("Test Supplier");
        request.setDisplayName("Test");
        request.setSupplierCode("SUP-TEST-001");

        when(supplierRepository.existsBySupplierCode("SUP-TEST-001"))
                .thenReturn(false);
        when(supplierMapper.toEntity(request)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier)).thenReturn(supplierResponse);

        SupplierResponse result = supplierService.createSupplier(request);

        assertThat(result).isSameAs(supplierResponse);
        assertThat(supplier.getRegistrationStatus())
                .isEqualTo(SupplierRegistrationStatus.PENDING_REVIEW);

        verify(supplierRepository).existsBySupplierCode("SUP-TEST-001");
        verify(supplierMapper).toEntity(request);
        verify(supplierRepository).save(supplier);
        verify(supplierMapper).toResponse(supplier);
    }

    @Test
    void createSupplier_shouldThrowConflict_whenSupplierCodeAlreadyExists() {
        CreateSupplierRequest request = new CreateSupplierRequest();
        request.setLegalName("Test Supplier");
        request.setSupplierCode("SUP-TEST-001");

        when(supplierRepository.existsBySupplierCode("SUP-TEST-001"))
                .thenReturn(true);

        assertThatThrownBy(() -> supplierService.createSupplier(request))
                .isInstanceOf(ConflictException.class);

        verify(supplierMapper, never()).toEntity(any());
        verify(supplierRepository, never()).save(any());
    }

    // GET BY ID

    @Test
    void getSupplierById_shouldReturnSupplier_whenSupplierExists() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponse(supplier))
                .thenReturn(supplierResponse);

        SupplierResponse result = supplierService.getSupplierById(1L);

        assertThat(result).isSameAs(supplierResponse);

        verify(supplierRepository).findById(1L);
        verify(supplierMapper).toResponse(supplier);
    }

    @Test
    void getSupplierById_shouldThrowNotFound_whenSupplierDoesNotExist() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(supplierMapper, never()).toResponse(any());
    }

    // SEARCH AND PAGINATION

    @Test
    void searchSuppliers_shouldReturnPagedSuppliers() {
        Pageable pageable = PageRequest.of(
                0, 10, Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Supplier> page =
                new PageImpl<>(List.of(supplier), pageable, 1);

        when(supplierRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<Supplier>>any(),
                eq(pageable)
        )).thenReturn(page);

        when(supplierMapper.toResponse(supplier))
                .thenReturn(supplierResponse);

        PageResponse<SupplierResponse> result =
                supplierService.searchSuppliers("Test", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isSameAs(supplierResponse);
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();

        verify(supplierMapper).toResponse(supplier);
    }

    // UPDATE

    @Test
    void updateSupplier_shouldUpdateSuccessfully() {
        UpdateSupplierRequest request = new UpdateSupplierRequest();
        request.setLegalName("Updated Supplier");

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier))
                .thenReturn(supplierResponse);

        SupplierResponse result =
                supplierService.updateSupplier(1L, request);

        assertThat(result).isSameAs(supplierResponse);

        verify(supplierMapper).updateEntity(request, supplier);
        verify(supplierRepository).save(supplier);
        verify(supplierMapper).toResponse(supplier);
    }

    @Test
    void updateSupplier_shouldThrowNotFound_whenSupplierDoesNotExist() {
        UpdateSupplierRequest request = new UpdateSupplierRequest();

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> supplierService.updateSupplier(1L, request)
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierRepository, never()).save(any());
        verify(supplierMapper, never()).updateEntity(any(), any());
    }

    // DELETE

    @Test
    void deleteSupplier_shouldDeleteSuccessfully() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).findById(1L);
        verify(supplierRepository).delete(supplier);
    }

    @Test
    void deleteSupplier_shouldThrowNotFound_whenSupplierDoesNotExist() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.deleteSupplier(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(supplierRepository, never()).delete((Supplier) any());
    }

    // RESTORE

    @Test
    void restoreSupplier_shouldRestoreSuccessfully() {
        when(supplierRepository.restoreById(1L)).thenReturn(1);
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponse(supplier))
                .thenReturn(supplierResponse);

        SupplierResponse result = supplierService.restoreSupplier(1L);

        assertThat(result).isSameAs(supplierResponse);

        verify(supplierRepository).restoreById(1L);
        verify(supplierRepository).findById(1L);
        verify(supplierMapper).toResponse(supplier);
    }

    @Test
    void restoreSupplier_shouldThrowNotFound_whenSupplierCannotBeRestored() {
        when(supplierRepository.restoreById(999L)).thenReturn(0);

        assertThatThrownBy(() -> supplierService.restoreSupplier(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(supplierRepository, never()).findById(anyLong());
        verify(supplierMapper, never()).toResponse(any());
    }

    // VERIFY REGISTRATION

    @Test
    void verifySupplier_shouldVerifyPendingSupplier() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier))
                .thenReturn(supplierResponse);

        SupplierResponse result = supplierService.verifySupplier(1L);

        assertThat(result).isSameAs(supplierResponse);
        assertThat(supplier.getRegistrationStatus())
                .isEqualTo(SupplierRegistrationStatus.VERIFIED);

        verify(supplierRepository).save(supplier);
        verify(supplierMapper).toResponse(supplier);
    }

    @Test
    void verifySupplier_shouldThrowConflict_whenSupplierIsNotPending() {
        supplier.setRegistrationStatus(SupplierRegistrationStatus.REJECTED);

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));

        assertThatThrownBy(() -> supplierService.verifySupplier(1L))
                .isInstanceOf(ConflictException.class);

        verify(supplierRepository, never()).save(any());
        verify(supplierMapper, never()).toResponse(any());
    }

    @Test
    void verifySupplier_shouldThrowNotFound_whenSupplierDoesNotExist() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.verifySupplier(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(supplierRepository, never()).save(any());
    }

    // REJECT REGISTRATION

    @Test
    void rejectSupplier_shouldRejectPendingSupplier() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier))
                .thenReturn(supplierResponse);

        SupplierResponse result = supplierService.rejectSupplier(1L);

        assertThat(result).isSameAs(supplierResponse);
        assertThat(supplier.getRegistrationStatus())
                .isEqualTo(SupplierRegistrationStatus.REJECTED);

        verify(supplierRepository).save(supplier);
        verify(supplierMapper).toResponse(supplier);
    }

    @Test
    void rejectSupplier_shouldThrowConflict_whenSupplierIsNotPending() {
        supplier.setRegistrationStatus(SupplierRegistrationStatus.VERIFIED);

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));

        assertThatThrownBy(() -> supplierService.rejectSupplier(1L))
                .isInstanceOf(ConflictException.class);

        verify(supplierRepository, never()).save(any());
        verify(supplierMapper, never()).toResponse(any());
    }

    @Test
    void rejectSupplier_shouldThrowNotFound_whenSupplierDoesNotExist() {
        when(supplierRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.rejectSupplier(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(supplierRepository, never()).save(any());
    }
}
