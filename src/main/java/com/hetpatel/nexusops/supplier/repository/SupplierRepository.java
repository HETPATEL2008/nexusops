package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.supplier.entity.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>,
                                            JpaSpecificationExecutor<Supplier> {

    boolean existsBySupplierCode(String supplierCode);

    @Modifying
    @Query(value = """
            UPDATE suppliers
            SET deleted = false,
                deleted_at = NULL
            WHERE id = :id
              AND deleted = true
            """, nativeQuery = true)
    int restoreById(@Param("id") Long id);
}
