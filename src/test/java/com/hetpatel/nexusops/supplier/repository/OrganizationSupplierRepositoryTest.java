package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.common.persistence.AuditConfig;
import com.hetpatel.nexusops.supplier.entity.OrganizationSupplier;
import com.hetpatel.nexusops.supplier.entity.OrganizationSupplierStatus;
import com.hetpatel.nexusops.supplier.entity.SupplierRiskLevel;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(AuditConfig.class)
class OrganizationSupplierRepositoryTest {

    @Autowired
    private OrganizationSupplierRepository organizationSupplierRepository;

    @Autowired
    private EntityManager entityManager;

    private Long organizationId;
    private Long supplierId;

    @BeforeEach
    void setUp() {

        entityManager.createNativeQuery("""
                INSERT INTO organizations (
                    name,
                    code,
                    email,
                    status,
                    created_at,
                    created_by,
                    updated_at,
                    last_updated_by,
                    deleted
                )
                VALUES (
                    'Test Organization',
                    'ORG-TEST-001',
                    'org@test.com',
                    'ACTIVE',
                    CURRENT_TIMESTAMP,
                    1,
                    CURRENT_TIMESTAMP,
                    1,
                    FALSE
                )
                """).executeUpdate();

        organizationId = ((Number) entityManager.createNativeQuery("""
                SELECT id
                FROM organizations
                WHERE code = 'ORG-TEST-001'
                """).getSingleResult()).longValue();

        entityManager.createNativeQuery("""
                INSERT INTO suppliers (
                    legal_name,
                    display_name,
                    supplier_code,
                    registration_status,
                    created_at,
                    created_by,
                    updated_at,
                    last_updated_by,
                    deleted
                )
                VALUES (
                    'Test Supplier',
                    'Test',
                    'SUP-TEST-001',
                    'VERIFIED',
                    CURRENT_TIMESTAMP,
                    1,
                    CURRENT_TIMESTAMP,
                    1,
                    FALSE
                )
                """).executeUpdate();

        supplierId = ((Number) entityManager.createNativeQuery("""
                SELECT id
                FROM suppliers
                WHERE supplier_code = 'SUP-TEST-001'
                """).getSingleResult()).longValue();

        entityManager.flush();
        entityManager.clear();
    }

    private OrganizationSupplier createAssociation() {

        OrganizationSupplier association = new OrganizationSupplier();

        association.setOrganizationId(organizationId);
        association.setSupplierId(supplierId);
        association.setStatus(OrganizationSupplierStatus.PENDING);
        association.setRiskLevel(SupplierRiskLevel.LOW);
        association.setNotes("Test association");

        return association;
    }

