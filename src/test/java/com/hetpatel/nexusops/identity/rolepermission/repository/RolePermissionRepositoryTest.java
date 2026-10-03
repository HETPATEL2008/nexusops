package com.hetpatel.nexusops.identity.rolepermission.repository;

import com.hetpatel.nexusops.identity.rolepermission.entity.RolePermission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RolePermissionRepositoryTest {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update("DELETE FROM role_permissions");
        jdbcTemplate.update("DELETE FROM user_roles");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM permissions");
        jdbcTemplate.update("DELETE FROM roles");
        jdbcTemplate.update("DELETE FROM organizations");

        jdbcTemplate.update(
                """
                INSERT INTO organizations
                (
                    id,
                    name,
                    code,
                    email,
                    status,
                    created_at,
                    updated_at,
                    created_by,
                    last_updated_by,
                    deleted,
                    deleted_at
                )
                VALUES
                (
                    1,
                    'Test Organization',
                    'TEST-ORG',
                    'org@example.com',
                    'ACTIVE',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    1,
                    FALSE,
                    NULL
                )
                """
        );

        jdbcTemplate.update(
                """
                INSERT INTO roles
                (
                    id,
                    organization_id,
                    name,
                    description,
                    created_at,
                    updated_at,
                    created_by,
                    last_updated_by,
                    deleted,
                    deleted_at
                )
                VALUES
                (
                    1,
                    1,
                    'ORG_ADMIN',
                    'Organization Administrator',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    1,
                    FALSE,
                    NULL
                )
                """
        );

        jdbcTemplate.update(
                """
                INSERT INTO roles
                (
                    id,
                    organization_id,
                    name,
                    description,
                    created_at,
                    updated_at,
                    created_by,
                    last_updated_by,
                    deleted,
                    deleted_at
                )
                VALUES
                (
                    2,
                    1,
                    'EMPLOYEE',
                    'General organization user',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    1,
                    FALSE,
                    NULL
                )
                """
        );

        jdbcTemplate.update(
                """
                INSERT INTO permissions
                (
                    id,
                    name,
                    description,
                    created_at,
                    updated_at,
                    created_by,
                    last_updated_by,
                    deleted,
                    deleted_at
                )
                VALUES
                (
                    1,
                    'USER_READ',
                    'View user information.',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    1,
                    FALSE,
                    NULL
                )
                """
        );

        jdbcTemplate.update(
                """
                INSERT INTO permissions
                (
                    id,
                    name,
                    description,
                    created_at,
                    updated_at,
                    created_by,
                    last_updated_by,
                    deleted,
                    deleted_at
                )
                VALUES
                (
                    2,
                    'USER_CREATE',
                    'Create users.',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    1,
                    FALSE,
                    NULL
                )
                """
        );

        jdbcTemplate.update(
                """
                INSERT INTO role_permissions
                (
                    id,
                    role_id,
                    permission_id,
                    created_at,
                    created_by
                )
                VALUES
                (
                    1,
                    1,
                    1,
                    CURRENT_TIMESTAMP,
                    1
                )
                """
        );
    }

    @Test
    void existsByRoleIdAndPermissionId_shouldReturnTrue_whenAssignmentExists() {

        boolean exists =
                rolePermissionRepository
                        .existsByRoleIdAndPermissionId(1L, 1L);

        assertTrue(exists);
    }

    @Test
    void existsByRoleIdAndPermissionId_shouldReturnFalse_whenAssignmentDoesNotExist() {

        boolean exists =
                rolePermissionRepository
                        .existsByRoleIdAndPermissionId(1L, 2L);

        assertFalse(exists);
    }

    @Test
    void findAllByRoleId_shouldReturnAssignedPermissions() {

        List<RolePermission> result =
                rolePermissionRepository.findAllByRoleId(1L);

        assertEquals(1, result.size());

        RolePermission rolePermission = result.get(0);

        assertEquals(1L, rolePermission.getId());
        assertEquals(1L, rolePermission.getRole().getId());
        assertEquals(1L, rolePermission.getPermission().getId());
    }

    @Test
    void findAllByRoleId_shouldReturnEmpty_whenRoleHasNoPermissions() {

        jdbcTemplate.update(
                """
                DELETE FROM role_permissions
                WHERE role_id = 1
                """
        );

        List<RolePermission> result =
                rolePermissionRepository.findAllByRoleId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteByRoleIdAndPermissionId_shouldRemoveAssignment() {

        rolePermissionRepository
                .deleteByRoleIdAndPermissionId(1L, 1L);

        boolean exists =
                rolePermissionRepository
                        .existsByRoleIdAndPermissionId(1L, 1L);

        assertFalse(exists);
    }

    @Test
    void deleteByRoleIdAndPermissionId_shouldNotRemoveOtherAssignments() {

        jdbcTemplate.update(
                """
                INSERT INTO role_permissions
                (
                    id,
                    role_id,
                    permission_id,
                    created_at,
                    created_by
                )
                VALUES
                (
                    2,
                    1,
                    2,
                    CURRENT_TIMESTAMP,
                    1
                )
                """
        );

        rolePermissionRepository
                .deleteByRoleIdAndPermissionId(1L, 1L);

        boolean firstAssignmentExists =
                rolePermissionRepository
                        .existsByRoleIdAndPermissionId(1L, 1L);

        boolean secondAssignmentExists =
                rolePermissionRepository
                        .existsByRoleIdAndPermissionId(1L, 2L);

        assertFalse(firstAssignmentExists);
        assertTrue(secondAssignmentExists);
    }
}
