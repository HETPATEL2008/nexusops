CREATE TABLE suppliers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,

                           legal_name VARCHAR(200) NOT NULL,
                           display_name VARCHAR(200),

                           supplier_code VARCHAR(50) NOT NULL,

                           tax_id VARCHAR(50),
                           registration_number VARCHAR(100),

                           website VARCHAR(255),

                           address_line1 VARCHAR(200),
                           address_line2 VARCHAR(200),
                           city VARCHAR(100),
                           state VARCHAR(100),
                           postal_code VARCHAR(20),
                           country_code CHAR(2),

                           notes TEXT,

                           created_at TIMESTAMP NOT NULL,
                           created_by BIGINT NOT NULL,
                           updated_at TIMESTAMP NOT NULL,
                           updated_by BIGINT NOT NULL,

                           deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           deleted_at TIMESTAMP NULL,

                           CONSTRAINT uk_suppliers_supplier_code
                               UNIQUE (supplier_code)
);

CREATE INDEX idx_suppliers_legal_name
    ON suppliers (legal_name);

CREATE INDEX idx_suppliers_display_name
    ON suppliers (display_name);

CREATE INDEX idx_suppliers_tax_id
    ON suppliers (tax_id);

CREATE INDEX idx_suppliers_country_code
    ON suppliers (country_code);
