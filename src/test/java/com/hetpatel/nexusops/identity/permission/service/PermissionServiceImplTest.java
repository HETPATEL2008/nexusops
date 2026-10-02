package com.hetpatel.nexusops.identity.permission.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.permission.dto.response.PermissionResponse;

import com.hetpatel.nexusops.identity.permission.entity.Permission;

import com.hetpatel.nexusops.identity.permission.mapper.PermissionMapper;

import com.hetpatel.nexusops.identity.permission.repository.PermissionRepository;

import com.hetpatel.nexusops.identity.permission.service.impl.PermissionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;

    private PermissionResponse permissionResponse;

    @BeforeEach
    void setUp() {

        permission = new Permission();

        permission.setId(1L);
        permission.setName("USER_READ");
        permission.setDescription("Allows reading users.");

        permissionResponse = new PermissionResponse();

        permissionResponse.setId(1L);
        permissionResponse.setName("USER_READ");
        permissionResponse.setDescription("Allows reading users.");
    }

    @Test
    void getPermissionById_shouldReturnPermission_whenPermissionExists() {

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.of(permission));

        when(permissionMapper.toResponse(permission))
                .thenReturn(permissionResponse);

        PermissionResponse result =
                permissionService.getPermissionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("USER_READ", result.getName());
        assertEquals(
                "Allows reading users.",
                result.getDescription()
        );

        verify(permissionRepository).findById(1L);
        verify(permissionMapper).toResponse(permission);
    }

    @Test
    void getPermissionById_shouldThrowException_whenPermissionDoesNotExist() {

        when(permissionRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> permissionService.getPermissionById(999L)
        );

        verify(permissionRepository).findById(999L);
        verifyNoInteractions(permissionMapper);
    }

    @Test
    void getAllPermissions_shouldReturnPageResponse() {

        Permission secondPermission = new Permission();

        secondPermission.setId(2L);
        secondPermission.setName("USER_CREATE");
        secondPermission.setDescription("Allows creating users.");

        PermissionResponse secondResponse = new PermissionResponse();

        secondResponse.setId(2L);
        secondResponse.setName("USER_CREATE");
        secondResponse.setDescription("Allows creating users.");

        PageRequest pageable =
                PageRequest.of(0, 10);

        Page<Permission> permissionPage =
                new PageImpl<>(
                        List.of(permission, secondPermission),
                        pageable,
                        2
                );

        when(permissionRepository.findAll(pageable))
                .thenReturn(permissionPage);

        when(permissionMapper.toResponse(permission))
                .thenReturn(permissionResponse);

        when(permissionMapper.toResponse(secondPermission))
                .thenReturn(secondResponse);

        PageResponse<PermissionResponse> result =
                permissionService.getAllPermissions(pageable);

        assertNotNull(result);

        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());

        assertEquals(
                "USER_READ",
                result.getContent().get(0).getName()
        );

        assertEquals(
                "USER_CREATE",
                result.getContent().get(1).getName()
        );

        verify(permissionRepository).findAll(pageable);
        verify(permissionMapper).toResponse(permission);
        verify(permissionMapper).toResponse(secondPermission);
    }

    @Test
    void getPermissionByName_shouldReturnPermission_whenPermissionExists() {

        when(permissionRepository.findByName("USER_READ"))
                .thenReturn(Optional.of(permission));

        when(permissionMapper.toResponse(permission))
                .thenReturn(permissionResponse);

        PermissionResponse result =
                permissionService.getPermissionByName("USER_READ");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("USER_READ", result.getName());

        verify(permissionRepository)
                .findByName("USER_READ");

        verify(permissionMapper)
                .toResponse(permission);
    }

    @Test
    void getPermissionByName_shouldThrowException_whenPermissionDoesNotExist() {

        when(permissionRepository.findByName("UNKNOWN_PERMISSION"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> permissionService
                        .getPermissionByName("UNKNOWN_PERMISSION")
        );

        verify(permissionRepository)
                .findByName("UNKNOWN_PERMISSION");

        verifyNoInteractions(permissionMapper);
    }
}
