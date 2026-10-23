package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.common.persistence.AuditConfig;

import com.hetpatel.nexusops.supplier.entity.Supplier;
import com.hetpatel.nexusops.supplier.entity.SupplierRegistrationStatus;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(AuditConfig.class)
class SupplierRepositoryTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private EntityManager entityManager;

    private Supplier createSupplier(String supplierCode) {
        Supplier supplier = new Supplier();

        supplier.setLegalName("Test Supplier");
        supplier.setDisplayName("Test");
        supplier.setSupplierCode(supplierCode);
        supplier.setRegistrationStatus(
                SupplierRegistrationStatus.PENDING_REVIEW
        );

        return supplier;
    }

    @Test
    void existsBySupplierCode_shouldReturnTrue_whenSupplierExists() {

        supplierRepository.save(createSupplier("SUP-TEST-001"));

        entityManager.flush();

        boolean exists =
                supplierRepository.existsBySupplierCode("SUP-TEST-001");

        assertThat(exists).isTrue();
    }

    @Test
    void existsBySupplierCode_shouldReturnFalse_whenSupplierDoesNotExist() {

        boolean exists =
                supplierRepository.existsBySupplierCode("SUP-MISSING");

        assertThat(exists).isFalse();
    }

    @Test
    void restoreById_shouldRestoreDeletedSupplier() {

        Supplier savedSupplier =
                supplierRepository.save(createSupplier("SUP-RESTORE-001"));

        Long supplierId = savedSupplier.getId();

        supplierRepository.delete(savedSupplier);

        entityManager.flush();
        entityManager.clear();

        int restoredRows =
                supplierRepository.restoreById(supplierId);

        assertThat(restoredRows).isEqualTo(1);

        entityManager.flush();
        entityManager.clear();

        Supplier restoredSupplier =
                supplierRepository.findById(supplierId).orElseThrow();

        assertThat(restoredSupplier.getId()).isEqualTo(supplierId);
        assertThat(restoredSupplier.getDeleted()).isFalse();
        assertThat(restoredSupplier.getDeletedAt()).isNull();
    }

    @Test
    void restoreById_shouldReturnZero_whenSupplierDoesNotExist() {

        int restoredRows =
                supplierRepository.restoreById(999999L);

        assertThat(restoredRows).isZero();
    }
}
