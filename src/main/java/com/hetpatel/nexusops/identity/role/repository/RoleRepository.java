package com.hetpatel.nexusops.identity.role.repository;

import com.hetpatel.nexusops.identity.role.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByOrganizationIdAndName(Long organizationId, String name);

    Optional<Role> findByOrganizationIdAndName(Long organizationId, String name);

    List<Role> findAllByOrganizationId(Long organizationId);

    List<Role> findAllByOrganizationIdIsNull();

    Optional<Role> findByIdAndOrganizationId(Long id, Long organizationId);

    @Modifying
    @Query(value = """
            UPDATE roles
            SET deleted = false,
                deleted_at = NULL
            WHERE id = :id
              AND deleted = true
            """, nativeQuery = true)
    int restoreById(@Param("id") Long id);
}
