package com.hetpatel.nexusops.identity.user.repository;

import com.hetpatel.nexusops.identity.user.entity.User;
import com.hetpatel.nexusops.identity.user.entity.UserStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

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
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {

        boolean exists =
                userRepository.existsByUsername("john.doe");

        assertTrue(exists);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {

        boolean exists =
                userRepository.existsByUsername("unknown.user");

        assertFalse(exists);
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {

        boolean exists =
                userRepository.existsByEmail("john@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {

        boolean exists =
                userRepository.existsByEmail("unknown@example.com");

        assertFalse(exists);
    }

    @Test
    void findByUsername_shouldReturnUser_whenUsernameExists() {

        Optional<User> result =
                userRepository.findByUsername("john.doe");

        assertTrue(result.isPresent());

        User user = result.get();

        assertEquals(1L, user.getId());
        assertEquals("john.doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUsernameDoesNotExist() {

        Optional<User> result =
                userRepository.findByUsername("unknown.user");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {

        Optional<User> result =
                userRepository.findByEmail("john@example.com");

        assertTrue(result.isPresent());

        User user = result.get();

        assertEquals(1L, user.getId());
        assertEquals("john.doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {

        Optional<User> result =
                userRepository.findByEmail("unknown@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void restoreById_shouldRestoreDeletedUser() {

        jdbcTemplate.update(
                """
                UPDATE users
                SET deleted = TRUE,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = 1
                """
        );

        int restoredRows =
                userRepository.restoreById(1L);

        assertEquals(1, restoredRows);

        Optional<User> result =
                userRepository.findById(1L);

        assertTrue(result.isPresent());

        User restoredUser = result.get();

        assertEquals(1L, restoredUser.getId());
        assertEquals("john.doe", restoredUser.getUsername());
    }

    @Test
    void restoreById_shouldReturnZero_whenUserDoesNotExist() {

        int restoredRows =
                userRepository.restoreById(999L);

        assertEquals(0, restoredRows);
    }

    @Test
    void findById_shouldNotReturnSoftDeletedUser() {

        jdbcTemplate.update(
                """
                UPDATE users
                SET deleted = TRUE,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = 1
                """
        );

        Optional<User> result =
                userRepository.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldNotReturnSoftDeletedUsers() {

        jdbcTemplate.update(
                """
                UPDATE users
                SET deleted = TRUE,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = 1
                """
        );

        var result =
                userRepository.findAll();

        assertTrue(result.isEmpty());
    }
}
