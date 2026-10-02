package com.hetpatel.nexusops.identity.permission.repository;

import com.hetpatel.nexusops.identity.permission.entity.Permission;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    private Permission createPermission(String name, String description) {

        Permission permission = new Permission();

        permission.setName(name);
        permission.setDescription(description);

        permission.setCreatedAt(Instant.now());
        permission.setUpdatedAt(Instant.now());
        permission.setCreatedBy(1L);
        permission.setLastUpdatedBy(1L);

        return permission;
    }

    @Test
    void findByName_shouldReturnPermission_whenPermissionExists() {

        Permission permission =
                createPermission(
                        "TEST_PERMISSION_READ",
                        "Test permission for repository testing."
                );

        Permission savedPermission =
                permissionRepository.save(permission);

        Optional<Permission> result =
                permissionRepository.findByName("TEST_PERMISSION_READ");

        assertTrue(result.isPresent());

        assertEquals(
                savedPermission.getId(),
                result.get().getId()
        );

        assertEquals(
                "TEST_PERMISSION_READ",
                result.get().getName()
        );
    }

    @Test
    void findByName_shouldReturnEmpty_whenPermissionDoesNotExist() {

        Optional<Permission> result =
                permissionRepository.findByName(
                        "UNKNOWN_PERMISSION"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldPersistPermission() {

        Permission permission =
                createPermission(
                        "TEST_PERMISSION_CREATE",
                        "Test permission for repository testing."
                );

        Permission savedPermission =
                permissionRepository.save(permission);

        assertNotNull(savedPermission.getId());

        assertEquals(
                "TEST_PERMISSION_CREATE",
                savedPermission.getName()
        );
    }
}
