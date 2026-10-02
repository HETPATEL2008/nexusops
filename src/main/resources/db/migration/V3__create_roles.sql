CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    last_updated_by BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT fk_roles_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations(id),

    CONSTRAINT uk_roles_organization_name
        UNIQUE (organization_id, name)
);
