package com.hetpatel.nexusops.organization.repository;

import com.hetpatel.nexusops.common.persistence.AuditConfig;

import com.hetpatel.nexusops.organization.entity.Organization;
import com.hetpatel.nexusops.organization.entity.OrganizationStatus;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(AuditConfig.class)
class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void existsByCode_shouldReturnTrue_whenOrganizationExists() {

        Organization organization = new Organization();

        organization.setName("Test Organization");
        organization.setCode("TEST001");
        organization.setEmail("test@example.com");
        organization.setStatus(OrganizationStatus.ACTIVE);

        organizationRepository.save(organization);

        boolean exists =
                organizationRepository.existsByCode("TEST001");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByCode_shouldReturnFalse_whenOrganizationDoesNotExist() {

        boolean exists =
                organizationRepository.existsByCode("NOT_FOUND");

        assertThat(exists).isFalse();
    }

    @Test
    void restoreById_shouldRestoreDeletedOrganization() {

        Organization organization = new Organization();

        organization.setName("Deleted Organization");
        organization.setCode("DELETED001");
        organization.setEmail("deleted@example.com");
        organization.setStatus(OrganizationStatus.ACTIVE);

        Organization savedOrganization =
                organizationRepository.save(organization);

        Long organizationId = savedOrganization.getId();

        organizationRepository.delete(savedOrganization);

        entityManager.flush();
        entityManager.clear();

        int restoredRows =
                organizationRepository.restoreById(organizationId);

        assertThat(restoredRows).isEqualTo(1);

        entityManager.flush();
        entityManager.clear();

        Organization restoredOrganization =
                organizationRepository.findById(organizationId)
                        .orElseThrow();

        assertThat(restoredOrganization.getId())
                .isEqualTo(organizationId);

        assertThat(restoredOrganization.getDeleted())
                .isFalse();

        assertThat(restoredOrganization.getDeletedAt())
                .isNull();
    }

    @Test
    void restoreById_shouldReturnZero_whenOrganizationDoesNotExist() {

        int restoredRows =
                organizationRepository.restoreById(999999L);

        assertThat(restoredRows).isZero();
    }
}
