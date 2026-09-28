-- Update Site Settings
UPDATE site_settings
SET
    clinic_name = 'Bệnh Viện Thú Y PetCare (VetCenter)',
    tagline = 'Chăm sóc Boss như người thân bằng cả trái tim',
    about_title = 'Về Bệnh Viện Thú Y PetCare',
    about_content = 'PetCare là hệ thống bệnh viện thú y uy tín tại Hà Nội và Quảng Ninh, cung cấp toàn diện dịch vụ khám chữa bệnh, bác sĩ thú y đến tận nhà, cấp cứu 24/7, phẫu thuật, spa cắt tỉa và khách sạn thú cưng. Chúng tôi cam kết mang lại sự an tâm tuyệt đối cho các bạn thú cưng và người nuôi.',
    address = '8 Khuất Duy Tiến, Thanh Xuân, Hà Nội',
    phone = '0383.553.886',
    email = 'cskh@vetcenter.vn',
    opening_hours = 'Thứ 2 – Chủ Nhật: 9.00 am – 8.00pm (Cấp cứu 24/7)',
    facebook_url = 'https://www.facebook.com/vetcenter.thanhxuan',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Update existing Branches to VetCenter branches (avoid foreign key violation)
UPDATE branches
SET name = 'Chi nhánh Thanh Xuân, Hà Nội - Cấp cứu 24/7',
    address = '8 Khuất Duy Tiến, Thanh Xuân, Hà Nội',
    phone = '0383.553.886',
    active = TRUE
WHERE id = 1;

UPDATE branches
SET name = 'Chi nhánh Hòn Gai, Hạ Long - Cấp cứu 24/7',
    address = '22 Tôn Thất Thuyết, Hạ Long, Quảng Ninh',
    phone = '0569.219.268',
    active = TRUE
WHERE id = 2;

UPDATE branches
SET name = 'Chi nhánh Bãi Cháy, Hạ Long - Cấp cứu 24/7',
    address = '728 Hạ Long, Bãi Cháy, Quảng Ninh',
    phone = '0569.219.268',
    active = TRUE
WHERE id = 3;

-- Update or Insert Services using ON CONFLICT (slug) DO UPDATE
INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Bác sĩ thú y đến nhà',
    'bac-si-thu-y-den-nha',
    'Khám bệnh, tiêm phòng, lấy mẫu xét nghiệm, chăm sóc hậu phẫu tại nhà chỉ sau 30 phút hẹn.',
    'Dịch vụ bác sĩ thú y tận nhà giúp các bé không phải di chuyển xa khi đang mệt, giảm căng thẳng tối đa cho thú cưng và tiết kiệm thời gian cho chủ nuôi.',
    150000, 500000, 45,
    'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-6-1024x576.png',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Khám & Điều trị tại bệnh viện',
    'kham-dieu-tri-cho-meo',
    'Khám tổng quát, nội khoa, ngoại khoa, điều trị bệnh truyền nhiễm chuyên sâu.',
    'Đội ngũ bác sĩ chuyên khoa với đầy đủ trang thiết bị xét nghiệm, chẩn đoán hình ảnh hiện đại giúp phát hiện và điều trị dứt điểm các bệnh lý phức tạp.',
    100000, 800000, 30,
    'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-2-1-1024x576.jpg',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

-- Also update existing 'kham-dieu-tri' slug if present
UPDATE services
SET
    name = 'Khám & Điều trị tại bệnh viện',
    summary = 'Khám tổng quát, nội khoa, ngoại khoa, điều trị bệnh truyền nhiễm chuyên sâu.',
    image_url = 'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-2-1-1024x576.jpg'
WHERE slug = 'kham-dieu-tri';

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Tiêm phòng vaccine & Tẩy giun',
    'tiem-phong-vaccine',
    'Phòng ngừa các bệnh nguy hiểm cho chó mèo theo đúng phác đồ tiêm chủng chuẩn quốc tế.',
    'Vaccine nhập khẩu chính hãng bảo quản theo tiêu chuẩn nghiêm ngặt. Kèm sổ tiêm và nhắc lịch định kỳ miễn phí.',
    150000, 450000, 20,
    'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-3-1024x576.png',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

-- Also update 'tiem-phong' slug if present
UPDATE services
SET
    name = 'Tiêm phòng vaccine & Tẩy giun',
    summary = 'Phòng ngừa các bệnh nguy hiểm cho chó mèo theo đúng phác đồ tiêm chủng chuẩn quốc tế.',
    image_url = 'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-3-1024x576.png'
