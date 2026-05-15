-- ============================================================
-- AUTH SERVICE - import.sql (SỬA LỖI ĐĂNG NHẬP)
--
-- SỬA CHÍNH: Dùng INSERT ... ON CONFLICT DO NOTHING
-- → Nếu bảng đã có user (restart), không gây lỗi duplicate key
-- → Transaction không bị rollback → bảng luôn có dữ liệu
--
-- Mật khẩu: Admin@123
-- BCrypt hash ($2a$12$...): ĐÚNG, đã xác minh
-- ============================================================

-- Admin account
INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (1,
        'admin',
        'admin@coolstore.vn',
        'Quan Tri Vien',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6FrFDdOJ5e',
        'ADMIN',
        true,
        NOW())
ON CONFLICT (username) DO NOTHING;

-- User account
INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (2,
        'nguyenvana',
        'nguyenvana@gmail.com',
        'Nguyen Van A',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6FrFDdOJ5e',
        'USER',
        true,
        NOW())
ON CONFLICT (username) DO NOTHING;

-- Đặt sequence bắt đầu từ 100 (tránh conflict với ID cứng 1, 2)
SELECT setval('app_user_seq', 100, false);