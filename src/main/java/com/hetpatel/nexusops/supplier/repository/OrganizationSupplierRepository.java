package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.supplier.entity.OrganizationSupplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OrganizationSupplierRepository extends
                                                JpaRepository<OrganizationSupplier, Long>,
                                                JpaSpecificationExecutor<OrganizationSupplier> {

    boolean existsByOrganizationIdAndSupplierId(
            Long organizationId,
            Long supplierId
    );

    Optional<OrganizationSupplier> findByOrganizationIdAndSupplierId(
            Long organizationId,
            Long supplierId
    );

    Page<OrganizationSupplier> findAllByOrganizationId(
            Long organizationId,
            Pageable pageable
    );

    Page<OrganizationSupplier> findAllBySupplierId(
            Long supplierId,
            Pageable pageable
    );

    @Query(value = """
        SELECT deleted
        FROM organization_suppliers
        WHERE organization_id = :organizationId
          AND supplier_id = :supplierId
        """, nativeQuery = true)
    Optional<Boolean> findDeletedStatusByOrganizationIdAndSupplierId(
            @Param("organizationId") Long organizationId,
            @Param("supplierId") Long supplierId
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(
            value = """
                    UPDATE organization_suppliers
                    SET deleted = FALSE,
                        deleted_at = NULL
                    WHERE id = :id
                      AND deleted = TRUE
                    """,
            nativeQuery = true
    )
    int restoreById(@Param("id") Long id);
}
