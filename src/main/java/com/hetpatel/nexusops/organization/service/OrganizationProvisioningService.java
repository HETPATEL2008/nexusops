package com.hetpatel.nexusops.organization.service;

import com.hetpatel.nexusops.organization.entity.Organization;

public interface OrganizationProvisioningService {

    void provisionDefaultRoles(Organization organization);
}
