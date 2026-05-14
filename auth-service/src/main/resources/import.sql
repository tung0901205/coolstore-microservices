-- ============================================================
-- AUTH SERVICE - import.sql (ĐÃ SỬA LỖI 6)
-- Dữ liệu mẫu cho auth-service
--
-- Lưu ý: Với mode drop-and-create, Hibernate sẽ tự tạo bảng
-- và sequence app_user_seq trước khi chạy file này.
-- Mật khẩu: Admin@123 (BCrypt hash)
-- ============================================================

-- Xóa dữ liệu cũ để tránh conflict khi restart (drop-and-create đã xóa bảng rồi)
-- DELETE FROM app_user; -- không cần vì drop-and-create đã xóa

-- Tài khoản Admin
INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (1,
        'admin',
        'admin@coolstore.vn',
        'Quan Tri Vien',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6FrFDdOJ5e',
        'ADMIN',
        true,
        NOW());

-- Tài khoản User thường
INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (2,
        'nguyenvana',
        'nguyenvana@gmail.com',
        'Nguyen Van A',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6FrFDdOJ5e',
        'USER',
        true,
        NOW());

-- Cập nhật sequence để ID tiếp theo bắt đầu từ 3
-- (tránh conflict nếu sequence bắt đầu từ 1)
SELECT setval('app_user_seq', 3, false);