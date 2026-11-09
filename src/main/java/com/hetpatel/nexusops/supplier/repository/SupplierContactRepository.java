package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.supplier.entity.SupplierContact;
import com.hetpatel.nexusops.supplier.entity.SupplierContactStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.*;

import org.springframework.data.repository.query.Param;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SupplierContactRepository extends JpaRepository<SupplierContact, Long>,
                                                   JpaSpecificationExecutor<SupplierContact> {

    Page<SupplierContact> findAllBySupplier_Id(
            Long supplierId,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sc
        FROM SupplierContact sc
        WHERE sc.id = :id
        """)
    Optional<SupplierContact> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sc
        FROM SupplierContact sc
        WHERE sc.supplier.id = :supplierId
          AND sc.isPrimary = true
          AND sc.status = :status
        """)
    List<SupplierContact> findPrimaryContactsForUpdate(
            @Param("supplierId") Long supplierId,
            @Param("status") SupplierContactStatus status
    );

    @Query(
            value = """
                SELECT EXISTS(
                    SELECT 1
                    FROM supplier_contacts
                    WHERE id = :id
                )
                """,
            nativeQuery = true
    )
    long existsIncludingDeleted(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(
            value = """
                    UPDATE supplier_contacts
                    SET deleted = FALSE,
                        deleted_at = NULL
                    WHERE id = :id
                      AND deleted = TRUE
                    """,
            nativeQuery = true
    )
    int restoreById(@Param("id") Long id);
}
