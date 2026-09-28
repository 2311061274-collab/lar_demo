-- Bổ sung trường phản hồi cho tin nhắn liên hệ
ALTER TABLE contact_messages ADD COLUMN IF NOT EXISTS reply_message TEXT;
ALTER TABLE contact_messages ADD COLUMN IF NOT EXISTS replied_at TIMESTAMP;

-- Tạo bảng reviews phục vụ Use case Đánh giá & Bình luận của Khách hàng
CREATE TABLE IF NOT EXISTS reviews
(
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    service_name VARCHAR(255),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu đánh giá ban đầu
INSERT INTO reviews (customer_name, phone, service_name, rating, comment, created_at, updated_at)
VALUES
    ('Nguyễn Văn Tuấn', '0912345678', 'Khám & điều trị chó mèo', 5, 'Bác sĩ rất nhẹ nhàng, khám kỹ và tư vấn tận tình cho bé Corgi nhà mình.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Trần Thị Mai', '0987654321', 'Spa & Grooming làm đẹp', 5, 'Dịch vụ spa tuyệt vời, bé mèo tắm xong thơm tho và lông mềm mượt.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Lê Hoàng Nam', '0933445566', 'Bác sĩ thú y đến nhà', 5, 'Bác sĩ đến đúng giờ, xử lý cấp cứu kịp thời cho cún lúc đêm muộn. Rất cảm ơn phòng khám!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
