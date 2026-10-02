package com.hetpatel.nexusops.identity.role.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.role.dto.request.CreateRoleRequest;
import com.hetpatel.nexusops.identity.role.dto.request.UpdateRoleRequest;

import com.hetpatel.nexusops.identity.role.dto.response.RoleResponse;

import com.hetpatel.nexusops.identity.role.entity.Role;

import com.hetpatel.nexusops.identity.role.mapper.RoleMapper;

import com.hetpatel.nexusops.identity.role.repository.RoleRepository;

import com.hetpatel.nexusops.identity.role.service.impl.RoleServiceImpl;

import com.hetpatel.nexusops.organization.entity.Organization;

import com.hetpatel.nexusops.organization.repository.OrganizationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Organization organization;
    private Role role;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {

        organization = new Organization();
        organization.setId(1L);
        organization.setName("Nexus Organization");
        organization.setCode("NEXUS");

        role = new Role();
        role.setId(1L);
        role.setOrganization(organization);
        role.setName("ADMIN");
        role.setDescription("Administrator role");

        roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setOrganizationId(1L);
        roleResponse.setName("ADMIN");
        roleResponse.setDescription("Administrator role");
    }

    @Test
    void createRole_shouldCreateRoleSuccessfully() {

        CreateRoleRequest request = new CreateRoleRequest();
        request.setOrganizationId(1L);
        request.setName("ADMIN");
        request.setDescription("Administrator role");

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(roleRepository.existsByOrganizationIdAndName(1L, "ADMIN"))
                .thenReturn(false);

        when(roleMapper.toEntity(request))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result = roleService.createRole(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());
        assertEquals(1L, result.getOrganizationId());

        verify(organizationRepository).findById(1L);
        verify(roleRepository)
                .existsByOrganizationIdAndName(1L, "ADMIN");
        verify(roleMapper).toEntity(request);
        verify(roleRepository).save(role);
        verify(roleMapper).toResponse(role);

        assertEquals(organization, role.getOrganization());
    }

    @Test
    void createRole_shouldThrowException_whenOrganizationDoesNotExist() {

        CreateRoleRequest request = new CreateRoleRequest();
        request.setOrganizationId(99L);
        request.setName("ADMIN");
        request.setDescription("Administrator role");

        when(organizationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.createRole(request)
        );

        verify(organizationRepository).findById(99L);
        verifyNoInteractions(roleRepository);
        verifyNoInteractions(roleMapper);
    }

    @Test
    void createRole_shouldThrowException_whenRoleAlreadyExists() {

        CreateRoleRequest request = new CreateRoleRequest();
        request.setOrganizationId(1L);
        request.setName("ADMIN");
        request.setDescription("Administrator role");

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(roleRepository.existsByOrganizationIdAndName(1L, "ADMIN"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> roleService.createRole(request)
        );

        verify(roleRepository)
                .existsByOrganizationIdAndName(1L, "ADMIN");

        verify(roleRepository, never()).save(any());
        verify(roleMapper, never()).toEntity(any());
    }

    @Test
    void getRoleById_shouldReturnRoleSuccessfully() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result =
                roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());

        verify(roleRepository).findById(1L);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void getRoleById_shouldThrowException_whenRoleDoesNotExist() {

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.getRoleById(99L)
        );

        verify(roleRepository).findById(99L);
        verifyNoInteractions(roleMapper);
    }

    @Test
    void getAllRoles_shouldReturnPaginatedRoles() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> rolePage =
                new PageImpl<>(
                        List.of(role),
                        pageable,
                        1
                );

        when(roleRepository.findAll(pageable))
                .thenReturn(rolePage);

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        PageResponse<RoleResponse> result =
                roleService.getAllRoles(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());

        assertEquals(
                "ADMIN",
                result.getContent().get(0).getName()
        );

        verify(roleRepository).findAll(pageable);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void updateRole_shouldUpdateRoleSuccessfully() {

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("MANAGER");
        request.setDescription("Manager role");

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleRepository.existsByOrganizationIdAndName(
                1L,
                "MANAGER"
        )).thenReturn(false);

        doAnswer(invocation -> {

            Role target = invocation.getArgument(1);

            target.setName("MANAGER");
            target.setDescription("Manager role");

            return null;

        }).when(roleMapper).updateEntity(request, role);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result =
                roleService.updateRole(1L, request);

        assertNotNull(result);

        verify(roleRepository).findById(1L);
        verify(roleRepository)
                .existsByOrganizationIdAndName(1L, "MANAGER");
        verify(roleMapper).updateEntity(request, role);
        verify(roleRepository).save(role);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void updateRole_shouldThrowException_whenRoleDoesNotExist() {

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("MANAGER");
        request.setDescription("Manager role");

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.updateRole(99L, request)
        );

        verify(roleRepository).findById(99L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateRole_shouldThrowException_whenNewNameAlreadyExists() {

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("MANAGER");
        request.setDescription("Manager role");

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleRepository.existsByOrganizationIdAndName(
                1L,
                "MANAGER"
        )).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> roleService.updateRole(1L, request)
        );

        verify(roleRepository)
                .existsByOrganizationIdAndName(1L, "MANAGER");

        verify(roleMapper, never())
                .updateEntity(any(), any());

        verify(roleRepository, never())
                .save(any());
    }

    @Test
    void updateRole_shouldNotCheckDuplicate_whenNameIsUnchanged() {

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("ADMIN");
        request.setDescription("Updated administrator role");

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        roleService.updateRole(1L, request);

        verify(
                roleRepository,
                never()
        ).existsByOrganizationIdAndName(
                anyLong(),
                eq("ADMIN")
        );

        verify(roleRepository).save(role);
    }

    @Test
    void updateRole_shouldUpdateGlobalRoleWithoutOrganizationCheck() {

        Role globalRole = new Role();
        globalRole.setId(2L);
        globalRole.setOrganization(null);
        globalRole.setName("SYSTEM_ADMIN");
        globalRole.setDescription("Global system administrator");

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("SYSTEM_MANAGER");
        request.setDescription("Global system manager");

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(globalRole));

        doAnswer(invocation -> {

            Role target = invocation.getArgument(1);

            target.setName("SYSTEM_MANAGER");
            target.setDescription("Global system manager");

            return null;

        }).when(roleMapper).updateEntity(request, globalRole);

        when(roleRepository.save(globalRole))
                .thenReturn(globalRole);

        when(roleMapper.toResponse(globalRole))
                .thenReturn(roleResponse);

        RoleResponse result =
                roleService.updateRole(2L, request);

        assertNotNull(result);

        verify(
                roleRepository,
                never()
        ).existsByOrganizationIdAndName(
                anyLong(),
                anyString()
        );

        verify(roleRepository).save(globalRole);
    }

    @Test
    void deleteRole_shouldDeleteRoleSuccessfully() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        roleService.deleteRole(1L);

        verify(roleRepository).findById(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void deleteRole_shouldThrowException_whenRoleDoesNotExist() {

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.deleteRole(99L)
        );

        verify(roleRepository).findById(99L);
        verify(roleRepository, never()).deleteById(anyLong());
    }

    @Test
    void restoreRole_shouldRestoreRoleSuccessfully() {

        when(roleRepository.restoreById(1L))
                .thenReturn(1);

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result =
                roleService.restoreRole(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());

        verify(roleRepository).restoreById(1L);
        verify(roleRepository).findById(1L);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void restoreRole_shouldThrowException_whenDeletedRoleDoesNotExist() {

        when(roleRepository.restoreById(99L))
                .thenReturn(0);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.restoreRole(99L)
        );

        verify(roleRepository).restoreById(99L);
        verify(roleRepository, never()).findById(99L);
        verifyNoInteractions(roleMapper);
    }

    @Test
    void restoreRole_shouldThrowException_whenRestoredRoleCannotBeFound() {

        when(roleRepository.restoreById(1L))
                .thenReturn(1);

        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.restoreRole(1L)
        );

        verify(roleRepository).restoreById(1L);
        verify(roleRepository).findById(1L);
    }
}
