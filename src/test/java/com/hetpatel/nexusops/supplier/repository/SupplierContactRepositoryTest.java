package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.common.persistence.AuditConfig;
import com.hetpatel.nexusops.supplier.entity.Supplier;
import com.hetpatel.nexusops.supplier.entity.SupplierContact;
import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierContactType;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditConfig.class)
class SupplierContactRepositoryTest {

    @Autowired
    private SupplierContactRepository supplierContactRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long supplierId;
    private Supplier supplier;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update(
                """
                INSERT INTO suppliers (
                    legal_name,
                    supplier_code,
                    registration_status,
                    created_at,
                    created_by,
                    updated_at,
                    last_updated_by,
                    deleted
                )
                VALUES (?, ?, ?, NOW(), ?, NOW(), ?, FALSE)
                """,
                "Test Supplier",
                "TEST-SUP-001",
                "PENDING_REVIEW",
                1L,
                1L
        );

        supplierId = jdbcTemplate.queryForObject(
                "SELECT id FROM suppliers WHERE supplier_code = ?",
                Long.class,
                "TEST-SUP-001"
        );

        supplier = entityManager.find(Supplier.class, supplierId);
    }

    private SupplierContact createContact(
            String firstName,
            String email,
            SupplierContactStatus status,
            boolean isPrimary
    ) {

        SupplierContact contact = new SupplierContact();

        contact.setSupplier(supplier);
        contact.setFirstName(firstName);
        contact.setLastName("Patel");
        contact.setEmail(email);
        contact.setContactType(SupplierContactType.GENERAL);
        contact.setStatus(status);
        contact.setIsPrimary(isPrimary);

        return contact;
    }

    @Test
    void save_ShouldPersistSupplierContact() {

        SupplierContact contact = createContact(
                "Het",
                "het@example.com",
                SupplierContactStatus.ACTIVE,
                true
        );

        SupplierContact savedContact =
                supplierContactRepository.saveAndFlush(contact);

        assertThat(savedContact.getId()).isNotNull();
        assertThat(savedContact.getSupplier().getId()).isEqualTo(supplierId);
        assertThat(savedContact.getFirstName()).isEqualTo("Het");
        assertThat(savedContact.getEmail()).isEqualTo("het@example.com");
        assertThat(savedContact.getContactType())
                .isEqualTo(SupplierContactType.GENERAL);
        assertThat(savedContact.getStatus())
                .isEqualTo(SupplierContactStatus.ACTIVE);
        assertThat(savedContact.getIsPrimary()).isTrue();
        assertThat(savedContact.getDeleted()).isFalse();
    }

    @Test
    void findById_ShouldReturnSupplierContact() {

        SupplierContact savedContact = supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        );

        Optional<SupplierContact> result =
                supplierContactRepository.findById(savedContact.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Het");
    }

    @Test
    void findAllBySupplier_Id_ShouldReturnPaginatedContacts() {

        supplierContactRepository.saveAll(List.of(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                ),
                createContact(
                        "Amit",
                        "amit@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                ),
                createContact(
                        "Rahul",
                        "rahul@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        ));

        Page<SupplierContact> result =
                supplierContactRepository.findAllBySupplier_Id(
                        supplierId,
                        PageRequest.of(
                                0,
                                2,
                                Sort.by(Sort.Direction.DESC, "id")
                        )
                );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByIdForUpdate_ShouldReturnSupplierContact() {

        SupplierContact savedContact = supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        );

        Optional<SupplierContact> result =
                supplierContactRepository.findByIdForUpdate(
                        savedContact.getId()
                );

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedContact.getId());
    }

    @Test
    void findPrimaryContactsForUpdate_ShouldReturnActivePrimaryContacts() {

        supplierContactRepository.saveAllAndFlush(List.of(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        true
                ),
                createContact(
                        "Amit",
                        "amit@example.com",
                        SupplierContactStatus.ACTIVE,
                        true
                ),
                createContact(
                        "Rahul",
                        "rahul@example.com",
                        SupplierContactStatus.INACTIVE,
                        true
                )
        ));

        List<SupplierContact> result =
                supplierContactRepository.findPrimaryContactsForUpdate(
                        supplierId,
                        SupplierContactStatus.ACTIVE
                );

        assertThat(result).hasSize(2);
        assertThat(result)
                .allMatch(contact ->
                        contact.getStatus() == SupplierContactStatus.ACTIVE
                                && Boolean.TRUE.equals(contact.getIsPrimary())
                );
    }

    @Test
    void findPrimaryContactsForUpdate_ShouldReturnEmptyListWhenNoMatchingContactExists() {

        supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.INACTIVE,
                        true
                )
        );

        List<SupplierContact> result =
                supplierContactRepository.findPrimaryContactsForUpdate(
                        supplierId,
                        SupplierContactStatus.ACTIVE
                );

        assertThat(result).isEmpty();
    }

    @Test
    void delete_ShouldSoftDeleteSupplierContact() {

        SupplierContact savedContact = supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        );

        Long contactId = savedContact.getId();

        supplierContactRepository.delete(savedContact);
        supplierContactRepository.flush();

        entityManager.clear();

        assertThat(supplierContactRepository.findById(contactId)).isEmpty();

        Boolean deleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM supplier_contacts WHERE id = ?",
                Boolean.class,
                contactId
        );

        assertThat(deleted).isTrue();
    }

    @Test
    void existsIncludingDeleted_ShouldReturnTrueForSoftDeletedContact() {

        SupplierContact savedContact = supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        );

        Long contactId = savedContact.getId();

        supplierContactRepository.delete(savedContact);
        supplierContactRepository.flush();

        entityManager.clear();

        assertThat(supplierContactRepository.existsIncludingDeleted(contactId))
                .isEqualTo(1L);
    }

    @Test
    void existsIncludingDeleted_ShouldReturnFalseForNonExistingContact() {

        assertThat(supplierContactRepository.existsIncludingDeleted(999999L))
                .isZero();
    }

    @Test
    void restoreById_ShouldRestoreSoftDeletedContact() {

        SupplierContact savedContact = supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        );

        Long contactId = savedContact.getId();

        supplierContactRepository.delete(savedContact);
        supplierContactRepository.flush();

        entityManager.clear();

        int restoredRows =
                supplierContactRepository.restoreById(contactId);

        assertThat(restoredRows).isEqualTo(1);

        Optional<SupplierContact> restoredContact =
                supplierContactRepository.findById(contactId);

        assertThat(restoredContact).isPresent();
        assertThat(restoredContact.get().getDeleted()).isFalse();
        assertThat(restoredContact.get().getDeletedAt()).isNull();
    }

    @Test
    void restoreById_ShouldReturnZeroWhenContactIsNotDeleted() {

        SupplierContact savedContact = supplierContactRepository.saveAndFlush(
                createContact(
                        "Het",
                        "het@example.com",
                        SupplierContactStatus.ACTIVE,
                        false
                )
        );

        int restoredRows =
                supplierContactRepository.restoreById(savedContact.getId());

        assertThat(restoredRows).isZero();
    }

    @Test
    void restoreById_ShouldReturnZeroWhenContactDoesNotExist() {

        int restoredRows =
                supplierContactRepository.restoreById(999999L);

        assertThat(restoredRows).isZero();
    }
}
