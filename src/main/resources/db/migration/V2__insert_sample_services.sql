INSERT INTO services
(
    name,
    slug,
    summary,
    description,
    price_from,
    price_to,
    duration_minutes,
    image_url,
    active
)
VALUES
    (
        'Khám & Điều trị',
        'kham-dieu-tri',
        'Khám sức khỏe và điều trị bệnh cho thú cưng.',
        'Dịch vụ khám tổng quát, chẩn đoán và điều trị bệnh cho chó mèo.',
        100000,
        NULL,
        30,
        '/images/services/examination.jpg',
        TRUE
    ),
    (
        'Tiêm phòng',
        'tiem-phong',
        'Tiêm vaccine định kỳ cho chó mèo.',
        'Các chương trình vaccine giúp phòng ngừa bệnh truyền nhiễm.',
        150000,
        500000,
        20,
        '/images/services/vaccine.jpg',
        TRUE
    ),
    (
        'Triệt sản',
        'triet-san',
        'Triệt sản chó mèo an toàn.',
        'Quy trình triệt sản với kiểm tra sức khỏe trước và sau phẫu thuật.',
        500000,
        1500000,
        60,
        '/images/services/neutering.jpg',
        TRUE
    ),
    (
        'Spa & Grooming',
        'spa-grooming',
        'Chăm sóc và vệ sinh thú cưng.',
        'Tắm, vệ sinh tai, cắt móng và chăm sóc lông.',
        200000,
        800000,
        60,
        '/images/services/spa.jpg',
        TRUE
    ),
    (
        'Khách sạn thú cưng',
        'khach-san-thu-cung',
        'Dịch vụ lưu trú dành cho thú cưng.',
        'Không gian an toàn dành cho thú cưng khi chủ đi công tác hoặc du lịch.',
        250000,
        NULL,
        1440,
        '/images/services/hotel.jpg',
        TRUE
    );
