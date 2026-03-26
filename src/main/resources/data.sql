-- Roles
INSERT INTO roles (id, name, description, created_at, updated_at) VALUES
(1, 'ROLE_GUEST', 'Guest role', DATE_SUB(NOW(), INTERVAL 180 DAY), DATE_SUB(NOW(), INTERVAL 180 DAY)),
(2, 'ROLE_USER', 'User role', DATE_SUB(NOW(), INTERVAL 180 DAY), DATE_SUB(NOW(), INTERVAL 180 DAY)),
(3, 'ROLE_ADMIN', 'Admin role', DATE_SUB(NOW(), INTERVAL 180 DAY), DATE_SUB(NOW(), INTERVAL 180 DAY));

-- Users
-- Mật khẩu demo cho tất cả tài khoản bên dưới: password
INSERT INTO users (id, full_name, email, password, phone, avatar_url, enabled, created_at, updated_at) VALUES
(1, 'Platform Admin', 'admin@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6H2uC0BnrCj9lPA5E/5Q7m8bHqWG', '0900000001', 'https://i.pravatar.cc/300?img=12', b'1', DATE_SUB(NOW(), INTERVAL 180 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'Demo User', 'user@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6H2uC0BnrCj9lPA5E/5Q7m8bHqWG', '0900000002', 'https://i.pravatar.cc/300?img=32', b'1', DATE_SUB(NOW(), INTERVAL 120 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'Nguyen Thu Ngoc', 'ngoc@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6H2uC0BnrCj9lPA5E/5Q7m8bHqWG', '0900000003', 'https://i.pravatar.cc/300?img=47', b'1', DATE_SUB(NOW(), INTERVAL 110 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 'Tran Quang Minh', 'minh@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6H2uC0BnrCj9lPA5E/5Q7m8bHqWG', '0900000004', 'https://i.pravatar.cc/300?img=15', b'1', DATE_SUB(NOW(), INTERVAL 100 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'Test Disabled', 'disabled@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6H2uC0BnrCj9lPA5E/5Q7m8bHqWG', '0900000005', 'https://i.pravatar.cc/300?img=8', b'0', DATE_SUB(NOW(), INTERVAL 95 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY));

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2),
(1, 3),
(2, 2),
(3, 2),
(4, 2),
(5, 2);

-- Wallets
INSERT INTO wallets (id, user_id, balance, created_at, updated_at) VALUES
(1, 1, 0.00, DATE_SUB(NOW(), INTERVAL 180 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 2, 352000.00, DATE_SUB(NOW(), INTERVAL 120 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 3, 136000.00, DATE_SUB(NOW(), INTERVAL 110 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 4, 74000.00, DATE_SUB(NOW(), INTERVAL 100 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 5, 5000.00, DATE_SUB(NOW(), INTERVAL 95 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY));

-- Wallet transactions
INSERT INTO wallet_transactions (id, wallet_id, user_id, type, status, amount, balance_after, reference_code, description, created_at, updated_at) VALUES
(1, 2, 2, 'DEPOSIT', 'SUCCESS', 300000.00, 300000.00, 'SEED-DEP-0001', 'Nạp tiền ví lần đầu để test mua gói', DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY)),
(2, 2, 2, 'SUBSCRIPTION_PURCHASE', 'SUCCESS', 99000.00, 201000.00, 'SEED-SUB-0001', 'Mua gói Standard không dùng voucher', DATE_SUB(NOW(), INTERVAL 38 DAY), DATE_SUB(NOW(), INTERVAL 38 DAY)),
(3, 2, 2, 'DEPOSIT', 'SUCCESS', 250000.00, 451000.00, 'SEED-DEP-0002', 'Nạp thêm tiền qua VNPAY', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 2, 2, 'SUBSCRIPTION_PURCHASE', 'SUCCESS', 149000.00, 302000.00, 'SEED-SUB-0002', 'Mua gói Premium áp voucher PREMIUM50K', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 2, 2, 'ADJUSTMENT', 'SUCCESS', 50000.00, 352000.00, 'SEED-ADJ-0001', 'Admin cộng tiền hỗ trợ khách hàng', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 3, 3, 'DEPOSIT', 'SUCCESS', 200000.00, 200000.00, 'SEED-DEP-0003', 'Nạp tiền ví tài khoản Ngọc', DATE_SUB(NOW(), INTERVAL 65 DAY), DATE_SUB(NOW(), INTERVAL 65 DAY)),
(7, 3, 3, 'SUBSCRIPTION_PURCHASE', 'SUCCESS', 99000.00, 101000.00, 'SEED-SUB-0003', 'Mua gói Standard tháng trước', DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
(8, 3, 3, 'ADJUSTMENT', 'SUCCESS', 35000.00, 136000.00, 'SEED-ADJ-0002', 'Admin cộng tiền khuyến mại', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(9, 4, 4, 'DEPOSIT', 'SUCCESS', 180000.00, 180000.00, 'SEED-DEP-0004', 'Nạp tiền ví lần đầu', DATE_SUB(NOW(), INTERVAL 160 DAY), DATE_SUB(NOW(), INTERVAL 160 DAY)),
(10, 4, 4, 'SUBSCRIPTION_PURCHASE', 'SUCCESS', 150000.00, 30000.00, 'SEED-SUB-0004', 'Mua gói Premium đợt cũ', DATE_SUB(NOW(), INTERVAL 155 DAY), DATE_SUB(NOW(), INTERVAL 155 DAY)),
(11, 4, 4, 'DEPOSIT', 'SUCCESS', 120000.00, 150000.00, 'SEED-DEP-0005', 'Nạp tiền để gia hạn gói', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
(12, 4, 4, 'SUBSCRIPTION_PURCHASE', 'SUCCESS', 49000.00, 101000.00, 'SEED-SUB-0005', 'Mua gói Standard áp voucher PREMIUM50K', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(13, 4, 4, 'ADJUSTMENT', 'SUCCESS', 3000.00, 104000.00, 'SEED-ADJ-0003', 'Admin cộng bù chênh lệch', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(14, 4, 4, 'ADJUSTMENT', 'SUCCESS', -30000.00, 74000.00, 'SEED-ADJ-0004', 'Admin trừ tiền xử lý vi phạm', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(15, 5, 5, 'DEPOSIT', 'SUCCESS', 120000.00, 120000.00, 'SEED-DEP-0006', 'Nạp tiền tài khoản test', DATE_SUB(NOW(), INTERVAL 95 DAY), DATE_SUB(NOW(), INTERVAL 95 DAY)),
(16, 5, 5, 'SUBSCRIPTION_PURCHASE', 'SUCCESS', 99000.00, 21000.00, 'SEED-SUB-0006', 'Mua gói Standard đã hết hạn', DATE_SUB(NOW(), INTERVAL 90 DAY), DATE_SUB(NOW(), INTERVAL 90 DAY)),
(17, 5, 5, 'ADJUSTMENT', 'SUCCESS', -16000.00, 5000.00, 'SEED-ADJ-0005', 'Admin trừ tiền hoàn phí xử lý', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
(18, 2, 2, 'DEPOSIT', 'PENDING', 100000.00, 352000.00, 'SEED-DEP-0007', 'Giao dịch nạp tiền đang chờ VNPAY xác nhận', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- Payment transactions (giao dịch nạp tiền qua cổng thanh toán)
INSERT INTO payment_transactions (id, user_id, wallet_transaction_id, amount, provider, external_reference, status, created_at, updated_at) VALUES
(1, 2, 1, 300000.00, 'VNPAY', 'VNP-DEP-0001', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY)),
(2, 2, 3, 250000.00, 'VNPAY', 'VNP-DEP-0002', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 3, 6, 200000.00, 'VNPAY', 'VNP-DEP-0003', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 65 DAY), DATE_SUB(NOW(), INTERVAL 65 DAY)),
(4, 4, 9, 180000.00, 'VNPAY', 'VNP-DEP-0004', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 160 DAY), DATE_SUB(NOW(), INTERVAL 160 DAY)),
(5, 4, 11, 120000.00, 'VNPAY', 'VNP-DEP-0005', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
(6, 5, 15, 120000.00, 'VNPAY', 'VNP-DEP-0006', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 95 DAY), DATE_SUB(NOW(), INTERVAL 95 DAY)),
(7, 2, 18, 100000.00, 'VNPAY', 'VNP-DEP-0007', 'PENDING', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR));

INSERT INTO countries (id, name, created_at, updated_at) VALUES
(1, 'USA', NOW(), NOW()),
(2, 'South Korea', NOW(), NOW()),
(3, 'Japan', NOW(), NOW()),
(4, 'United Kingdom', NOW(), NOW()),
(5, 'Viet Nam', NOW(), NOW()),
(6, 'France', NOW(), NOW());

INSERT INTO genres (id, name, description, created_at, updated_at) VALUES
(1, 'Action', 'Nhiều cảnh hành động và nhịp độ cao', NOW(), NOW()),
(2, 'Drama', 'Tập trung vào cảm xúc và xung đột nội tâm', NOW(), NOW()),
(3, 'Sci-Fi', 'Khoa học viễn tưởng, công nghệ và tương lai', NOW(), NOW()),
(4, 'Thriller', 'Căng thẳng, hồi hộp, bất ngờ', NOW(), NOW()),
(5, 'Romance', 'Tình cảm và các mối quan hệ', NOW(), NOW()),
(6, 'Fantasy', 'Thế giới giả tưởng, ma thuật, sử thi', NOW(), NOW()),
(7, 'Crime', 'Tội phạm, phá án và điều tra', NOW(), NOW()),
(8, 'Adventure', 'Phiêu lưu và hành trình khám phá', NOW(), NOW()),
(9, 'Animation', 'Hoạt hình và phong cách hình ảnh độc đáo', NOW(), NOW()),
(10, 'Comedy', 'Hài hước và giải trí', NOW(), NOW());

INSERT INTO actors (id, name, avatar_url, created_at, updated_at) VALUES
(1, 'Ethan Cole', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(2, 'Sophia Park', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(3, 'Daniel Brooks', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(4, 'Minji Han', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(5, 'Kenji Sato', 'https://images.unsplash.com/photo-1504593811423-6dd665756598?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(6, 'Ava Laurent', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(7, 'Lucas Grant', 'https://images.unsplash.com/photo-1504257432389-52343af06ae3?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(8, 'Hana Mori', 'https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(9, 'Nguyen Bao An', 'https://images.unsplash.com/photo-1531123897727-8f129e1688ce?w=400&auto=format&fit=crop&q=60', NOW(), NOW()),
(10, 'Oliver Reed', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=400&auto=format&fit=crop&q=60', NOW(), NOW());

INSERT INTO directors (id, name, created_at, updated_at) VALUES
(1, 'Michael Stone', NOW(), NOW()),
(2, 'Jiwoo Kim', NOW(), NOW()),
(3, 'Akira Tanaka', NOW(), NOW()),
(4, 'Charlotte Reed', NOW(), NOW()),
(5, 'Tran Quoc Minh', NOW(), NOW()),
(6, 'Amelie Durand', NOW(), NOW());

INSERT INTO subscription_plans (id, name, access_level, price, duration_days, active, feature_description, created_at, updated_at) VALUES
(1, 'Free', 'FREE', 0, 30, b'1', 'Xem phim free;Truy cập trailer;Tìm kiếm và xem danh sách phim', NOW(), NOW()),
(2, 'Standard', 'STANDARD', 99000, 30, b'1', 'Xem phim Free + Standard;Ít quảng cáo hơn;Ưu tiên nội dung phổ biến', NOW(), NOW()),
(3, 'Premium', 'PREMIUM', 199000, 30, b'1', 'Mở khóa toàn bộ thư viện;Không quảng cáo;Ưu tiên nội dung độc quyền', NOW(), NOW());

INSERT INTO vouchers (id, code, name, description, discount_type, discount_value, max_discount_amount, min_order_amount, quantity, used_count, active, start_at, end_at, created_at, updated_at) VALUES
(1, 'GIAM10', 'Giảm 10% cho mọi gói', 'Giảm 10% cho đơn mua gói từ 99.000đ, tối đa 30.000đ', 'PERCENT', 10.00, 30000.00, 99000.00, 200, 7, b'1', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'PREMIUM50K', 'Giảm 50.000đ cho Premium', 'Áp dụng tốt cho gói Premium hoặc đơn từ 150.000đ', 'FIXED_AMOUNT', 50000.00, NULL, 150000.00, 80, 12, b'1', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'FLASH30K', 'Flash sale 30.000đ', 'Mã giảm nhanh cho đơn bất kỳ từ 99.000đ', 'FIXED_AMOUNT', 30000.00, NULL, 99000.00, 50, 20, b'1', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'HETHAN', 'Voucher đã hết hạn', 'Dùng để test trường hợp voucher hết hạn', 'FIXED_AMOUNT', 20000.00, NULL, 50000.00, 100, 30, b'0', DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'SAPDIENRA', 'Voucher sắp mở', 'Dùng để test voucher chưa đến thời gian áp dụng', 'PERCENT', 15.00, 40000.00, 99000.00, 60, 0, b'1', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NOW(), NOW()),
(6, 'HETLUOT', 'Voucher hết lượt dùng', 'Dùng để test trường hợp đã dùng hết số lượng', 'FIXED_AMOUNT', 10000.00, NULL, 50000.00, 5, 5, b'1', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));


INSERT INTO movies (id, title, original_title, slug, short_description, description, release_year, duration_minutes, movie_type, access_level, country_id, poster_url, backdrop_url, trailer_url, featured, popular, active, average_rating, view_count, created_at, updated_at) VALUES
(1, 'Neon Skyline', 'Neon Skyline', 'neon-skyline-1', 'Một cuộc truy đuổi cyberpunk trong thành phố không ngủ.', 'Trong một siêu đô thị phủ ánh đèn neon, một cựu đặc vụ phải bảo vệ chìa khóa dữ liệu có thể thay đổi cán cân quyền lực giữa các tập đoàn công nghệ.', 2025, 118, 'MOVIE', 'PREMIUM', 1, 'https://images.unsplash.com/photo-1513106580091-1d82408b8cd6?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1494526585095-c41746248156?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/aqz-KE-bpKQ', b'1', b'1', b'1', 4.8, 3021, NOW(), NOW()),
(2, 'Shadow District', 'Shadow District', 'shadow-district-2', 'Tổ trọng án bước vào mê cung của tội phạm đô thị.', 'Một đội điều tra đặc biệt cố gắng phá giải chuỗi án mạng kỳ lạ trong khu vực ngầm của thành phố. Mỗi manh mối đều kéo họ gần hơn tới một âm mưu đen tối.', 2024, 110, 'MOVIE', 'STANDARD', 2, 'https://images.unsplash.com/photo-1505685296765-3a2736de412f?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1477959858617-67f85cf4f1df?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/5PSNL1qE6VY', b'1', b'1', b'1', 4.5, 2103, NOW(), NOW()),
(3, 'Whispering Tides', 'Whispering Tides', 'whispering-tides-3', 'Một chuyện tình chữa lành bên bờ biển đầy bí mật.', 'Một nữ đạo diễn trở về thị trấn ven biển thời thơ ấu để phục hồi sau biến cố sự nghiệp, rồi gặp lại người đã thay đổi tuổi trẻ của cô.', 2025, 102, 'MOVIE', 'FREE', 4, 'https://images.unsplash.com/photo-1493246507139-91e8fad9978e?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/tgbNymZ7vqY', b'0', b'1', b'1', 4.3, 1840, NOW(), NOW()),
(4, 'Quantum Heist', 'Quantum Heist', 'quantum-heist-4', 'Phi vụ trộm xuyên thời gian đầy hiểm nguy.', 'Một nhóm chuyên gia công nghệ và lừa đảo lên kế hoạch đánh cắp thiết bị lượng tử trước khi nó bị quân sự hóa. Mỗi lần dịch chuyển thời gian là một lần họ đánh đổi tương lai.', 2026, 124, 'MOVIE', 'PREMIUM', 1, 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/ScMzIvxBSi4', b'1', b'1', b'1', 4.9, 4210, NOW(), NOW()),
(5, 'Midnight Seoul', 'Midnight Seoul', 'midnight-seoul-5', 'Đêm Seoul rực sáng và những số phận va vào nhau.', 'Một DJ ngầm, một công tố viên và một hacker tuổi teen bị cuốn vào cùng một đêm hỗn loạn làm đảo lộn thủ đô Seoul.', 2025, 54, 'SERIES', 'STANDARD', 2, 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1538485399081-7c8071ed23ff?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/aqz-KE-bpKQ', b'1', b'1', b'1', 4.6, 2764, NOW(), NOW()),
(6, 'Eclipse Protocol', 'Eclipse Protocol', 'eclipse-protocol-6', 'Điệp vụ bí mật trong kỷ nguyên giám sát toàn cầu.', 'Một chuyên gia an ninh mạng phát hiện chương trình giám sát toàn cầu và phải liên minh với kẻ thù cũ để ngăn một cuộc khủng hoảng dữ liệu.', 2024, 116, 'MOVIE', 'STANDARD', 4, 'https://images.unsplash.com/photo-1465821185615-20b3c2fbf41b?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1516321497487-e288fb19713f?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/5PSNL1qE6VY', b'0', b'1', b'1', 4.1, 1411, NOW(), NOW()),
(7, 'Sakura Memory', 'Sakura Memory', 'sakura-memory-7', 'Một bộ phim chữa lành về ký ức và mùa hoa anh đào.', 'Một nhà thiết kế game mất trí nhớ dần tìm lại những mảnh ghép cuộc đời qua cuốn nhật ký của chị gái và một thành phố ngập sắc sakura.', 2023, 108, 'MOVIE', 'FREE', 3, 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/tgbNymZ7vqY', b'0', b'0', b'1', 4.4, 993, NOW(), NOW()),
(8, 'Dragon Gate Legacy', 'Dragon Gate Legacy', 'dragon-gate-legacy-8', 'Sử thi võ hiệp giả tưởng với trận chiến định mệnh.', 'Ba gia tộc mang trong mình lời nguyền cổ xưa phải liên minh để bảo vệ Long Môn khỏi thế lực muốn hồi sinh bóng tối.', 2025, 52, 'SERIES', 'PREMIUM', 3, 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/ScMzIvxBSi4', b'1', b'1', b'1', 4.7, 3555, NOW(), NOW()),
(9, 'Saigon Heatwave', 'Saigon Heatwave', 'saigon-heatwave-9', 'Một mùa hè Sài Gòn đầy nhiệt và những ngã rẽ.', 'Một đầu bếp trẻ, một nhà đầu tư và một cô phóng viên va chạm giữa cuộc đua startup ẩm thực ở Sài Gòn, tạo nên hành trình vừa kịch tính vừa lãng mạn.', 2026, 49, 'SERIES', 'FREE', 5, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/aqz-KE-bpKQ', b'1', b'1', b'1', 4.2, 1895, NOW(), NOW()),
(10, 'Glass Kingdom', 'Glass Kingdom', 'glass-kingdom-10', 'Đế chế truyền thông và những bí mật sau tấm kính.', 'Sau cái chết bí ẩn của người sáng lập, cả tập đoàn truyền thông lớn nhất châu Âu bước vào cuộc chiến quyền lực khốc liệt.', 2024, 57, 'SERIES', 'STANDARD', 6, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/5PSNL1qE6VY', b'0', b'1', b'1', 4.0, 1620, NOW(), NOW()),
(11, 'Blue Horizon', 'Blue Horizon', 'blue-horizon-11', 'Phiêu lưu chinh phục đại dương sâu thẳm.', 'Một đội thám hiểm biển sâu phát hiện dấu vết của nền văn minh thất lạc cùng một mối đe dọa thức tỉnh từ đáy đại dương.', 2023, 114, 'MOVIE', 'FREE', 1, 'https://images.unsplash.com/photo-1500375592092-40eb2168fd21?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1500375592092-40eb2168fd21?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/tgbNymZ7vqY', b'0', b'0', b'1', 3.9, 840, NOW(), NOW()),
(12, 'Velvet Case', 'Velvet Case', 'velvet-case-12', 'Bản giao hưởng phá án tại London mưa đêm.', 'Một nữ luật sư và một điều tra viên bất đắc dĩ bước vào hồ sơ mất tích liên quan tới giới tinh hoa London.', 2025, 111, 'MOVIE', 'STANDARD', 4, 'https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/ScMzIvxBSi4', b'0', b'0', b'1', 4.2, 1130, NOW(), NOW()),
(13, 'Aurora Station', 'Aurora Station', 'aurora-station-13', 'Trạm không gian cuối cùng và bí mật bị chôn giấu.', 'Khi hệ thống sự sống trên Aurora Station gặp trục trặc, một kỹ sư trẻ phát hiện sự thật về nhiệm vụ mà cả phi hành đoàn bị buộc phải giữ im lặng.', 2026, 120, 'MOVIE', 'PREMIUM', 1, 'https://images.unsplash.com/photo-1446776877081-d282a0f896e2?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/aqz-KE-bpKQ', b'1', b'1', b'1', 4.7, 2899, NOW(), NOW()),
(14, 'Paper Hearts', 'Paper Hearts', 'paper-hearts-14', 'Lãng mạn nhẹ nhàng giữa hai con người cô đơn.', 'Một biên tập viên sách và một họa sĩ minh họa gặp nhau trong dự án cuối cùng trước khi nhà xuất bản đóng cửa, mở ra câu chuyện tình ấm áp.', 2024, 101, 'MOVIE', 'FREE', 6, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/tgbNymZ7vqY', b'0', b'0', b'1', 4.1, 733, NOW(), NOW()),
(15, 'Code Zero', 'Code Zero', 'code-zero-15', 'Hacker trẻ đối đầu mạng lưới tống tiền toàn cầu.', 'Một thiên tài bảo mật ẩn danh phải hợp tác với cơ quan điều tra để chặn cuộc tấn công ransomware quy mô lớn vào bệnh viện quốc tế.', 2026, 50, 'SERIES', 'PREMIUM', 2, 'https://images.unsplash.com/photo-1510511459019-5dda7724fd87?w=800&auto=format&fit=crop&q=60', 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=1600&auto=format&fit=crop&q=60', 'https://www.youtube.com/embed/5PSNL1qE6VY', b'1', b'1', b'1', 4.8, 3875, NOW(), NOW());

INSERT INTO movie_genres (movie_id, genre_id) VALUES
(1,1),(1,3),(1,4),
(2,4),(2,7),(2,2),
(3,2),(3,5),
(4,1),(4,3),(4,8),
(5,2),(5,4),(5,7),
(6,1),(6,3),(6,4),
(7,2),(7,5),
(8,1),(8,6),(8,8),
(9,2),(9,5),(9,10),
(10,2),(10,7),(10,4),
(11,8),(11,3),
(12,7),(12,2),(12,4),
(13,3),(13,4),(13,8),
(14,5),(14,2),
(15,3),(15,4),(15,7);

INSERT INTO movie_actors (movie_id, actor_id) VALUES
(1,1),(1,2),
(2,4),(2,7),
(3,6),(3,10),
(4,1),(4,3),
(5,2),(5,4),
(6,3),(6,10),
(7,5),(7,8),
(8,5),(8,2),
(9,9),(9,6),
(10,7),(10,6),
(11,1),(11,10),
(12,6),(12,7),
(13,3),(13,2),
(14,6),(14,10),
(15,4),(15,3);

INSERT INTO movie_directors (movie_id, director_id) VALUES
(1,1),(2,2),(3,4),(4,1),(5,2),(6,4),(7,3),(8,3),(9,5),(10,6),(11,1),(12,4),(13,1),(14,6),(15,2);

INSERT INTO episodes (id, movie_id, episode_number, title, video_url, duration_minutes, free_preview, active, created_at, updated_at) VALUES
(1,1,1,'Neon Skyline - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',118,b'0',b'1',NOW(),NOW()),
(2,2,1,'Shadow District - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',110,b'0',b'1',NOW(),NOW()),
(3,3,1,'Whispering Tides - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',102,b'1',b'1',NOW(),NOW()),
(4,4,1,'Quantum Heist - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',124,b'0',b'1',NOW(),NOW()),
(5,5,1,'Midnight Seoul - Episode 1','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',54,b'1',b'1',NOW(),NOW()),
(6,5,2,'Midnight Seoul - Episode 2','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscape.mp4',55,b'0',b'1',NOW(),NOW()),
(7,5,3,'Midnight Seoul - Episode 3','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4',52,b'0',b'1',NOW(),NOW()),
(8,6,1,'Eclipse Protocol - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4',116,b'0',b'1',NOW(),NOW()),
(9,7,1,'Sakura Memory - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4',108,b'1',b'1',NOW(),NOW()),
(10,8,1,'Dragon Gate Legacy - Episode 1','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4',52,b'0',b'1',NOW(),NOW()),
(11,8,2,'Dragon Gate Legacy - Episode 2','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',51,b'0',b'1',NOW(),NOW()),
(12,8,3,'Dragon Gate Legacy - Episode 3','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',53,b'0',b'1',NOW(),NOW()),
(13,9,1,'Saigon Heatwave - Episode 1','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',49,b'1',b'1',NOW(),NOW()),
(14,9,2,'Saigon Heatwave - Episode 2','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',48,b'1',b'1',NOW(),NOW()),
(15,9,3,'Saigon Heatwave - Episode 3','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscape.mp4',50,b'1',b'1',NOW(),NOW()),
(16,10,1,'Glass Kingdom - Episode 1','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',57,b'0',b'1',NOW(),NOW()),
(17,10,2,'Glass Kingdom - Episode 2','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4',58,b'0',b'1',NOW(),NOW()),
(18,10,3,'Glass Kingdom - Episode 3','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4',57,b'0',b'1',NOW(),NOW()),
(19,11,1,'Blue Horizon - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',114,b'1',b'1',NOW(),NOW()),
(20,12,1,'Velvet Case - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',111,b'0',b'1',NOW(),NOW()),
(21,13,1,'Aurora Station - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscape.mp4',120,b'0',b'1',NOW(),NOW()),
(22,14,1,'Paper Hearts - Full Movie','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',101,b'1',b'1',NOW(),NOW()),
(23,15,1,'Code Zero - Episode 1','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',50,b'0',b'1',NOW(),NOW()),
(24,15,2,'Code Zero - Episode 2','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',49,b'0',b'1',NOW(),NOW()),
(25,15,3,'Code Zero - Episode 3','https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4',51,b'0',b'1',NOW(),NOW());

INSERT INTO banners (id, title, subtitle, image_url, cta_text, cta_link, display_order, active, movie_id, created_at, updated_at) VALUES
(1, 'Quantum Heist is now streaming', 'Mở khóa phi vụ sci-fi premium với nhịp phim bùng nổ và giao diện player cinematic.', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=1600&auto=format&fit=crop&q=60', 'Xem ngay', '/movies/4', 1, b'1', 4, NOW(), NOW()),
(2, 'Saigon Heatwave - series Việt mới', 'Phim bộ Free dễ test luồng guest, lịch sử xem và continue watching.', 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=1600&auto=format&fit=crop&q=60', 'Khám phá', '/movies/9', 2, b'1', 9, NOW(), NOW()),
(3, 'Nâng cấp Premium để mở khóa toàn bộ', 'Aurora Station, Dragon Gate Legacy và Code Zero đang chờ bạn.', 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=1600&auto=format&fit=crop&q=60', 'Xem bảng giá', '/subscription/plans', 3, b'1', 13, NOW(), NOW());


-- Subscription history
INSERT INTO user_subscriptions (id, user_id, plan_id, start_date, end_date, status, paid_amount, created_at, updated_at) VALUES
(1, 2, 2, DATE_SUB(CURDATE(), INTERVAL 38 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY), 'EXPIRED', 99000.00, DATE_SUB(NOW(), INTERVAL 38 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 2, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 28 DAY), 'ACTIVE', 149000.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, 2, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'EXPIRED', 99000.00, DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 4, 3, DATE_SUB(CURDATE(), INTERVAL 155 DAY), DATE_SUB(CURDATE(), INTERVAL 125 DAY), 'EXPIRED', 150000.00, DATE_SUB(NOW(), INTERVAL 155 DAY), DATE_SUB(NOW(), INTERVAL 125 DAY)),
(5, 4, 2, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_ADD(CURDATE(), INTERVAL 26 DAY), 'ACTIVE', 49000.00, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(6, 5, 2, DATE_SUB(CURDATE(), INTERVAL 90 DAY), DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'EXPIRED', 99000.00, DATE_SUB(NOW(), INTERVAL 90 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY));

-- Favorites
INSERT INTO favorites (id, user_id, movie_id, created_at, updated_at) VALUES
(1, 2, 3, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
(2, 2, 9, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 3, 7, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
(4, 4, 13, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 5, 11, DATE_SUB(NOW(), INTERVAL 70 DAY), DATE_SUB(NOW(), INTERVAL 70 DAY));

-- Comments
INSERT INTO comments (id, movie_id, user_id, content, hidden, created_at, updated_at) VALUES
(1, 3, 2, 'Whispering Tides có hình ảnh đẹp, nhịp phim nhẹ nhàng và dễ xem.', b'0', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
(2, 9, 2, 'Phim Việt xem khá cuốn, phần màu sắc và âm nhạc ổn.', b'0', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 7, 3, 'Sakura Memory chữa lành đúng nghĩa, rất hợp để xem cuối tuần.', b'0', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(4, 13, 4, 'Aurora Station có bối cảnh không gian tốt, đáng tiền gói Premium.', b'0', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 11, 5, 'Blue Horizon phần đầu khá ổn nhưng nhịp phim hơi chậm.', b'1', DATE_SUB(NOW(), INTERVAL 68 DAY), DATE_SUB(NOW(), INTERVAL 68 DAY));

-- Ratings
INSERT INTO ratings (id, movie_id, user_id, stars, created_at, updated_at) VALUES
(1, 3, 2, 5, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
(2, 9, 2, 4, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 7, 3, 5, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(4, 13, 4, 5, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 11, 5, 3, DATE_SUB(NOW(), INTERVAL 68 DAY), DATE_SUB(NOW(), INTERVAL 68 DAY)),
(6, 5, 3, 4, DATE_SUB(NOW(), INTERVAL 33 DAY), DATE_SUB(NOW(), INTERVAL 33 DAY));

-- Watch history
INSERT INTO watch_history (id, user_id, movie_id, episode_id, last_position_seconds, completed, last_watched_at, created_at, updated_at) VALUES
(1, 2, 3, 3, 420, b'0', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 2, 9, 14, 1180, b'0', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(3, 3, 7, 9, 6480, b'1', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 4, 13, 21, 5400, b'0', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 5, 11, 19, 2100, b'0', DATE_SUB(NOW(), INTERVAL 67 DAY), DATE_SUB(NOW(), INTERVAL 67 DAY), DATE_SUB(NOW(), INTERVAL 67 DAY));

UPDATE users
SET password = '$2y$12$095i2bnI81UJRFSGp78tEOvJu7v1N2Fvwex8EUn8OFqfMU5jt1q6C'
WHERE email = 'admin@example.com';

UPDATE users
SET password = '$2y$12$zjvcwSy.3GkUzGcNezbWLetSOMgmbCuE2ZAs4EJSzNe0sO3aaOo5.'
WHERE email IN ('user@example.com', 'ngoc@example.com', 'minh@example.com', 'disabled@example.com');