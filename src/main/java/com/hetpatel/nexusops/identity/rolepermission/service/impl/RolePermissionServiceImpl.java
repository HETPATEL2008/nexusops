package com.hetpatel.nexusops.identity.rolepermission.service.impl;

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

import com.hetpatel.nexusops.identity.rolepermission.service.RolePermissionService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RolePermissionMapper rolePermissionMapper;

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public RolePermissionResponse assignPermission(
            Long roleId,
            AssignPermissionRequest assignPermissionRequest) {

        Role role = roleRepository
                .findById(roleId)
                .orElseThrow(() -> {
                    log.warn("Role not found with id {}", roleId);
                    return new ResourceNotFoundException(
                            "Role not found with id: " + roleId);
                });

        Permission permission = permissionRepository
                .findById(assignPermissionRequest.getPermissionId())
                .orElseThrow(() -> {
                    log.warn(
                            "Permission not found with id {}",
                            assignPermissionRequest.getPermissionId()
                    );
                    return new ResourceNotFoundException(
                            "Permission not found with id: "
                                    + assignPermissionRequest.getPermissionId()
                    );
                });

        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permission.getId())) {
            log.warn(
                    "Permission {} is already assigned to role {}",
                    permission.getId(),
                    roleId
            );
            throw new ConflictException(
                    "Permission is already assigned to role"
            );
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        RolePermission savedRolePermission = rolePermissionRepository.save(rolePermission);

        log.info(
                "Permission {} assigned successfully to role {}",
                permission.getId(),
                roleId
        );

        return rolePermissionMapper.toResponse(savedRolePermission);
    }

    @Override
    public List<RolePermissionResponse> getRolePermissions(Long roleId) {
        roleRepository
                .findById(roleId)
                .orElseThrow(() -> {
                    log.warn("Role not found with id {}", roleId);
                    return new ResourceNotFoundException(
                            "Role not found with id: " + roleId);
                });

        List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(roleId);

        log.info(
                "Retrieved {} permissions for role {}",
                rolePermissions.size(),
                roleId
        );

        return rolePermissions
                .stream()
                .map(rolePermissionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removePermission(Long roleId, Long permissionId) {
        roleRepository
                .findById(roleId)
                .orElseThrow(() -> {
                    log.warn("Role not found with id {}", roleId);
                    return new ResourceNotFoundException(
                            "Role not found with id: " + roleId);
                });

        permissionRepository
                .findById(permissionId)
                .orElseThrow(() -> {
                    log.warn("Permission not found with id {}", permissionId);
                    return new ResourceNotFoundException(
                            "Permission not found with id: " + permissionId);
                });

        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            log.warn(
                    "Permission {} is not assigned to role {}",
                    permissionId,
                    roleId
            );
            throw new ResourceNotFoundException(
                    "Permission is not assigned to role"
            );
        }

        rolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, permissionId);

        log.info(
                "Permission {} removed successfully from role {}",
                permissionId,
                roleId
        );
    }
}
