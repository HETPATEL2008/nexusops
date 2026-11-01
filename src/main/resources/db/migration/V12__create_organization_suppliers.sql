
CREATE TABLE organization_suppliers (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                        organization_id BIGINT NOT NULL,
                                        supplier_id BIGINT NOT NULL,

                                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                        risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',

                                        notes TEXT,

                                        created_at TIMESTAMP NOT NULL,
                                        created_by BIGINT NOT NULL,
                                        updated_at TIMESTAMP NOT NULL,
                                        last_updated_by BIGINT NOT NULL,

                                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                        deleted_at TIMESTAMP NULL,

                                        CONSTRAINT uk_org_suppliers_org_supplier
                                            UNIQUE (organization_id, supplier_id),

                                        CONSTRAINT fk_org_suppliers_organization
                                            FOREIGN KEY (organization_id)
                                                REFERENCES organizations (id),

                                        CONSTRAINT fk_org_suppliers_supplier
                                            FOREIGN KEY (supplier_id)
                                                REFERENCES suppliers (id),

                                        CONSTRAINT chk_org_suppliers_status
                                            CHECK (
                                                status IN (
                                                           'PENDING',
                                                           'ACTIVE',
                                                           'INACTIVE',
                                                           'SUSPENDED',
                                                           'BLOCKED'
                                                    )
                                                ),

                                        CONSTRAINT chk_org_suppliers_risk_level
                                            CHECK (
                                                risk_level IN (
                                                               'LOW',
                                                               'MEDIUM',
                                                               'HIGH',
                                                               'CRITICAL'
                                                    )
                                                )
);

CREATE INDEX idx_org_suppliers_organization
    ON organization_suppliers (organization_id);

CREATE INDEX idx_org_suppliers_supplier
    ON organization_suppliers (supplier_id);

CREATE INDEX idx_org_suppliers_status
    ON organization_suppliers (status);
