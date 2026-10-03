package com.hetpatel.nexusops.identity.userrole.repository;

import com.hetpatel.nexusops.identity.userrole.entity.UserRole;

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
class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

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
                INSERT INTO users
                (
                    id,
                    organization_id,
                    username,
                    email,
                    password_hash,
                    first_name,
                    last_name,
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
                    1,
                    'john.doe',
                    'john@example.com',
                    '$2a$10$testHash',
                    'John',
                    'Doe',
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
                INSERT INTO user_roles
                (
                    id,
                    user_id,
                    role_id,
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
    void existsByUserIdAndRoleId_shouldReturnTrue_whenAssignmentExists() {

        boolean exists =
                userRoleRepository.existsByUserIdAndRoleId(1L, 1L);

        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndRoleId_shouldReturnFalse_whenAssignmentDoesNotExist() {

        boolean exists =
                userRoleRepository.existsByUserIdAndRoleId(1L, 2L);

        assertFalse(exists);
    }

    @Test
    void findAllByUserId_shouldReturnAssignedRoles() {

        List<UserRole> result =
                userRoleRepository.findAllByUserId(1L);

        assertEquals(1, result.size());

        UserRole userRole = result.get(0);

        assertEquals(1L, userRole.getId());
        assertEquals(1L, userRole.getUser().getId());
        assertEquals(1L, userRole.getRole().getId());
    }

    @Test
    void findAllByUserId_shouldReturnEmpty_whenUserHasNoRoles() {

        jdbcTemplate.update(
                """
                DELETE FROM user_roles
                WHERE user_id = 1
                """
        );

        List<UserRole> result =
                userRoleRepository.findAllByUserId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteByUserIdAndRoleId_shouldRemoveAssignment() {

        userRoleRepository.deleteByUserIdAndRoleId(1L, 1L);

        boolean exists =
                userRoleRepository.existsByUserIdAndRoleId(1L, 1L);

        assertFalse(exists);
    }

    @Test
    void deleteByUserIdAndRoleId_shouldNotRemoveOtherAssignments() {

        jdbcTemplate.update(
                """
                INSERT INTO user_roles
                (
                    id,
                    user_id,
                    role_id,
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

        userRoleRepository.deleteByUserIdAndRoleId(1L, 1L);

        boolean firstAssignmentExists =
                userRoleRepository.existsByUserIdAndRoleId(1L, 1L);

        boolean secondAssignmentExists =
                userRoleRepository.existsByUserIdAndRoleId(1L, 2L);

        assertFalse(firstAssignmentExists);
        assertTrue(secondAssignmentExists);
    }
}
