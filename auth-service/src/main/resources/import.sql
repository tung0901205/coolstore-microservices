-- Mật khẩu: Admin@123
INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (1,'admin','admin@coolstore.vn','Quản Trị Viên',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6FrFDdOJ5e','ADMIN',true,NOW());

INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (2,'nguyenvana','nguyenvana@gmail.com','Nguyễn Văn A',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6FrFDdOJ5e','USER',true,NOW());