package com.hetpatel.nexusops.supplier.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.BusinessException;
import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.common.validation.PhoneNumberValidator;

import com.hetpatel.nexusops.supplier.entity.Supplier;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierContactRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierContactRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierContactResponse;

import com.hetpatel.nexusops.supplier.entity.SupplierContact;
import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import com.hetpatel.nexusops.supplier.mapper.SupplierContactMapper;

import com.hetpatel.nexusops.supplier.repository.SupplierContactRepository;
import com.hetpatel.nexusops.supplier.repository.SupplierRepository;

import com.hetpatel.nexusops.supplier.service.impl.SupplierContactServiceImpl;

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

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierContactServiceImplTest {

    @Mock
    private SupplierContactRepository supplierContactRepository;

    @Mock
    private SupplierContactMapper supplierContactMapper;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @InjectMocks
    private SupplierContactServiceImpl supplierContactService;

    private SupplierContact contact;
    private SupplierContactResponse contactResponse;

    private static final Long SUPPLIER_ID = 1L;
    private static final Long CONTACT_ID = 2L;

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john@example.com";
    private static final String COUNTRY_CODE = "+91";
    private static final String PHONE_NUMBER = "9876543210";

    @BeforeEach
    void setUp() {

        contact = new SupplierContact();

        contact.setId(CONTACT_ID);
        contact.setSupplierId(SUPPLIER_ID);
        contact.setFirstName(FIRST_NAME);
        contact.setLastName(LAST_NAME);
        contact.setEmail(EMAIL);
        contact.setPhoneCountryCode(COUNTRY_CODE);
        contact.setPhoneNumber(PHONE_NUMBER);
        contact.setContactType(SupplierContactType.GENERAL);
        contact.setIsPrimary(false);
        contact.setStatus(SupplierContactStatus.ACTIVE);

        contactResponse = new SupplierContactResponse();

        contactResponse.setId(CONTACT_ID);
        contactResponse.setSupplierId(SUPPLIER_ID);
        contactResponse.setFirstName(FIRST_NAME);
        contactResponse.setLastName(LAST_NAME);
        contactResponse.setEmail(EMAIL);
        contactResponse.setPhoneCountryCode(COUNTRY_CODE);
        contactResponse.setPhoneNumber(PHONE_NUMBER);
        contactResponse.setContactType(SupplierContactType.GENERAL);
        contactResponse.setIsPrimary(false);
        contactResponse.setStatus(SupplierContactStatus.ACTIVE);
    }

    // CREATE

    @Test
    void createSupplierContact_shouldCreateSuccessfully() {

        CreateSupplierContactRequest request =
                new CreateSupplierContactRequest();

        request.setSupplierId(SUPPLIER_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setEmail(EMAIL);
        request.setPhoneCountryCode(COUNTRY_CODE);
        request.setPhoneNumber(PHONE_NUMBER);
        request.setContactType(SupplierContactType.GENERAL);
        request.setIsPrimary(false);

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(new com.hetpatel.nexusops.supplier.entity.Supplier()));

        when(phoneNumberValidator.validateAndNormalize(
                COUNTRY_CODE, PHONE_NUMBER, "Phone number"
        )).thenReturn(new PhoneNumberValidator.NormalizedPhoneNumber(
                COUNTRY_CODE, PHONE_NUMBER
        ));

        when(supplierContactMapper.toEntity(request))
                .thenReturn(contact);

        when(supplierContactRepository.save(contact))
                .thenReturn(contact);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.createSupplierContact(request);

        assertThat(result).isSameAs(contactResponse);

        verify(supplierRepository).findById(SUPPLIER_ID);
        verify(supplierContactRepository).save(contact);
        verify(supplierContactMapper).toResponse(contact);
    }

    @Test
    void createSupplierContact_shouldThrowBusinessException_whenRequestIsNull() {

        assertThatThrownBy(
                () -> supplierContactService.createSupplierContact(null)
        ).isInstanceOf(BusinessException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    @Test
    void createSupplierContact_shouldThrowBusinessException_whenSupplierIdIsInvalid() {

        CreateSupplierContactRequest request =
                new CreateSupplierContactRequest();

        request.setSupplierId(0L);

        assertThatThrownBy(
                () -> supplierContactService.createSupplierContact(request)
        ).isInstanceOf(BusinessException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    @Test
    void createSupplierContact_shouldThrowNotFound_whenSupplierDoesNotExist() {

        CreateSupplierContactRequest request =
                new CreateSupplierContactRequest();

        request.setSupplierId(SUPPLIER_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setEmail(EMAIL);
        request.setContactType(SupplierContactType.GENERAL);

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> supplierContactService.createSupplierContact(request)
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    @Test
    void createSupplierContact_shouldThrowBusinessException_whenNoContactMethodProvided() {

        CreateSupplierContactRequest request =
                new CreateSupplierContactRequest();

        request.setSupplierId(SUPPLIER_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setContactType(SupplierContactType.GENERAL);

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        assertThatThrownBy(
                () -> supplierContactService.createSupplierContact(request)
        ).isInstanceOf(BusinessException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    @Test
    void createSupplierContact_shouldThrowConflict_whenPrimaryContactAlreadyExists() {

        CreateSupplierContactRequest request =
                new CreateSupplierContactRequest();

        request.setSupplierId(SUPPLIER_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setEmail(EMAIL);
        request.setContactType(SupplierContactType.GENERAL);
        request.setIsPrimary(true);

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findPrimaryContactsForUpdate(
                SUPPLIER_ID, SupplierContactStatus.ACTIVE
        )).thenReturn(List.of(contact));

        assertThatThrownBy(
                () -> supplierContactService.createSupplierContact(request)
        ).isInstanceOf(ConflictException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    // GET BY ID

    @Test
    void getSupplierContactById_shouldReturnContact_whenContactExists() {

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.getSupplierContactById(CONTACT_ID);

        assertThat(result).isSameAs(contactResponse);

        verify(supplierContactRepository).findById(CONTACT_ID);
        verify(supplierContactMapper).toResponse(contact);
    }

    @Test
    void getSupplierContactById_shouldThrowNotFound_whenContactDoesNotExist() {

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> supplierContactService.getSupplierContactById(CONTACT_ID)
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierContactMapper, never()).toResponse(any());
    }

    // GET CONTACTS BY SUPPLIER

    @Test
    void getSupplierContactsBySupplier_shouldReturnPagedContacts() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<SupplierContact> page =
                new PageImpl<>(List.of(contact), pageable, 1);

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findAllBySupplierId(
                SUPPLIER_ID, pageable
        )).thenReturn(page);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        PageResponse<SupplierContactResponse> result =
                supplierContactService.getSupplierContactsBySupplier(
                        SUPPLIER_ID, 0, 10
                );

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0)).isSameAs(contactResponse);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.last()).isTrue();

        verify(supplierRepository).findById(SUPPLIER_ID);
        verify(supplierContactRepository)
                .findAllBySupplierId(SUPPLIER_ID, pageable);
    }

    @Test
    void getSupplierContactsBySupplier_shouldThrowNotFound_whenSupplierDoesNotExist() {

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> supplierContactService.getSupplierContactsBySupplier(
                        SUPPLIER_ID, 0, 10
                )
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierContactRepository, never())
                .findAllBySupplierId(anyLong(), any(Pageable.class));
    }

    // SEARCH CONTACTS

    @Test
    void searchSupplierContacts_shouldReturnPagedContacts() {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<SupplierContact> page =
                new PageImpl<>(List.of(contact), pageable, 1);

        when(supplierRepository.findById(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(page);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        PageResponse<SupplierContactResponse> result =
                supplierContactService.searchSupplierContacts(
                        SUPPLIER_ID,
                        null,
                        null,
                        null,
                        null,
                        0,
                        10
                );

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0)).isSameAs(contactResponse);

        verify(supplierRepository).findById(SUPPLIER_ID);
        verify(supplierContactRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        );
    }

    // UPDATE

    @Test
    void updateSupplierContact_shouldUpdateSuccessfully() {

        UpdateSupplierContactRequest request =
                new UpdateSupplierContactRequest();

        request.setFirstName("Updated");
        request.setLastName(LAST_NAME);
        request.setEmail(EMAIL);
        request.setPhoneCountryCode(COUNTRY_CODE);
        request.setPhoneNumber(PHONE_NUMBER);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(phoneNumberValidator.validateAndNormalize(
                COUNTRY_CODE, PHONE_NUMBER, "Phone number"
        )).thenReturn(new PhoneNumberValidator.NormalizedPhoneNumber(
                COUNTRY_CODE, PHONE_NUMBER
        ));

        when(supplierContactRepository.save(contact))
                .thenReturn(contact);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.updateSupplierContact(
                        CONTACT_ID, request
                );

        assertThat(result).isSameAs(contactResponse);

        verify(supplierContactMapper).updateEntity(request, contact);
        verify(supplierContactRepository).save(contact);
    }

    @Test
    void updateSupplierContact_shouldThrowNotFound_whenContactDoesNotExist() {

        UpdateSupplierContactRequest request =
                new UpdateSupplierContactRequest();

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> supplierContactService.updateSupplierContact(
                        CONTACT_ID, request
                )
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierContactRepository, never()).save(any());
        verify(supplierContactMapper, never()).updateEntity(any(), any());
    }

    // DELETE

    @Test
    void deleteSupplierContact_shouldDeleteSuccessfully() {

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        supplierContactService.deleteSupplierContact(CONTACT_ID);

        verify(supplierContactRepository).delete(contact);
    }

    @Test
    void deleteSupplierContact_shouldThrowNotFound_whenContactDoesNotExist() {

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> supplierContactService.deleteSupplierContact(CONTACT_ID)
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierContactRepository, never()).delete(any(SupplierContact.class));
    }

    // RESTORE

    @Test
    void restoreSupplierContact_shouldRestoreSuccessfully() {

        when(supplierContactRepository.restoreById(CONTACT_ID))
                .thenReturn(1);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.restoreSupplierContact(CONTACT_ID);

        assertThat(result).isSameAs(contactResponse);

        verify(supplierContactRepository).restoreById(CONTACT_ID);
        verify(supplierContactRepository).findById(CONTACT_ID);
        verify(supplierContactMapper).toResponse(contact);
    }

    @Test
    void restoreSupplierContact_shouldThrowNotFound_whenContactCannotBeRestored() {

        when(supplierContactRepository.restoreById(999L))
                .thenReturn(0);

        assertThatThrownBy(
                () -> supplierContactService.restoreSupplierContact(999L)
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(supplierContactRepository, never()).findById(anyLong());
        verify(supplierContactMapper, never()).toResponse(any());
    }

    // SET PRIMARY

    @Test
    void setPrimaryContact_shouldSetPrimarySuccessfully() {

        contact.setIsPrimary(false);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactRepository.findPrimaryContactsForUpdate(
                SUPPLIER_ID, SupplierContactStatus.ACTIVE
        )).thenReturn(List.of());

        when(supplierContactRepository.save(contact))
                .thenReturn(contact);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.setPrimaryContact(CONTACT_ID);

        assertThat(result).isSameAs(contactResponse);
        assertThat(contact.getIsPrimary()).isTrue();

        verify(supplierContactRepository).save(contact);
    }

    @Test
    void setPrimaryContact_shouldThrowConflict_whenContactIsInactive() {

        contact.setStatus(SupplierContactStatus.INACTIVE);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        assertThatThrownBy(
                () -> supplierContactService.setPrimaryContact(CONTACT_ID)
        ).isInstanceOf(ConflictException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    // ACTIVATE

    @Test
    void activateSupplierContact_shouldActivateSuccessfully() {

        contact.setStatus(SupplierContactStatus.INACTIVE);
        contact.setIsPrimary(false);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactRepository.save(contact))
                .thenReturn(contact);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.activateSupplierContact(CONTACT_ID);

        assertThat(result).isSameAs(contactResponse);
        assertThat(contact.getStatus())
                .isEqualTo(SupplierContactStatus.ACTIVE);

        verify(supplierContactRepository).save(contact);
    }

    @Test
    void activateSupplierContact_shouldThrowConflict_whenAnotherPrimaryExists() {

        contact.setStatus(SupplierContactStatus.INACTIVE);
        contact.setIsPrimary(true);

        SupplierContact existingPrimary = new SupplierContact();
        existingPrimary.setId(99L);
        existingPrimary.setSupplierId(SUPPLIER_ID);
        existingPrimary.setIsPrimary(true);
        existingPrimary.setStatus(SupplierContactStatus.ACTIVE);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactRepository.findPrimaryContactsForUpdate(
                SUPPLIER_ID, SupplierContactStatus.ACTIVE
        )).thenReturn(List.of(existingPrimary));

        assertThatThrownBy(
                () -> supplierContactService.activateSupplierContact(CONTACT_ID)
        ).isInstanceOf(ConflictException.class);

        verify(supplierContactRepository, never()).save(any());
    }

    // DEACTIVATE

    @Test
    void deactivateSupplierContact_shouldDeactivateSuccessfully() {

        contact.setStatus(SupplierContactStatus.ACTIVE);
        contact.setIsPrimary(false);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactRepository.save(contact))
                .thenReturn(contact);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        SupplierContactResponse result =
                supplierContactService.deactivateSupplierContact(CONTACT_ID);

        assertThat(result).isSameAs(contactResponse);
        assertThat(contact.getStatus())
                .isEqualTo(SupplierContactStatus.INACTIVE);
        assertThat(contact.getIsPrimary()).isFalse();
    }

    @Test
    void deactivateSupplierContact_shouldClearPrimary_whenContactIsPrimary() {

        contact.setStatus(SupplierContactStatus.ACTIVE);
        contact.setIsPrimary(true);

        when(supplierContactRepository.findById(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierRepository.findByIdForUpdate(SUPPLIER_ID))
                .thenReturn(Optional.of(new Supplier()));

        when(supplierContactRepository.findByIdForUpdate(CONTACT_ID))
                .thenReturn(Optional.of(contact));

        when(supplierContactRepository.save(contact))
                .thenReturn(contact);

        when(supplierContactMapper.toResponse(contact))
                .thenReturn(contactResponse);

        supplierContactService.deactivateSupplierContact(CONTACT_ID);

        assertThat(contact.getIsPrimary()).isFalse();
        assertThat(contact.getStatus())
                .isEqualTo(SupplierContactStatus.INACTIVE);
    }
}
