package com.hetpatel.nexusops.identity.role.service.impl;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.common.exception.ConflictException;
import com.hetpatel.nexusops.common.exception.ResourceNotFoundException;

import com.hetpatel.nexusops.identity.role.dto.request.CreateRoleRequest;
import com.hetpatel.nexusops.identity.role.dto.request.UpdateRoleRequest;

import com.hetpatel.nexusops.identity.role.dto.response.RoleResponse;

import com.hetpatel.nexusops.identity.role.entity.Role;

import com.hetpatel.nexusops.identity.role.mapper.RoleMapper;

import com.hetpatel.nexusops.identity.role.repository.RoleRepository;

import com.hetpatel.nexusops.identity.role.service.RoleService;

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
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    private final OrganizationRepository organizationRepository;

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn(
                            "Role creation failed: organization not found, id={}",
                            request.getOrganizationId()
                    );

                    return new ResourceNotFoundException(
                            "Organization not found with id: "
                                    + request.getOrganizationId()
                    );
                });

        if (roleRepository.existsByOrganizationIdAndName(
                request.getOrganizationId(),
                request.getName())) {

            log.warn(
                    "Role creation failed: role already exists, organizationId={}, name={}",
                    request.getOrganizationId(),
                    request.getName()
            );

            throw new ConflictException(
                    "Role with name '" + request.getName()
                            + "' already exists in this organization."
            );
        }

        Role role = roleMapper.toEntity(request);

        role.setOrganization(organization);

        Role savedRole = roleRepository.save(role);

        log.info(
                "Role created successfully: id={}, name={}, organizationId={}",
                savedRole.getId(),
                savedRole.getName(),
                organization.getId()
        );

        return roleMapper.toResponse(savedRole);
    }

    @Override
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Role not found: id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Role not found with id: " + id
                    );
                });

        return roleMapper.toResponse(role);
    }

    @Override
    public PageResponse<RoleResponse> getAllRoles(Pageable pageable) {

        Page<Role> rolePage = roleRepository.findAll(pageable);

        List<RoleResponse> content =
                rolePage.getContent()
                        .stream()
                        .map(roleMapper::toResponse)
                        .toList();

        log.info(
                "Roles retrieved: page={}, size={}, totalElements={}",
                rolePage.getNumber(),
                rolePage.getSize(),
                rolePage.getTotalElements()
        );

        return new PageResponse<>(
                content,
                rolePage.getNumber(),
                rolePage.getSize(),
                rolePage.getTotalElements(),
                rolePage.getTotalPages(),
                rolePage.isLast()
        );
    }

    @Override
    public RoleResponse updateRole(
            Long id,
            UpdateRoleRequest request) {

        Role role = roleRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Role update failed: role not found, id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Role not found with id: " + id
                    );
                });

        Long organizationId = role.getOrganization() != null
                ? role.getOrganization().getId()
                : null;

        if (!role.getName().equals(request.getName())
                && organizationId != null
                && roleRepository.existsByOrganizationIdAndName(
                organizationId,
                request.getName())) {

            log.warn(
                    "Role update failed: role name already exists, " +
                            "organizationId={}, name={}",
                    organizationId,
                    request.getName()
            );

            throw new ConflictException(
                    "Role with name '" + request.getName()
                            + "' already exists in this organization."
            );
        }

        roleMapper.updateEntity(request, role);

        Role updatedRole = roleRepository.save(role);

        log.info(
                "Role updated successfully: id={}, name={}",
                updatedRole.getId(),
                updatedRole.getName()
        );

        return roleMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {

        Role role = roleRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Role deletion failed: role not found, id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Role not found with id: " + id
                    );
                });

        roleRepository.deleteById(id);

        log.info(
                "Role soft deleted successfully: id={}, name={}",
                role.getId(),
                role.getName()
        );
    }

    @Override
    @Transactional
    public RoleResponse restoreRole(Long id) {

        int restoredRows = roleRepository.restoreById(id);

        if (restoredRows == 0) {
            log.warn(
                    "Role restoration failed: deleted role not found, id={}",
                    id
            );

            throw new ResourceNotFoundException(
                    "Deleted role not found with id: " + id
            );
        }

        Role restoredRole =
                roleRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Role restoration failed: restored role not found, id={}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Role not found with id: " + id
                            );
                        });

        log.info(
                "Role restored successfully: id={}, name={}",
                restoredRole.getId(),
                restoredRole.getName()
        );

        return roleMapper.toResponse(restoredRole);
    }
}
