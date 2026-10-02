package com.hetpatel.nexusops.identity.permission.service.impl;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.permission.dto.response.PermissionResponse;

import com.hetpatel.nexusops.identity.permission.entity.Permission;

import com.hetpatel.nexusops.identity.permission.mapper.PermissionMapper;

import com.hetpatel.nexusops.identity.permission.repository.PermissionRepository;

import com.hetpatel.nexusops.identity.permission.service.PermissionService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse getPermissionById(Long id) {

        Permission permission = permissionRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn("Permission not found: id={}", id);
                    return new ResourceNotFoundException(
                            "Permission not found with id: " + id
                    );
                });

        log.info(
                "Permission fetched successfully by id: id={}, name={}",
                permission.getId(),
                permission.getName()
        );


        return permissionMapper.toResponse(permission);
    }

    @Override
    public PageResponse<PermissionResponse> getAllPermissions(Pageable pageable) {

        Page<Permission> permissionPage = permissionRepository.findAll(pageable);

        log.info(
                "Permissions fetched successfully: page={}, size={}, totalElements={}",
                permissionPage.getNumber(),
                permissionPage.getSize(),
                permissionPage.getTotalElements()
        );

        return new PageResponse<>(
                permissionPage.getContent()
                        .stream()
                        .map(permissionMapper::toResponse)
                        .toList(),
                permissionPage.getNumber(),
                permissionPage.getSize(),
                permissionPage.getTotalElements(),
                permissionPage.getTotalPages(),
                permissionPage.isLast()
        );
    }

    @Override
    public PermissionResponse getPermissionByName(String name) {

        Permission permission = permissionRepository
                .findByName(name)
                .orElseThrow(() -> {
                    log.warn("Permission not found: name={}", name);
                    return new ResourceNotFoundException(
                            "Permission not found with name: " + name
                    );
                });

        log.info(
                "Permission fetched successfully by name: id={}, name={}",
                permission.getId(),
                permission.getName()
        );

        return permissionMapper.toResponse(permission);
    }
}
