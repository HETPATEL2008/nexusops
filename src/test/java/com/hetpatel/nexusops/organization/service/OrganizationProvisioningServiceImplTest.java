package com.hetpatel.nexusops.organization.service;

import com.hetpatel.nexusops.identity.role.entity.Role;

import com.hetpatel.nexusops.identity.role.enums.PredefinedRole;

import com.hetpatel.nexusops.identity.role.repository.RoleRepository;

import com.hetpatel.nexusops.organization.entity.Organization;

import com.hetpatel.nexusops.organization.service.impl.OrganizationProvisioningServiceImpl;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrganizationProvisioningServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private OrganizationProvisioningServiceImpl provisioningService;

    @Test
    void shouldProvisionAllDefaultRolesForOrganization() {

        Organization organization = new Organization();
        organization.setId(1L);

        provisioningService.provisionDefaultRoles(organization);

        ArgumentCaptor<List<Role>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(roleRepository).saveAll(captor.capture());

        List<Role> roles = captor.getValue();

        assertEquals(
                PredefinedRole.values().length,
                roles.size()
        );

        for (int i = 0; i < roles.size(); i++) {

            Role role = roles.get(i);
            PredefinedRole predefinedRole =
                    PredefinedRole.values()[i];

            assertSame(
                    organization,
                    role.getOrganization()
            );

            assertEquals(
                    predefinedRole.name(),
                    role.getName()
            );

            assertEquals(
                    predefinedRole.getDescription(),
                    role.getDescription()
            );
        }
    }
}
