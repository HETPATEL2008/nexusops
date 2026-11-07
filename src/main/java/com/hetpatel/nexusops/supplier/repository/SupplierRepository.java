package com.hetpatel.nexusops.supplier.repository;

import com.hetpatel.nexusops.supplier.entity.Supplier;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>,
                                            JpaSpecificationExecutor<Supplier> {

    boolean existsBySupplierCode(String supplierCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Supplier s WHERE s.id = :id")
    Optional<Supplier> findByIdForUpdate(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE suppliers
            SET deleted = false,
                deleted_at = NULL
            WHERE id = :id
              AND deleted = true
            """, nativeQuery = true)
    int restoreById(@Param("id") Long id);
}
