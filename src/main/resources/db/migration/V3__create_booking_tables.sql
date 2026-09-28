CREATE TABLE branches
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE customers
(
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(255),
    address VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE pets
(
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(255),
    gender VARCHAR(30),
    birth_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pet_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id)
            ON DELETE CASCADE
);


CREATE TABLE appointments
(
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,

    customer_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,

    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,

    visit_type VARCHAR(30) NOT NULL,
    visit_address VARCHAR(500),

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    note TEXT,
    admin_note TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appointment_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id),

    CONSTRAINT fk_appointment_pet
        FOREIGN KEY (pet_id)
            REFERENCES pets(id),

    CONSTRAINT fk_appointment_service
        FOREIGN KEY (service_id)
            REFERENCES services(id),

    CONSTRAINT fk_appointment_branch
        FOREIGN KEY (branch_id)
            REFERENCES branches(id)
);
