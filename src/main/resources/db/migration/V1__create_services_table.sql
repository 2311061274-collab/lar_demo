CREATE TABLE services
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    slug VARCHAR(255) NOT NULL UNIQUE,

    summary VARCHAR(500),

    description TEXT,

    price_from NUMERIC(12, 2),

    price_to NUMERIC(12, 2),

    duration_minutes INTEGER,

    image_url VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
