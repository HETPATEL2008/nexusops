package com.hetpatel.nexusops.identity.role.repository;

import com.hetpatel.nexusops.identity.role.entity.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update("DELETE FROM user_roles");
        jdbcTemplate.update("DELETE FROM role_permissions");
        jdbcTemplate.update("DELETE FROM users");
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
                    2,
                    'Second Organization',
                    'SECOND-ORG',
                    'second@example.com',
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
                    'ADMIN',
                    'Administrator role',
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
                    'MANAGER',
                    'Manager role',
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
                    3,
                    2,
                    'ADMIN',
                    'Administrator role for second organization',
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
                    4,
                    NULL,
                    'SYSTEM_ADMIN',
                    'Global system administrator',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    1,
                    FALSE,
                    NULL
                )
                """
        );
    }

    @Test
    void existsByOrganizationIdAndName_shouldReturnTrue_whenRoleExists() {

        boolean exists =
                roleRepository.existsByOrganizationIdAndName(
                        1L,
                        "ADMIN"
                );

        assertTrue(exists);
    }

    @Test
    void existsByOrganizationIdAndName_shouldReturnFalse_whenRoleDoesNotExist() {

        boolean exists =
                roleRepository.existsByOrganizationIdAndName(
                        1L,
                        "PURCHASER"
                );

        assertFalse(exists);
    }

    @Test
    void findByOrganizationIdAndName_shouldReturnRole() {

        Optional<Role> result =
                roleRepository.findByOrganizationIdAndName(
                        1L,
                        "ADMIN"
                );

        assertTrue(result.isPresent());

        Role role = result.get();

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getName());
        assertNotNull(role.getOrganization());
        assertEquals(1L, role.getOrganization().getId());
    }

    @Test
    void findByOrganizationIdAndName_shouldReturnEmpty_whenRoleDoesNotExist() {

        Optional<Role> result =
                roleRepository.findByOrganizationIdAndName(
                        1L,
                        "UNKNOWN"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByOrganizationId_shouldReturnOnlyOrganizationRoles() {

        List<Role> roles =
                roleRepository.findAllByOrganizationId(1L);

        assertEquals(2, roles.size());

        assertTrue(
                roles.stream()
                        .allMatch(role ->
                                role.getOrganization() != null
                                        && role.getOrganization()
                                        .getId()
                                        .equals(1L))
        );

        assertTrue(
                roles.stream()
                        .anyMatch(role ->
                                role.getName().equals("ADMIN"))
        );

        assertTrue(
                roles.stream()
                        .anyMatch(role ->
                                role.getName().equals("MANAGER"))
        );
    }

    @Test
    void findAllByOrganizationIdIsNull_shouldReturnGlobalRoles() {

        List<Role> roles =
                roleRepository.findAllByOrganizationIdIsNull();

        assertEquals(1, roles.size());

        Role role = roles.get(0);

        assertEquals("SYSTEM_ADMIN", role.getName());
        assertNull(role.getOrganization());
    }

    @Test
    void findByIdAndOrganizationId_shouldReturnRoleForCorrectOrganization() {

        Optional<Role> result =
                roleRepository.findByIdAndOrganizationId(
                        1L,
                        1L
                );

        assertTrue(result.isPresent());

        Role role = result.get();

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getName());
    }

    @Test
    void findByIdAndOrganizationId_shouldReturnEmpty_forDifferentOrganization() {

        Optional<Role> result =
                roleRepository.findByIdAndOrganizationId(
                        1L,
                        2L
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndOrganizationId_shouldReturnEmpty_whenRoleDoesNotExist() {

        Optional<Role> result =
                roleRepository.findByIdAndOrganizationId(
                        999L,
                        1L
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnRole_whenRoleExists() {

        Optional<Role> result =
                roleRepository.findById(1L);

        assertTrue(result.isPresent());

        assertEquals(
                "ADMIN",
                result.get().getName()
        );
    }

    @Test
    void findById_shouldNotReturnSoftDeletedRole() {

        jdbcTemplate.update(
                """
                UPDATE roles
                SET deleted = TRUE,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = 1
                """
        );

        Optional<Role> result =
                roleRepository.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldNotReturnSoftDeletedRoles() {

        jdbcTemplate.update(
                """
                UPDATE roles
                SET deleted = TRUE,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = 1
                """
        );

        List<Role> roles =
                roleRepository.findAll();

        assertEquals(3, roles.size());

        assertTrue(
                roles.stream()
                        .noneMatch(role ->
                                role.getId().equals(1L))
        );
    }

    @Test
    void restoreById_shouldRestoreDeletedRole() {

        jdbcTemplate.update(
                """
                UPDATE roles
                SET deleted = TRUE,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = 1
                """
        );

        int restoredRows =
                roleRepository.restoreById(1L);

        assertEquals(1, restoredRows);

        Optional<Role> result =
                roleRepository.findById(1L);

        assertTrue(result.isPresent());

        assertEquals(
                "ADMIN",
                result.get().getName()
        );
    }

    @Test
    void restoreById_shouldReturnZero_whenRoleDoesNotExist() {

        int restoredRows =
                roleRepository.restoreById(999L);

        assertEquals(0, restoredRows);
    }

    @Test
    void restoreById_shouldRestoreOnlyDeletedRole() {

        int restoredRows =
                roleRepository.restoreById(1L);

        assertEquals(0, restoredRows);
    }
}
