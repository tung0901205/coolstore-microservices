-- Seed accounts for auth-service.
-- Password for both accounts: Admin@123

INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (1,
        'admin',
        'admin@coolstore.vn',
        'Quan Tri Vien',
        '$2a$12$zaztO6ACquKLwTGZJExTQOZ5jK86f0nK/S1m/wDXhpkW2IV9/LOX2',
        'ADMIN',
        true,
        NOW())
ON CONFLICT (username) DO UPDATE SET
        email = EXCLUDED.email,
        full_name = EXCLUDED.full_name,
        password_hash = EXCLUDED.password_hash,
        role = EXCLUDED.role,
        active = EXCLUDED.active;

INSERT INTO app_user (id, username, email, full_name, password_hash, role, active, created_at)
VALUES (2,
        'nguyenvana',
        'nguyenvana@gmail.com',
        'Nguyen Van A',
        '$2a$12$zaztO6ACquKLwTGZJExTQOZ5jK86f0nK/S1m/wDXhpkW2IV9/LOX2',
        'USER',
        true,
        NOW())
ON CONFLICT (username) DO UPDATE SET
        email = EXCLUDED.email,
        full_name = EXCLUDED.full_name,
        password_hash = EXCLUDED.password_hash,
        role = EXCLUDED.role,
        active = EXCLUDED.active;

SELECT setval('app_user_seq', 100, false);