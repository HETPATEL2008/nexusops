CREATE TABLE supplier_contacts
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    supplier_id BIGINT NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    designation VARCHAR(100),
    department VARCHAR(100),

    email VARCHAR(255),

    phone_country_code VARCHAR(5),
    phone_number VARCHAR(20),

    alternate_phone_country_code VARCHAR(5),
    alternate_phone_number VARCHAR(20),

    contact_type VARCHAR(30) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    notes TEXT,

    created_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    last_updated_by BIGINT NOT NULL,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_supplier_contacts_supplier
        FOREIGN KEY (supplier_id)
            REFERENCES suppliers (id),

    CONSTRAINT chk_supplier_contacts_communication
        CHECK (
            email IS NOT NULL
                OR phone_number IS NOT NULL
                OR alternate_phone_number IS NOT NULL
            ),

    CONSTRAINT chk_supplier_contacts_phone_country_code
        CHECK (
            phone_number IS NULL
                OR phone_country_code IS NOT NULL
            ),

    CONSTRAINT chk_supplier_contacts_alternate_phone_country_code
        CHECK (
            alternate_phone_number IS NULL
                OR alternate_phone_country_code IS NOT NULL
            ),

    CONSTRAINT chk_supplier_contacts_type
        CHECK (
            contact_type IN (
                             'GENERAL',
                             'SALES',
                             'FINANCE',
                             'TECHNICAL',
                             'OPERATIONS',
                             'LOGISTICS',
                             'SUPPORT'
                )
            ),

    CONSTRAINT chk_supplier_contacts_status
        CHECK (
            status IN (
                       'ACTIVE',
                       'INACTIVE'
                )
            ),

    INDEX idx_supplier_contacts_supplier_id (supplier_id),
    INDEX idx_supplier_contacts_email (email),
    INDEX idx_supplier_contacts_type (contact_type),
    INDEX idx_supplier_contacts_status (status)
);