    @Test
    void save_shouldPersistAssociationSuccessfully() {

        OrganizationSupplier savedAssociation =
                organizationSupplierRepository.save(createAssociation());

        entityManager.flush();
        entityManager.clear();

        OrganizationSupplier result =
                organizationSupplierRepository
                        .findById(savedAssociation.getId())
                        .orElseThrow();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getOrganizationId()).isEqualTo(organizationId);
        assertThat(result.getSupplierId()).isEqualTo(supplierId);
        assertThat(result.getStatus())
                .isEqualTo(OrganizationSupplierStatus.PENDING);
        assertThat(result.getRiskLevel())
                .isEqualTo(SupplierRiskLevel.LOW);
        assertThat(result.getNotes()).isEqualTo("Test association");
    }

    @Test
    void existsByOrganizationIdAndSupplierId_shouldReturnTrue_whenAssociationExists() {

        organizationSupplierRepository.save(createAssociation());

        entityManager.flush();

        boolean exists =
                organizationSupplierRepository
                        .existsByOrganizationIdAndSupplierId(
                                organizationId,
                                supplierId
                        );

        assertThat(exists).isTrue();
    }

    @Test
    void existsByOrganizationIdAndSupplierId_shouldReturnFalse_whenAssociationDoesNotExist() {

        boolean exists =
                organizationSupplierRepository
                        .existsByOrganizationIdAndSupplierId(
                                organizationId,
                                999999L
                        );

        assertThat(exists).isFalse();
    }

    @Test
    void findByOrganizationIdAndSupplierId_shouldReturnAssociation_whenAssociationExists() {

        OrganizationSupplier savedAssociation =
                organizationSupplierRepository.save(createAssociation());

        entityManager.flush();
        entityManager.clear();

        Optional<OrganizationSupplier> result =
                organizationSupplierRepository
                        .findByOrganizationIdAndSupplierId(
                                organizationId,
                                supplierId
                        );

        assertThat(result).isPresent();
        assertThat(result.get().getId())
                .isEqualTo(savedAssociation.getId());
    }

    @Test
    void findByOrganizationIdAndSupplierId_shouldReturnEmpty_whenAssociationDoesNotExist() {

        Optional<OrganizationSupplier> result =
                organizationSupplierRepository
                        .findByOrganizationIdAndSupplierId(
                                organizationId,
                                999999L
                        );

        assertThat(result).isEmpty();
    }

    @Test
    void findDeletedStatusByOrganizationIdAndSupplierId_shouldReturnFalse_whenAssociationIsActive() {

        organizationSupplierRepository.save(createAssociation());

        entityManager.flush();
        entityManager.clear();

        Optional<Boolean> result =
                organizationSupplierRepository
                        .findDeletedStatusByOrganizationIdAndSupplierId(
                                organizationId,
                                supplierId
                        );

        assertThat(result).contains(false);
    }

    @Test
    void findDeletedStatusByOrganizationIdAndSupplierId_shouldReturnTrue_whenAssociationIsDeleted() {

        OrganizationSupplier association =
                organizationSupplierRepository.save(createAssociation());

        entityManager.flush();

        organizationSupplierRepository.delete(association);

        entityManager.flush();
        entityManager.clear();

        Optional<Boolean> result =
                organizationSupplierRepository
                        .findDeletedStatusByOrganizationIdAndSupplierId(
                                organizationId,
                                supplierId
                        );

        assertThat(result).contains(true);
    }

    @Test
    void findDeletedStatusByOrganizationIdAndSupplierId_shouldReturnEmpty_whenAssociationDoesNotExist() {

        Optional<Boolean> result =
                organizationSupplierRepository
                        .findDeletedStatusByOrganizationIdAndSupplierId(
                                organizationId,
                                999999L
                        );

        assertThat(result).isEmpty();
    }

    @Test
    void restoreById_shouldRestoreDeletedAssociation() {

        OrganizationSupplier association =
                organizationSupplierRepository.save(createAssociation());

        Long associationId = association.getId();

        entityManager.flush();

        organizationSupplierRepository.delete(association);

        entityManager.flush();
        entityManager.clear();

        int restoredRows =
                organizationSupplierRepository.restoreById(associationId);

        assertThat(restoredRows).isEqualTo(1);

        entityManager.flush();
        entityManager.clear();

        OrganizationSupplier restoredAssociation =
                organizationSupplierRepository
                        .findById(associationId)
                        .orElseThrow();

        assertThat(restoredAssociation.getDeleted()).isFalse();
        assertThat(restoredAssociation.getDeletedAt()).isNull();
    }

    @Test
    void restoreById_shouldReturnZero_whenAssociationDoesNotExist() {

        int restoredRows =
                organizationSupplierRepository.restoreById(999999L);

        assertThat(restoredRows).isZero();
    }

    @Test
    void findById_shouldNotReturnSoftDeletedAssociation() {

        OrganizationSupplier association =
                organizationSupplierRepository.save(createAssociation());

        Long associationId = association.getId();

        entityManager.flush();

        organizationSupplierRepository.delete(association);

        entityManager.flush();
        entityManager.clear();

        Optional<OrganizationSupplier> result =
                organizationSupplierRepository.findById(associationId);

        assertThat(result).isEmpty();
    }
}
