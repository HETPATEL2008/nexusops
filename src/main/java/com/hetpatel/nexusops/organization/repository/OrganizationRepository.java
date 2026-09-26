package com.hetpatel.nexusops.organization.repository;

import com.hetpatel.nexusops.organization.entity.Organization;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByCode(String code);

    @Modifying
    @Query(value = """
            UPDATE organizations
            SET deleted = false,
                deleted_at = NULL
            WHERE id = :id
              AND deleted = true
            """, nativeQuery = true)
    int restoreById(@Param("id") Long id);
}
