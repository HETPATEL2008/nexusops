package com.hetpatel.nexusops.identity.userrole.service;

import com.hetpatel.nexusops.common.exception.BusinessException;
import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.role.entity.Role;

import com.hetpatel.nexusops.identity.role.repository.RoleRepository;

import com.hetpatel.nexusops.identity.user.entity.User;

import com.hetpatel.nexusops.identity.user.repository.UserRepository;

import com.hetpatel.nexusops.identity.userrole.dto.request.AssignRoleRequest;

import com.hetpatel.nexusops.identity.userrole.dto.response.UserRoleResponse;

import com.hetpatel.nexusops.identity.userrole.entity.UserRole;

import com.hetpatel.nexusops.identity.userrole.mapper.UserRoleMapper;

import com.hetpatel.nexusops.identity.userrole.repository.UserRoleRepository;

import com.hetpatel.nexusops.identity.userrole.service.impl.UserRoleServiceImpl;

import com.hetpatel.nexusops.organization.entity.Organization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private Organization organization;

    private User user;

    private Role role;

    private UserRole userRole;

    private UserRoleResponse userRoleResponse;

    @BeforeEach
    void setUp() {

        organization = new Organization();
        organization.setId(1L);

        user = new User();
        user.setId(10L);
        user.setOrganization(organization);

        role = new Role();
        role.setId(20L);
        role.setOrganization(organization);
        role.setName("TEST_ROLE");

        userRole = new UserRole();
        userRole.setId(30L);
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleResponse = new UserRoleResponse();
        userRoleResponse.setId(30L);
        userRoleResponse.setUserId(10L);
        userRoleResponse.setRoleId(20L);
    }

    // ---------------------------------------------------------
    // assignRole()
    // ---------------------------------------------------------

    @Test
    void assignRole_shouldAssignRoleSuccessfully() {

        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(20L);

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.of(role));

        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(false);

        when(userRoleRepository.save(any(UserRole.class)))
                .thenReturn(userRole);

        when(userRoleMapper.toResponse(userRole))
                .thenReturn(userRoleResponse);

        UserRoleResponse result =
                userRoleService.assignRole(10L, request);

        assertThat(result).isSameAs(userRoleResponse);

        verify(userRoleRepository)
                .save(any(UserRole.class));

        verify(userRoleMapper)
                .toResponse(userRole);
    }

    @Test
    void assignRole_shouldThrowException_whenUserDoesNotExist() {

        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(20L);

        when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userRoleService.assignRole(10L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 10");

        verify(roleRepository, never())
                .findById(any());

        verify(userRoleRepository, never())
                .save(any());
    }

    @Test
    void assignRole_shouldThrowException_whenRoleDoesNotExist() {

        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(20L);

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userRoleService.assignRole(10L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found with id: 20");

        verify(userRoleRepository, never())
                .save(any());
    }

    @Test
    void assignRole_shouldThrowException_whenRoleIsGlobal() {

        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(20L);

        Role globalRole = new Role();
        globalRole.setId(20L);
        globalRole.setName("SYSTEM_ADMIN");
        globalRole.setOrganization(null);

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.of(globalRole));

        assertThatThrownBy(() ->
                userRoleService.assignRole(10L, request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Global roles cannot be assigned through this operation"
                );

        verify(userRoleRepository, never())
                .save(any());
    }

    @Test
    void assignRole_shouldThrowException_whenOrganizationsDoNotMatch() {

        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(20L);

        Organization differentOrganization = new Organization();
        differentOrganization.setId(2L);

        Role differentOrganizationRole = new Role();
        differentOrganizationRole.setId(20L);
        differentOrganizationRole.setOrganization(differentOrganization);
        differentOrganizationRole.setName("TEST_ROLE");

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.of(differentOrganizationRole));

        assertThatThrownBy(() ->
                userRoleService.assignRole(10L, request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "User and role must belong to the same organization"
                );

        verify(userRoleRepository, never())
                .save(any());
    }

    @Test
    void assignRole_shouldThrowException_whenRoleAlreadyAssigned() {

        AssignRoleRequest request = new AssignRoleRequest();
        request.setRoleId(20L);

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.of(role));

        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                userRoleService.assignRole(10L, request)
        )
                .isInstanceOf(ConflictException.class)
                .hasMessage("Role is already assigned to user");

        verify(userRoleRepository, never())
                .save(any());
    }

    // ---------------------------------------------------------
    // getUserRoles()
    // ---------------------------------------------------------

    @Test
    void getUserRoles_shouldReturnRolesSuccessfully() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(userRoleRepository.findAllByUserId(10L))
                .thenReturn(List.of(userRole));

        when(userRoleMapper.toResponse(userRole))
                .thenReturn(userRoleResponse);

        List<UserRoleResponse> result =
                userRoleService.getUserRoles(10L);

        assertThat(result)
                .hasSize(1)
                .containsExactly(userRoleResponse);

        verify(userRoleRepository)
                .findAllByUserId(10L);

        verify(userRoleMapper)
                .toResponse(userRole);
    }

    @Test
    void getUserRoles_shouldReturnEmptyList_whenUserHasNoRoles() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(userRoleRepository.findAllByUserId(10L))
                .thenReturn(List.of());

        List<UserRoleResponse> result =
                userRoleService.getUserRoles(10L);

        assertThat(result).isEmpty();

        verify(userRoleRepository)
                .findAllByUserId(10L);

        verify(userRoleMapper, never())
                .toResponse(any());
    }

    @Test
    void getUserRoles_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userRoleService.getUserRoles(10L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 10");

        verify(userRoleRepository, never())
                .findAllByUserId(any());
    }

    // ---------------------------------------------------------
    // removeRole()
    // ---------------------------------------------------------

    @Test
    void removeRole_shouldRemoveRoleSuccessfully() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.of(role));

        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(true);

        userRoleService.removeRole(10L, 20L);

        verify(userRoleRepository)
                .deleteByUserIdAndRoleId(10L, 20L);
    }

    @Test
    void removeRole_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userRoleService.removeRole(10L, 20L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 10");

        verify(roleRepository, never())
                .findById(any());

        verify(userRoleRepository, never())
                .deleteByUserIdAndRoleId(any(), any());
    }

    @Test
    void removeRole_shouldThrowException_whenRoleDoesNotExist() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userRoleService.removeRole(10L, 20L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found with id: 20");

        verify(userRoleRepository, never())
                .existsByUserIdAndRoleId(any(), any());

        verify(userRoleRepository, never())
                .deleteByUserIdAndRoleId(any(), any());
    }

    @Test
    void removeRole_shouldThrowException_whenRoleIsNotAssigned() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(20L))
                .thenReturn(Optional.of(role));

        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(false);

        assertThatThrownBy(() ->
                userRoleService.removeRole(10L, 20L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role is not assigned to user");

        verify(userRoleRepository, never())
                .deleteByUserIdAndRoleId(any(), any());
    }
}
