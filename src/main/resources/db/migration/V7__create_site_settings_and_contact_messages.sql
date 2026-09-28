CREATE TABLE site_settings
(
    id BIGINT PRIMARY KEY,

    clinic_name VARCHAR(255) NOT NULL,

    tagline VARCHAR(500),

    about_title VARCHAR(255),

    about_content TEXT,

    address VARCHAR(500),

    phone VARCHAR(50),

    email VARCHAR(255),

    opening_hours VARCHAR(500),

    facebook_url VARCHAR(500),

    map_url VARCHAR(1000),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO site_settings
(
    id,
    clinic_name,
    tagline,
    about_title,
    about_content,
    address,
    phone,
    email,
    opening_hours
)
VALUES
    (
        1,
        'PetCare Veterinary Center',
        'Chăm sóc thú cưng bằng sự tận tâm và chuyên nghiệp',
        'Về PetCare Veterinary Center',
        'PetCare cung cấp các dịch vụ khám, điều trị, tiêm phòng, chăm sóc và lưu trú dành cho thú cưng.',
        'Hà Nội',
        '024 1234 5678',
        'contact@petcare.vn',
        'Thứ 2 - Chủ nhật: 08:00 - 20:00'
    );


CREATE TABLE contact_messages
(
    id BIGSERIAL PRIMARY KEY,

    full_name VARCHAR(255) NOT NULL,

    phone VARCHAR(50) NOT NULL,

    email VARCHAR(255),

    subject VARCHAR(255),

    message TEXT NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'NEW',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_contact_status
        CHECK (
            status IN (
                       'NEW',
                       'READ',
                       'REPLIED'
                )
            )
);
