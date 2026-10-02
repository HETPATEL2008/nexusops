package com.hetpatel.nexusops.organization.service.impl;

import com.hetpatel.nexusops.identity.role.entity.Role;

import com.hetpatel.nexusops.identity.role.enums.PredefinedRole;

import com.hetpatel.nexusops.identity.role.repository.RoleRepository;

import com.hetpatel.nexusops.organization.entity.Organization;

import com.hetpatel.nexusops.organization.service.OrganizationProvisioningService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationProvisioningServiceImpl implements OrganizationProvisioningService {

    private final RoleRepository roleRepository;

    @Override
    public void provisionDefaultRoles(Organization organization) {

        List<Role> roles = Arrays
                .stream(PredefinedRole.values())
                .map(predefinedRole -> createRole(organization, predefinedRole))
                .toList();

        roleRepository.saveAll(roles);
    }

    private Role createRole(Organization organization,
                            PredefinedRole predefinedRole) {

        Role role = new Role();

        role.setOrganization(organization);
        role.setName(predefinedRole.name());
        role.setDescription(predefinedRole.getDescription());

        return role;
    }
}
