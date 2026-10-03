package com.hetpatel.nexusops.identity.userrole.service.impl;

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

import com.hetpatel.nexusops.identity.userrole.service.UserRoleService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserRoleResponse assignRole(Long userId, AssignRoleRequest assignRoleRequest) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", userId);
                    return new ResourceNotFoundException(
                            "User not found with id: " + userId);
                });

        Role role = roleRepository
                .findById(assignRoleRequest.getRoleId())
                .orElseThrow(() -> {
                    log.warn("Role not found with id {}", assignRoleRequest.getRoleId());
                    return new ResourceNotFoundException(
                            "Role not found with id: " + assignRoleRequest.getRoleId()
                    );
                });

        if (role.getOrganization() == null) {
            log.warn(
                    "Attempt to assign global role with id {} to user with id {}",
                    role.getId(),
                    userId
            );
            throw new BusinessException("Global roles cannot be assigned through this operation");
        }

        if (!user.getOrganization().getId().equals(role.getOrganization().getId())) {
            log.warn(
                    "Organization mismatch while assigning role {} to user {}",
                    role.getId(),
                    userId
            );
            throw new BusinessException("User and role must belong to the same organization");
        }

        if (userRoleRepository.existsByUserIdAndRoleId(userId, role.getId())) {
            log.warn(
                    "Role {} is already assigned to user {}",
                    role.getId(),
                    userId
            );
            throw new ConflictException("Role is already assigned to user");
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        UserRole savedUserRole = userRoleRepository.save(userRole);

        log.info(
                "Role {} assigned successfully to user {}",
                role.getId(),
                userId
        );

        return userRoleMapper.toResponse(savedUserRole);
    }

    @Override
    public List<UserRoleResponse> getUserRoles(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", userId);
                    return new ResourceNotFoundException(
                            "User not found with id: " + userId);
                });

        List<UserRole> userRoles = userRoleRepository.findAllByUserId(userId);

        log.info("Retrieved {} roles for user {}", userRoles.size(), userId);

        return userRoles
                .stream()
                .map(userRoleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", userId);
                    return new ResourceNotFoundException(
                            "User not found with id: " + userId);
                });

        roleRepository
                .findById(roleId)
                .orElseThrow(() -> {
                    log.warn("Role not found with id {}", roleId);
                    return new ResourceNotFoundException(
                            "Role not found with id: " + roleId);
                });

        if (!userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            log.warn(
                    "Role {} is not assigned to user {}",
                    roleId,
                    userId
            );
            throw new ResourceNotFoundException(
                    "Role is not assigned to user"
            );
        }

        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);

        log.info(
                "Role {} removed successfully from user {}",
                roleId,
                userId
        );
    }
}
