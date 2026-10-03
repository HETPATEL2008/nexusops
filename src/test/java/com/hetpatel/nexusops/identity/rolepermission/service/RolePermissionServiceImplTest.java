package com.hetpatel.nexusops.identity.rolepermission.service;

import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.permission.entity.Permission;
import com.hetpatel.nexusops.identity.permission.repository.PermissionRepository;

import com.hetpatel.nexusops.identity.role.entity.Role;
import com.hetpatel.nexusops.identity.role.repository.RoleRepository;

import com.hetpatel.nexusops.identity.rolepermission.dto.request.AssignPermissionRequest;
import com.hetpatel.nexusops.identity.rolepermission.dto.response.RolePermissionResponse;
import com.hetpatel.nexusops.identity.rolepermission.entity.RolePermission;
import com.hetpatel.nexusops.identity.rolepermission.mapper.RolePermissionMapper;
import com.hetpatel.nexusops.identity.rolepermission.repository.RolePermissionRepository;

import com.hetpatel.nexusops.identity.rolepermission.service.impl.RolePermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceImplTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    private Role role;
    private Permission permission;
    private AssignPermissionRequest assignPermissionRequest;
    private RolePermission rolePermission;
    private RolePermissionResponse rolePermissionResponse;

    @BeforeEach
    void setUp() {

        role = new Role();
        role.setId(1L);
        role.setName("ORG_ADMIN");

        permission = new Permission();
        permission.setId(1L);
        permission.setName("USER_READ");

        assignPermissionRequest = new AssignPermissionRequest();
        assignPermissionRequest.setPermissionId(1L);

        rolePermission = new RolePermission();
        rolePermission.setId(1L);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        rolePermissionResponse = new RolePermissionResponse();
        rolePermissionResponse.setId(1L);
        rolePermissionResponse.setRoleId(1L);
        rolePermissionResponse.setPermissionId(1L);
    }

    @Test
    void assignPermission_shouldAssignPermissionSuccessfully() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.of(permission));

        when(rolePermissionRepository
                .existsByRoleIdAndPermissionId(1L, 1L))
                .thenReturn(false);

        when(rolePermissionRepository.save(any(RolePermission.class)))
                .thenReturn(rolePermission);

        when(rolePermissionMapper.toResponse(rolePermission))
                .thenReturn(rolePermissionResponse);

        RolePermissionResponse result =
                rolePermissionService.assignPermission(
                        1L,
                        assignPermissionRequest
                );

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getRoleId());
        assertEquals(1L, result.getPermissionId());

        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository)
                .existsByRoleIdAndPermissionId(1L, 1L);
        verify(rolePermissionRepository)
                .save(any(RolePermission.class));
        verify(rolePermissionMapper)
                .toResponse(rolePermission);
    }

    @Test
    void assignPermission_shouldThrowException_whenRoleDoesNotExist() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> rolePermissionService.assignPermission(
                        1L,
                        assignPermissionRequest
                )
        );

        verify(roleRepository).findById(1L);

        verifyNoInteractions(
                permissionRepository,
                rolePermissionRepository,
                rolePermissionMapper
        );
    }

    @Test
    void assignPermission_shouldThrowException_whenPermissionDoesNotExist() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> rolePermissionService.assignPermission(
                        1L,
                        assignPermissionRequest
                )
        );

        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);

        verifyNoInteractions(
                rolePermissionRepository,
                rolePermissionMapper
        );
    }

    @Test
    void assignPermission_shouldThrowException_whenPermissionAlreadyAssigned() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.of(permission));

        when(rolePermissionRepository
                .existsByRoleIdAndPermissionId(1L, 1L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> rolePermissionService.assignPermission(
                        1L,
                        assignPermissionRequest
                )
        );

        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository)
                .existsByRoleIdAndPermissionId(1L, 1L);

        verify(rolePermissionRepository, never())
                .save(any(RolePermission.class));

        verifyNoInteractions(rolePermissionMapper);
    }

    @Test
    void getRolePermissions_shouldReturnAssignedPermissions() {

        RolePermission secondRolePermission = new RolePermission();
        secondRolePermission.setId(2L);
        secondRolePermission.setRole(role);
        secondRolePermission.setPermission(permission);

        RolePermissionResponse secondResponse =
                new RolePermissionResponse();
        secondResponse.setId(2L);
        secondResponse.setRoleId(1L);
        secondResponse.setPermissionId(1L);

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(rolePermissionRepository.findAllByRoleId(1L))
                .thenReturn(List.of(
                        rolePermission,
                        secondRolePermission
                ));

        when(rolePermissionMapper.toResponse(rolePermission))
                .thenReturn(rolePermissionResponse);

        when(rolePermissionMapper.toResponse(secondRolePermission))
                .thenReturn(secondResponse);

        List<RolePermissionResponse> result =
                rolePermissionService.getRolePermissions(1L);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(roleRepository).findById(1L);
        verify(rolePermissionRepository).findAllByRoleId(1L);
        verify(rolePermissionMapper)
                .toResponse(rolePermission);
        verify(rolePermissionMapper)
                .toResponse(secondRolePermission);
    }

    @Test
    void getRolePermissions_shouldReturnEmpty_whenRoleHasNoPermissions() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(rolePermissionRepository.findAllByRoleId(1L))
                .thenReturn(List.of());

        List<RolePermissionResponse> result =
                rolePermissionService.getRolePermissions(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roleRepository).findById(1L);
        verify(rolePermissionRepository).findAllByRoleId(1L);

        verifyNoInteractions(rolePermissionMapper);
    }

    @Test
    void getRolePermissions_shouldThrowException_whenRoleDoesNotExist() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> rolePermissionService.getRolePermissions(1L)
        );

        verify(roleRepository).findById(1L);

        verifyNoInteractions(
                rolePermissionRepository,
                rolePermissionMapper
        );
    }

    @Test
    void removePermission_shouldRemovePermissionSuccessfully() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.of(permission));

        when(rolePermissionRepository
                .existsByRoleIdAndPermissionId(1L, 1L))
                .thenReturn(true);

        rolePermissionService.removePermission(1L, 1L);

        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository)
                .existsByRoleIdAndPermissionId(1L, 1L);

        verify(rolePermissionRepository)
                .deleteByRoleIdAndPermissionId(1L, 1L);
    }

    @Test
    void removePermission_shouldThrowException_whenRoleDoesNotExist() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> rolePermissionService.removePermission(1L, 1L)
        );

        verify(roleRepository).findById(1L);

        verifyNoInteractions(
                permissionRepository,
                rolePermissionRepository
        );
    }

    @Test
    void removePermission_shouldThrowException_whenPermissionDoesNotExist() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> rolePermissionService.removePermission(1L, 1L)
        );

        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);

        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    void removePermission_shouldThrowException_whenPermissionIsNotAssigned() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findById(1L))
                .thenReturn(Optional.of(permission));

        when(rolePermissionRepository
                .existsByRoleIdAndPermissionId(1L, 1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> rolePermissionService.removePermission(1L, 1L)
        );

        verify(roleRepository).findById(1L);
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository)
                .existsByRoleIdAndPermissionId(1L, 1L);

        verify(rolePermissionRepository, never())
                .deleteByRoleIdAndPermissionId(anyLong(), anyLong());
    }
}
