package com.hetpatel.nexusops.supplier.service.impl;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.BusinessException;
import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.common.validation.PhoneNumberValidator;

import com.hetpatel.nexusops.supplier.dto.request.CreateSupplierContactRequest;
import com.hetpatel.nexusops.supplier.dto.request.UpdateSupplierContactRequest;

import com.hetpatel.nexusops.supplier.dto.response.SupplierContactResponse;

import com.hetpatel.nexusops.supplier.entity.Supplier;
import com.hetpatel.nexusops.supplier.entity.SupplierContact;
import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import com.hetpatel.nexusops.supplier.mapper.SupplierContactMapper;

import com.hetpatel.nexusops.supplier.repository.SupplierContactRepository;
import com.hetpatel.nexusops.supplier.repository.SupplierRepository;

import com.hetpatel.nexusops.supplier.service.SupplierContactService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplierContactServiceImpl implements SupplierContactService {

    private final SupplierContactRepository supplierContactRepository;
    private final SupplierContactMapper supplierContactMapper;

    private final SupplierRepository supplierRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Override
    @Transactional
    public SupplierContactResponse createSupplierContact(CreateSupplierContactRequest request) {

        if (request == null) {
            throw new BusinessException("Request must not be null.");
        }

        Long supplierId = request.getSupplierId();

        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException("Supplier Id must be a positive value.");
        }

        boolean isPrimary = Boolean.TRUE.equals(request.getIsPrimary());

        Supplier supplier;

        if (isPrimary) {
            supplier =
                    supplierRepository
                            .findByIdForUpdate(supplierId)
                            .orElseThrow(() -> {
                                log.warn(
                                        "Supplier contact creation failed: supplier not found, supplierId={}",
                                        supplierId
                                );

                                return new ResourceNotFoundException(
                                        "Supplier not found with id: " + supplierId
                                );
                            });
        } else {
            supplier =
                    supplierRepository
                            .findById(supplierId)
                            .orElseThrow(() -> {
                                log.warn(
                                        "Supplier contact creation failed: supplier not found, supplierId={}",
                                        supplierId
                                );

                                return new ResourceNotFoundException(
                                        "Supplier not found with id: " + supplierId
                                );
                            });
        }

        boolean hasEmail = hasText(request.getEmail());
        boolean hasPhoneNumber = hasText(request.getPhoneNumber());
        boolean hasAlternatePhoneNumber = hasText(request.getAlternatePhoneNumber());

        validateContactMethods(
                request,
                hasEmail,
                hasPhoneNumber,
                hasAlternatePhoneNumber
        );

        PhoneNumberValidator.NormalizedPhoneNumber normalizedPhone = null;
        PhoneNumberValidator.NormalizedPhoneNumber normalizedAlternatePhone = null;

        if (hasPhoneNumber) {
            normalizedPhone = phoneNumberValidator.validateAndNormalize(
                    request.getPhoneCountryCode(),
                    request.getPhoneNumber(),
                    "Phone number"
            );
        }

        if (hasAlternatePhoneNumber) {
            normalizedAlternatePhone = phoneNumberValidator.validateAndNormalize(
                    request.getAlternatePhoneCountryCode(),
                    request.getAlternatePhoneNumber(),
                    "Alternate phone number"
            );
        }

        if (isPrimary
                && !supplierContactRepository.findPrimaryContactsForUpdate(
                supplierId,
                SupplierContactStatus.ACTIVE
        ).isEmpty()) {

            log.warn(
                    "Supplier contact creation failed: active primary contact already exists, supplierId={}",
                    supplierId
            );

            throw new ConflictException(
                    "An active primary contact already exists for this supplier."
            );
        }

        SupplierContact supplierContact = supplierContactMapper.toEntity(request);
        supplierContact.setSupplier(supplier);

        if (normalizedPhone != null) {
            supplierContact.setPhoneCountryCode(normalizedPhone.countryCode());
            supplierContact.setPhoneNumber(normalizedPhone.phoneNumber());
        }

        if (normalizedAlternatePhone != null) {
            supplierContact.setAlternatePhoneCountryCode(
                    normalizedAlternatePhone.countryCode()
            );
            supplierContact.setAlternatePhoneNumber(
                    normalizedAlternatePhone.phoneNumber()
            );
        }

        supplierContact.setIsPrimary(isPrimary);

        SupplierContact savedContact = supplierContactRepository.save(supplierContact);

        log.info(
                "Supplier contact created successfully: contactId={}, supplierId={}",
                savedContact.getId(),
                savedContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(savedContact);
    }

    @Override
    public SupplierContactResponse getSupplierContactById(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    "Supplier contact Id must be a positive value."
            );
        }

        SupplierContact supplierContact =
                supplierContactRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier contact retrieval failed: contact not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier contact not found with id: " + id
                            );
                        });

        log.info(
                "Supplier contact retrieved successfully: contactId={}, supplierId={}",
                supplierContact.getId(),
                supplierContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(supplierContact);
    }

    @Override
    public PageResponse<SupplierContactResponse> getSupplierContactsBySupplier(
            Long supplierId,
            int page,
            int size
    ) {

        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException(
                    "Supplier Id must be a positive value."
            );
        }

        if (page < 0) {
            throw new BusinessException(
                    "Page number must not be negative."
            );
        }

        if (size <= 0 || size > 100) {
            throw new BusinessException(
                    "Page size must be between 1 and 100."
            );
        }

        supplierRepository.findById(supplierId)
                .orElseThrow(() -> {
                    log.warn(
                            "Supplier contact retrieval failed: supplier not found, supplierId={}",
                            supplierId
                    );

                    return new ResourceNotFoundException(
                            "Supplier not found with id: " + supplierId
                    );
                });

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<SupplierContact> supplierContactPage =
                supplierContactRepository.findAllBySupplier_Id(
                        supplierId,
                        pageable
                );

        List<SupplierContactResponse> content = supplierContactPage
                .getContent()
                .stream()
                .map(supplierContactMapper::toResponse)
                .toList();

        log.info(
                "Supplier contacts retrieved successfully: supplierId={}, page={}, size={}, totalElements={}",
                supplierId,
                page,
                size,
                supplierContactPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                supplierContactPage.getNumber(),
                supplierContactPage.getSize(),
                supplierContactPage.getTotalElements(),
                supplierContactPage.getTotalPages(),
                supplierContactPage.isLast()
        );
    }

    @Override
    public PageResponse<SupplierContactResponse> searchSupplierContacts(
            Long supplierId,
            SupplierContactType contactType,
            SupplierContactStatus status,
            Boolean isPrimary,
            String search,
            int page,
            int size
    ) {
        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException("Supplier Id must be a positive value.");
        }

        if (page < 0) {
            throw new BusinessException("Page number must not be negative.");
        }

        if (size <= 0 || size > 100) {
            throw new BusinessException("Page size must be between 1 and 100.");
        }

        supplierRepository.findById(supplierId).orElseThrow(() -> {
            log.warn(
                    "Supplier contact search failed: supplier not found, supplierId={}",
                    supplierId
            );

            return new ResourceNotFoundException(
                    "Supplier not found with id: " + supplierId
            );
        });

        Specification<SupplierContact> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("supplier").get("id"),
                                supplierId
                        );

        if (contactType != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("contactType"),
                                    contactType
                            )
            );
        }

        if (status != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    status
                            )
            );
        }

        if (isPrimary != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("isPrimary"),
                                    isPrimary
                            )
            );
        }

        if (search != null && !search.isBlank()) {
            String searchPattern = "%" + search.trim().toLowerCase() + "%";

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.or(
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("firstName")),
                                            searchPattern
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("lastName")),
                                            searchPattern
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("email")),
                                            searchPattern
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("designation")),
                                            searchPattern
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("department")),
                                            searchPattern
                                    )
                            )
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<SupplierContact> contactPage = supplierContactRepository.findAll(specification, pageable);

        List<SupplierContactResponse> content = contactPage
                .getContent()
                .stream()
                .map(supplierContactMapper::toResponse)
                .toList();

        log.info(
                "Supplier contacts searched: supplierId={}, contactType={}, status={}, isPrimary={}, hasSearch={}, page={}, size={}, totalElements={}",
                supplierId,
                contactType,
                status,
                isPrimary,
                search != null && !search.isBlank(),
                page,
                size,
                contactPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                contactPage.getNumber(),
                contactPage.getSize(),
                contactPage.getTotalElements(),
                contactPage.getTotalPages(),
                contactPage.isLast()
        );
    }

    @Override
    @Transactional
    public SupplierContactResponse updateSupplierContact(
            Long id,
            UpdateSupplierContactRequest request
    ) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    "Supplier contact Id must be a positive value."
            );
        }

        if (request == null) {
            throw new BusinessException(
                    "Request must not be null."
            );
        }

        SupplierContact supplierContact =
                supplierContactRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Supplier contact update failed: contact not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier contact not found with id: " + id
                            );
                        });

        supplierContactMapper.updateEntity(request, supplierContact);

        validateContactMethods(supplierContact);

        boolean hasPhoneNumber = hasText(supplierContact.getPhoneNumber());
        boolean hasAlternatePhoneNumber = hasText(
                supplierContact.getAlternatePhoneNumber()
        );

        if (hasPhoneNumber) {
            PhoneNumberValidator.NormalizedPhoneNumber normalizedPhone =
                    phoneNumberValidator.validateAndNormalize(
                            supplierContact.getPhoneCountryCode(),
                            supplierContact.getPhoneNumber(),
                            "Phone number"
                    );

            supplierContact.setPhoneCountryCode(normalizedPhone.countryCode());
            supplierContact.setPhoneNumber(normalizedPhone.phoneNumber());
        }

        if (hasAlternatePhoneNumber) {
            PhoneNumberValidator.NormalizedPhoneNumber normalizedAlternatePhone =
                    phoneNumberValidator.validateAndNormalize(
                            supplierContact.getAlternatePhoneCountryCode(),
                            supplierContact.getAlternatePhoneNumber(),
                            "Alternate phone number"
                    );

            supplierContact.setAlternatePhoneCountryCode(
                    normalizedAlternatePhone.countryCode()
            );

            supplierContact.setAlternatePhoneNumber(
                    normalizedAlternatePhone.phoneNumber()
            );
        }

        SupplierContact updatedContact =
                supplierContactRepository.save(supplierContact);

        log.info(
                "Supplier contact updated successfully: contactId={}, supplierId={}",
                updatedContact.getId(),
                updatedContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(updatedContact);
    }

    @Override
    @Transactional
    public void deleteSupplierContact(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Supplier contact id must be positive.");
        }

        SupplierContact supplierContact = supplierContactRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier contact not found with id: " + id
                ));

        Long supplierId = supplierContact.getSupplier().getId();

        supplierRepository.findByIdForUpdate(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId
                ));

        supplierContact = supplierContactRepository
                .findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier contact not found with id: " + id
                ));

        if (Boolean.TRUE.equals(supplierContact.getIsPrimary())) {
            supplierContact.setIsPrimary(false);
            supplierContactRepository.saveAndFlush(supplierContact);
        }

        supplierContactRepository.delete(supplierContact);

        log.info(
                "Supplier contact deleted successfully: id={}, supplierId={}",
                id,
                supplierContact.getSupplier().getId()
        );
    }

    @Override
    @Transactional
    public SupplierContactResponse restoreSupplierContact(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    "Supplier contact Id must be a positive value."
            );
        }

        int restoredRows = supplierContactRepository.restoreById(id);

        if (restoredRows == 0) {
            log.warn(
                    "Supplier contact restoration failed: contact not found or not deleted, id={}",
                    id
            );

            throw new ResourceNotFoundException(
                    "Deleted supplier contact not found with id: " + id
            );
        }

        SupplierContact restoredContact =
                supplierContactRepository.findById(id)
                        .orElseThrow(() -> {
                            log.error(
                                    "Supplier contact restoration failed after database update, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier contact not found with id: " + id
                            );
                        });

        log.info(
                "Supplier contact restored successfully: contactId={}, supplierId={}",
                restoredContact.getId(),
                restoredContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(restoredContact);
    }

    @Override
    @Transactional
    public SupplierContactResponse setPrimaryContact(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    "Supplier contact Id must be a positive value."
            );
        }

        SupplierContact supplierContact =
                supplierContactRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Setting primary contact failed: supplier contact not found, contactId={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Supplier contact not found with id: " + id
                            );
                        });

        Long supplierId = supplierContact.getSupplier().getId();

        supplierRepository
                .findByIdForUpdate(supplierId)
                .orElseThrow(() -> {
                    log.warn(
                            "Setting primary contact failed: supplier not found, supplierId={}",
                            supplierId
                    );

                    return new ResourceNotFoundException(
                            "Supplier not found with id: " + supplierId
                    );
                });

        supplierContact =
                supplierContactRepository
                        .findByIdForUpdate(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Supplier contact not found with id: " + id
                        ));

        if (supplierContact.getStatus() != SupplierContactStatus.ACTIVE) {
            throw new ConflictException(
                    "Only active supplier contacts can be set as primary."
            );
        }

        if (Boolean.TRUE.equals(supplierContact.getIsPrimary())) {
            log.info(
                    "Supplier contact is already the primary contact: contactId={}, supplierId={}",
                    supplierContact.getId(),
                    supplierContact.getSupplier().getId()
            );

            return supplierContactMapper.toResponse(supplierContact);
        }

        supplierContactRepository.findPrimaryContactsForUpdate(
                supplierId,
                SupplierContactStatus.ACTIVE
        ).forEach(existingPrimaryContact -> {

            existingPrimaryContact.setIsPrimary(false);

            supplierContactRepository.save(existingPrimaryContact);

            log.info(
                    "Previous primary supplier contact unset: contactId={}, supplierId={}",
                    existingPrimaryContact.getId(),
                    existingPrimaryContact.getSupplier().getId()
            );
        });

        supplierContact.setIsPrimary(true);

        SupplierContact primaryContact =
                supplierContactRepository.save(supplierContact);

        log.info(
                "Primary supplier contact set successfully: contactId={}, supplierId={}",
                primaryContact.getId(),
                primaryContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(primaryContact);
    }

    @Override
    @Transactional
    public SupplierContactResponse activateSupplierContact(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    "Supplier contact Id must be a positive value."
            );
        }

        SupplierContact supplierContact = supplierContactRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Supplier contact activation failed: contact not found, contactId={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Supplier contact not found with id: " + id
                    );
                });

        Long supplierId = supplierContact.getSupplier().getId();

        supplierRepository.findByIdForUpdate(supplierId)
                .orElseThrow(() -> {
                    log.warn(
                            "Supplier contact activation failed: supplier not found, supplierId={}",
                            supplierId
                    );

                    return new ResourceNotFoundException(
                            "Supplier not found with id: " + supplierId
                    );
                });

        supplierContact = supplierContactRepository
                .findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier contact not found with id: " + id
                ));

        if (supplierContact.getStatus() == SupplierContactStatus.ACTIVE) {
            return supplierContactMapper.toResponse(supplierContact);
        }

        if (Boolean.TRUE.equals(supplierContact.getIsPrimary())
                && !supplierContactRepository.findPrimaryContactsForUpdate(
                supplierId,
                SupplierContactStatus.ACTIVE
        ).isEmpty()) {

            log.warn(
                    "Supplier contact activation failed: another active primary contact exists, contactId={}, supplierId={}",
                    supplierContact.getId(),
                    supplierId
            );

            throw new ConflictException(
                    "Another active primary contact already exists for this supplier."
            );
        }

        supplierContact.setStatus(SupplierContactStatus.ACTIVE);

        SupplierContact activatedContact =
                supplierContactRepository.save(supplierContact);

        log.info(
                "Supplier contact activated successfully: contactId={}, supplierId={}",
                activatedContact.getId(),
                activatedContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(activatedContact);
    }

    @Override
    @Transactional
    public SupplierContactResponse deactivateSupplierContact(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Supplier contact id must be positive.");
        }

        SupplierContact supplierContact = supplierContactRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier contact not found with id: " + id
                ));

        Long supplierId = supplierContact.getSupplier().getId();

        supplierRepository.findByIdForUpdate(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId
                ));

        supplierContact = supplierContactRepository
                .findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier contact not found with id: " + id
                ));

        if (supplierContact.getStatus() == SupplierContactStatus.INACTIVE) {
            return supplierContactMapper.toResponse(supplierContact);
        }

        if (Boolean.TRUE.equals(supplierContact.getIsPrimary())) {
            supplierContact.setIsPrimary(false);
        }

        supplierContact.setStatus(SupplierContactStatus.INACTIVE);

        SupplierContact deactivatedContact =
                supplierContactRepository.save(supplierContact);

        log.info(
                "Supplier contact deactivated successfully: id={}, supplierId={}",
                deactivatedContact.getId(),
                deactivatedContact.getSupplier().getId()
        );

        return supplierContactMapper.toResponse(deactivatedContact);
    }

    // Helper Method(s)

    private void validateContactMethods(
            CreateSupplierContactRequest request,
            boolean hasEmail,
            boolean hasPhoneNumber,
            boolean hasAlternatePhoneNumber
    ) {

        if (!hasEmail && !hasPhoneNumber && !hasAlternatePhoneNumber) {
            throw new BusinessException(
                    "At least one contact method must be provided."
            );
        }

        boolean hasPhoneCountryCode = hasText(request.getPhoneCountryCode());

        boolean hasAlternatePhoneCountryCode =
                hasText(request.getAlternatePhoneCountryCode());

        if (hasPhoneNumber != hasPhoneCountryCode) {
            throw new BusinessException(
                    "Phone country code and phone number must be provided together."
            );
        }

        if (hasAlternatePhoneNumber != hasAlternatePhoneCountryCode) {
            throw new BusinessException(
                    "Alternate phone country code and phone number must be provided together."
            );
        }
    }

    private void validateContactMethods(SupplierContact supplierContact) {

        boolean hasEmail = hasText(supplierContact.getEmail());
        boolean hasPhoneNumber = hasText(supplierContact.getPhoneNumber());
        boolean hasAlternatePhoneNumber = hasText(
                supplierContact.getAlternatePhoneNumber()
        );

        if (!hasEmail && !hasPhoneNumber && !hasAlternatePhoneNumber) {
            throw new BusinessException(
                    "At least one contact method (email, phone, or alternate phone) is required."
            );
        }

        boolean hasPhoneCountryCode = hasText(
                supplierContact.getPhoneCountryCode()
        );

        boolean hasAlternatePhoneCountryCode = hasText(
                supplierContact.getAlternatePhoneCountryCode()
        );

        if (hasPhoneNumber != hasPhoneCountryCode) {
            throw new BusinessException(
                    "Phone country code and phone number must be provided together."
            );
        }

        if (hasAlternatePhoneNumber != hasAlternatePhoneCountryCode) {
            throw new BusinessException(
                    "Alternate phone country code and phone number must be provided together."
            );
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