WHERE slug = 'tiem-phong';

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Triệt sản chó mèo an toàn',
    'triet-san-cho-meo',
    'Phẫu thuật triệt sản an toàn, ít đau, theo dõi hồi phục tận tình bởi đội ngũ bác sĩ thú y.',
    'Phẫu thuật vô trùng tuyệt đối, sử dụng chỉ tự tiêu thẩm mỹ, theo dõi hậu phẫu chu đáo giúp bé nhanh chóng bình phục.',
    400000, 1200000, 60,
    'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-2-5-1024x576.jpg',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

-- Also update 'triet-san' slug if present
UPDATE services
SET
    name = 'Triệt sản chó mèo an toàn',
    summary = 'Phẫu thuật triệt sản an toàn, ít đau, theo dõi hồi phục tận tình bởi đội ngũ bác sĩ thú y.',
    image_url = 'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-2-5-1024x576.jpg'
WHERE slug = 'triet-san';

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Spa & Grooming làm đẹp',
    'spa-grooming',
    'Tắm dưỡng sinh, sấy tạo phồng, cắt tỉa lông thẩm mỹ, vệ sinh tai móng trọn gói.',
    'Sử dụng các dòng sữa tắm dưỡng lông cao cấp, kỹ thuật viên spa nhẹ nhàng, yêu thương thú cưng.',
    150000, 600000, 90,
    'https://vetcenter.vn/wp-content/uploads/2024/02/vetcenter-tam-cat-thu-cung.png',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Khách sạn thú cưng lưu trú',
    'khach-san-thu-cung',
    'Dịch vụ lưu trú ngắn hạn và dài hạn, phòng riêng sạch sẽ khử trùng, điều hòa 24/7.',
    'Không gian lưu trú thoáng mát, chế độ dinh dưỡng khoa học theo yêu cầu, nhân viên cập nhật hình ảnh hàng ngày.',
    120000, 350000, 1440,
    'https://vetcenter.vn/wp-content/uploads/2026/06/iStock-1143749718-Copy-1024x765.jpg',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Cấp cứu thú cưng 24/24',
    'cap-cuu-thu-cung-24-24',
    'Đội ngũ bác sĩ cấp cứu thường trực 24/7 xử lý nhanh ngộ độc, chấn thương, khó đẻ, sốc nhiệt.',
    'Trang bị hệ thống thở oxy, máy theo dõi sinh tồn, phòng phẫu thuật cấp cứu luôn sẵn sàng 24/24.',
    200000, 1500000, 60,
    'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-5-1024x576.png',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

INSERT INTO services (name, slug, summary, description, price_from, price_to, duration_minutes, image_url, active)
VALUES
(
    'Xét nghiệm & Chẩn đoán hình ảnh',
    'xet-nghiem-chan-doan',
    'Máu, sinh hóa, nước tiểu, ký sinh trùng, siêu âm màu và chẩn đoán chuyên sâu.',
    'Hệ thống máy xét nghiệm huyết học tự động, máy siêu âm màu hiện đại cho kết quả chính xác trong vòng 15-30 phút.',
    150000, 900000, 30,
    'https://vetcenter.vn/wp-content/uploads/2026/06/vetcenter-background-4-1024x576.png',
    TRUE
)
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name,
    summary = EXCLUDED.summary,
    description = EXCLUDED.description,
    price_from = EXCLUDED.price_from,
    price_to = EXCLUDED.price_to,
    duration_minutes = EXCLUDED.duration_minutes,
    image_url = EXCLUDED.image_url,
    active = EXCLUDED.active;

-- Update or Insert News
INSERT INTO news (title, slug, summary, content, image_url, published, published_at)
VALUES
(
    'Cách trị chó bị rụng lông và ngứa lâu ngày không khỏi',
    'cach-tri-cho-bi-rung-long-va-ngua-lau-ngay-khong-khoi',
    'Tìm hiểu nguyên nhân khiến chó bị rụng lông kèm ngứa ngáy dữ dội do viêm da, nấm, ve rận và phác đồ điều trị hiệu quả tại nhà.',
    '<p>Rụng lông và ngứa ở chó là một trong những vấn đề phổ biến nhất mà các chủ nuôi hay gặp phải. Nếu tình trạng kéo dài, có thể dẫn đến nhiễm trùng da thứ phát và ảnh hưởng nghiêm trọng đến sức khỏe của thú cưng.</p><h3>1. Các nguyên nhân phổ biến</h3><p>- Nhiễm ký sinh trùng ngoài da: Ve, rận, bọ chét hoặc cái ghẻ Demodex/Sarcoptes.<br>- Nhiễm nấm da: Gây rụng lông từng mảng tròn, da đóng vảy đỏ.<br>- Dị ứng thức ăn hoặc môi trường sống.</p><h3>2. Cách xử lý và phòng ngừa</h3><p>Cần đưa bé đến phòng khám để cạo da soi kính hiển vi xác định đúng loại ký sinh trùng hoặc nấm trước khi bôi thuốc. Tắm bằng dầu tắm đặc trị và giữ môi trường sống khô ráo, sạch sẽ.</p>',
    'https://vetcenter.vn/wp-content/uploads/2026/06/iStock-1143749718-Copy-768x574.jpg',
    TRUE,
    CURRENT_TIMESTAMP
)
ON CONFLICT (slug) DO UPDATE SET
    title = EXCLUDED.title,
    summary = EXCLUDED.summary,
    content = EXCLUDED.content,
    image_url = EXCLUDED.image_url,
    published = EXCLUDED.published;

