package com.hetpatel.nexusops.identity.user.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.user.dto.request.CreateUserRequest;
import com.hetpatel.nexusops.identity.user.dto.request.UpdateUserRequest;

import com.hetpatel.nexusops.identity.user.dto.response.UserResponse;

import com.hetpatel.nexusops.identity.user.entity.User;
import com.hetpatel.nexusops.identity.user.entity.UserStatus;

import com.hetpatel.nexusops.identity.user.mapper.UserMapper;

import com.hetpatel.nexusops.identity.user.repository.UserRepository;

import com.hetpatel.nexusops.identity.user.security.PasswordHasher;

import com.hetpatel.nexusops.identity.user.service.impl.UserServiceImpl;

import com.hetpatel.nexusops.organization.entity.Organization;

import com.hetpatel.nexusops.organization.repository.OrganizationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UserServiceImpl userService;

    private Organization organization;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        organization = new Organization();
        organization.setId(1L);
        organization.setName("Nexus Organization");
        organization.setCode("NEXUS");

        user = new User();
        user.setId(1L);
        user.setOrganization(organization);
        user.setUsername("john.doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("$2a$10$hashedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setStatus(UserStatus.ACTIVE);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setOrganizationId(1L);
        userResponse.setUsername("john.doe");
        userResponse.setEmail("john@example.com");
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {

        CreateUserRequest request = new CreateUserRequest();
        request.setOrganizationId(1L);
        request.setUsername("john.doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(userRepository.existsByUsername("john.doe"))
                .thenReturn(false);

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordHasher.hash("Password@123"))
                .thenReturn("$2a$10$hashedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(UserStatus.ACTIVE, result.getStatus());

        verify(organizationRepository).findById(1L);
        verify(userRepository).existsByUsername("john.doe");
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordHasher).hash("Password@123");
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);

        assertEquals(organization, user.getOrganization());
        assertEquals("$2a$10$hashedPassword", user.getPasswordHash());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void createUser_shouldThrowException_whenOrganizationDoesNotExist() {

        CreateUserRequest request = new CreateUserRequest();
        request.setOrganizationId(99L);
        request.setUsername("john.doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(organizationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.createUser(request)
        );

        verify(organizationRepository).findById(99L);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void createUser_shouldThrowException_whenUsernameAlreadyExists() {

        CreateUserRequest request = new CreateUserRequest();
        request.setOrganizationId(1L);
        request.setUsername("john.doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(userRepository.existsByUsername("john.doe"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository).existsByUsername("john.doe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        CreateUserRequest request = new CreateUserRequest();
        request.setOrganizationId(1L);
        request.setUsername("john.doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(userRepository.existsByUsername("john.doe"))
                .thenReturn(false);

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository).existsByUsername("john.doe");
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void getUserById_shouldReturnUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe", result.getUsername());

        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(99L)
        );

        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_shouldReturnPaginatedUsers() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user),
                        pageable,
                        1
                );

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        PageResponse<UserResponse> result =
                userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());

        assertEquals(
                "john.doe",
                result.getContent().get(0).getUsername()
        );

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("john.updated");
        request.setEmail("john.updated@example.com");
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setStatus(UserStatus.INACTIVE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByUsername("john.updated"))
                .thenReturn(false);

        when(userRepository.existsByEmail("john.updated@example.com"))
                .thenReturn(false);

        doAnswer(invocation -> {
            User target = invocation.getArgument(1);

            target.setUsername("john.updated");
            target.setEmail("john.updated@example.com");
            target.setFirstName("John");
            target.setLastName("Updated");
            target.setStatus(UserStatus.INACTIVE);

            return null;
        }).when(userMapper).updateEntity(request, user);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.updateUser(1L, request);

        assertNotNull(result);

        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("john.updated");
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userMapper).updateEntity(request, user);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("john.updated");
        request.setEmail("john.updated@example.com");
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(99L, request)
        );

        verify(userRepository).findById(99L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowException_whenUsernameAlreadyExists() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("existing.user");
        request.setEmail("john@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByUsername("existing.user"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> userService.updateUser(1L, request)
        );

        verify(userRepository).existsByUsername("existing.user");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyExists() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("john.doe");
        request.setEmail("existing@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("existing@example.com"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> userService.updateUser(1L, request)
        );

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldNotCheckUsername_whenUsernameIsUnchanged() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("john.doe");
        request.setEmail("john.updated@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("john.updated@example.com"))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        userService.updateUser(1L, request);

        verify(userRepository, never())
                .existsByUsername("john.doe");

        verify(userRepository)
                .existsByEmail("john.updated@example.com");
    }

    @Test
    void updateUser_shouldNotCheckEmail_whenEmailIsUnchanged() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("john.updated");
        request.setEmail("john@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByUsername("john.updated"))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        userService.updateUser(1L, request);

        verify(userRepository)
                .existsByUsername("john.updated");

        verify(userRepository, never())
                .existsByEmail("john@example.com");
    }

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(99L)
        );

        verify(userRepository).findById(99L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void restoreUser_shouldRestoreUserSuccessfully() {

        when(userRepository.restoreById(1L))
                .thenReturn(1);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.restoreUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe", result.getUsername());

        verify(userRepository).restoreById(1L);
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void restoreUser_shouldThrowException_whenDeletedUserDoesNotExist() {

        when(userRepository.restoreById(99L))
                .thenReturn(0);

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.restoreUser(99L)
        );

        verify(userRepository).restoreById(99L);
        verify(userRepository, never()).findById(99L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void restoreUser_shouldThrowException_whenRestoreSucceedsButUserCannotBeFound() {

        when(userRepository.restoreById(1L))
                .thenReturn(1);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.restoreUser(1L)
        );

        verify(userRepository).restoreById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_shouldStoreHashedPassword_notRawPassword() {

        CreateUserRequest request = new CreateUserRequest();
        request.setOrganizationId(1L);
        request.setUsername("john.doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(organizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        when(userRepository.existsByUsername("john.doe"))
                .thenReturn(false);

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordHasher.hash("Password@123"))
                .thenReturn("$2a$10$secureHash");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        userService.createUser(request);

        ArgumentCaptor<String> passwordCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(passwordHasher).hash(passwordCaptor.capture());

        assertEquals(
                "Password@123",
                passwordCaptor.getValue()
        );

        assertEquals(
                "$2a$10$secureHash",
                user.getPasswordHash()
        );

        assertNotEquals(
                request.getPassword(),
                user.getPasswordHash()
        );
    }
}
