package com.hetpatel.nexusops.common.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    /*
     * Registers the auditing provider with Spring.
     *
     * Spring Data JPA uses AuditorAware<Long> to determine
     * the ID of the user who is performing the current operation.
     *
     * The returned Long will eventually be used for:
     * - createdBy
     * - lastUpdatedBy
     *
     * CURRENTLY:
     * We do not have authentication/security or the User module yet,
     * so a temporary fixed user ID is returned.
     *
     * LATER:
     * Once authentication is implemented, this will be changed to
     * retrieve the currently authenticated user's ID from the
     * Spring Security context.
     */

    @Bean
    public AuditorAware<Long> auditorProvider() {

        /*
         * Temporary implementation.
         *
         * 1L represents a temporary system/user ID during development.
         */

        return () -> Optional.of(1L);
    }
}
