package com.hetpatel.nexusops.identity.user.service.impl;

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

import com.hetpatel.nexusops.identity.user.service.UserService;

import com.hetpatel.nexusops.organization.entity.Organization;

import com.hetpatel.nexusops.organization.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final OrganizationRepository organizationRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn(
                            "Organization not found with id: {}",
                            request.getOrganizationId()
                    );

                    return new ResourceNotFoundException(
                            "Organization not found with id: " + request.getOrganizationId()
                    );
                });

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("User creation failed. Username already exists: {}", request.getUsername());

            throw new ConflictException(
                    "User with username '" + request.getUsername() + "' already exists."
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User creation failed. Email already exists: {}", request.getEmail());

            throw new ConflictException(
                    "User with email '" + request.getEmail() + "' already exists."
            );
        }

        User user = userMapper.toEntity(request);

        user.setOrganization(organization);

        String passwordHash = passwordHasher.hash(request.getPassword());
        user.setPasswordHash(passwordHash);

        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        log.info("User created successfully with id: {} and username: {}",
                savedUser.getId(),
                savedUser.getUsername()
        );

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "User not found: id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "User not found with id: " + id
                    );
                });

        return userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> content =
                userPage
                        .getContent()
                        .stream()
                        .map(userMapper::toResponse)
                        .toList();

        log.info(
                "Users retrieved: page={}, size={}, totalElements={}",
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    @Transactional
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "User update failed: user not found, id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "User not found with id: " + id
                    );
                });

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            log.warn(
                    "User update failed: username already exists, id={}, username={}",
                    id,
                    request.getUsername()
            );

            throw new ConflictException(
                    "User with username '" + request.getUsername() + "' already exists."
            );
        }

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            log.warn(
                    "User update failed: email already exists, id={}, email={}",
                    id,
                    request.getEmail()
            );

            throw new ConflictException(
                    "User with email '" + request.getEmail() + "' already exists."
            );
        }

        userMapper.updateEntity(request, user);

        User updatedUser = userRepository.save(user);

        log.info(
                "User updated successfully: id={}, username={}, email={}",
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail()
        );

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "User deletion failed: user not found, id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "User not found with id: " + id
                    );
                });

        userRepository.deleteById(id);

        log.info(
                "User soft deleted successfully: id={}, username={}",
                user.getId(),
                user.getUsername()
        );
    }

    @Override
    @Transactional
    public UserResponse restoreUser(Long id) {

        int restoredRows = userRepository.restoreById(id);

        if (restoredRows == 0) {
            log.warn(
                    "User restoration failed: deleted user not found, id={}", id);

            throw new ResourceNotFoundException(
                    "Deleted user not found with id: " + id
            );
        }

        User restoredUser =
                userRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "User restoration failed: restored user not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "User not found with id: " + id
                            );
                        });

        log.info(
                "User restored successfully: id={}, username={}",
                restoredUser.getId(),
                restoredUser.getUsername()
        );

        return userMapper.toResponse(restoredUser);
    }
}