INSERT INTO news (title, slug, summary, content, image_url, published, published_at)
VALUES
(
    'Tai chó có mùi hôi: Hướng dẫn vệ sinh tai chó đúng cách',
    'tai-cho-co-mui-hoi-huong-dan-ve-sinh-tai-cho-dung-cach',
    'Nguyên nhân khiến tai chó có mùi hôi khó chịu và cách vệ sinh tai an toàn, không làm tổn thương màng nhĩ của cún cưng.',
    '<p>Tai chó có cấu tạo hình chữ L sâu bên trong, là môi trường lý tưởng cho bụi bẩn, độ ẩm và vi khuẩn phát triển nếu không được vệ sinh định kỳ.</p><h3>1. Dấu hiệu tai chó bị viêm nhiễm</h3><p>- Chó thường xuyên lắc đầu, gãi tai liên tục.<br>- Tai có mùi hôi chua hoặc tanh nồng, xuất hiện nhiều chất bẩn màu nâu sẫm.<br>- Vành tai đỏ ửng, sưng đau khi chạm vào.</p><h3>2. Quy trình vệ sinh tai an toàn</h3><p>Sử dụng dung dịch rửa tai chuyên dụng cho thú cưng, nhỏ vào ống tai và massage nhẹ nhàng góc tai trong 30 giây để chất bẩn tan ra, sau đó dùng bông gòn mềm lau sạch phần ngoài.</p>',
    'https://vetcenter.vn/wp-content/uploads/2022/07/pexels-helena-lopes-4009986-768x535.jpg',
    TRUE,
    CURRENT_TIMESTAMP
)
ON CONFLICT (slug) DO UPDATE SET
    title = EXCLUDED.title,
    summary = EXCLUDED.summary,
    content = EXCLUDED.content,
    image_url = EXCLUDED.image_url,
    published = EXCLUDED.published;

INSERT INTO news (title, slug, summary, content, image_url, published, published_at)
VALUES
(
    'Bệnh Parvo ở chó có lây sang người không? Dấu hiệu nhận biết',
    'benh-parvo-o-cho-co-lay-sang-nguoi-khong-dau-hieu-nhan-biet',
    'Bệnh viêm ruột truyền nhiễm Parvovirus ở chó nguy hiểm như thế nào, tỷ lệ sống sót ra sao và liệu có lây lan sang người không?',
    '<p>Canine Parvovirus (Parvo) là một trong những bệnh truyền nhiễm nguy hiểm nhất ở loài chó, đặc biệt là chó con từ 1 - 6 tháng tuổi chưa được tiêm đủ vaccine.</p><h3>1. Bệnh có lây sang người không?</h3><p><strong>Không!</strong> Virus Parvo ở chó là chủng đặc hiệu chỉ gây bệnh trên loài chó và động vật họ chó, hoàn toàn KHÔNG lây lan sang con người.</p><h3>2. Dấu hiệu nhận biết khẩn cấp</h3><p>- Chó mệt mỏi, bỏ ăn, nằm ủ rũ.<br>- Nôn mửa liên tục ra dịch màu vàng hoặc bọt trắng.<br>- Tiêu chảy cấp, phân có mùi tanh đặc trưng và lẫn máu tươi.<br>- Sốt cao rồi hạ thân nhiệt nhanh chóng.</p><p>Khi phát hiện dấu hiệu trên, hãy liên hệ ngay hotline cấp cứu 24/7 của PetCare để được điều trị kịp thời!</p>',
    'https://vetcenter.vn/wp-content/uploads/2022/05/pexels-aysun-kahraman-oktem-5788341-768x575.jpg',
    TRUE,
    CURRENT_TIMESTAMP
)
ON CONFLICT (slug) DO UPDATE SET
    title = EXCLUDED.title,
    summary = EXCLUDED.summary,
    content = EXCLUDED.content,
    image_url = EXCLUDED.image_url,
    published = EXCLUDED.published;
