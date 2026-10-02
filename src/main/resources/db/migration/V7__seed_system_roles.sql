INSERT INTO roles (
    organization_id,
    name,
    description,
    created_at,
    updated_at,
    created_by,
    last_updated_by,
    deleted,
    deleted_at
)
VALUES
    (
        NULL,
        'SYSTEM_ADMIN',
        'Platform-level administrator with system-wide administrative access.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        1,
        FALSE,
        NULL
    );
