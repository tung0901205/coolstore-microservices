package org.coolstore.auth.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity người dùng hệ thống CoolStore.
 * Dùng PanacheEntityBase thay vì PanacheEntity vì id là Long tùy chỉnh.
 */
@Entity
@Table(name = "app_user")
public class AppUser extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "app_user_seq", allocationSize = 1)
    public Long id;

    @Column(nullable = false, unique = true, length = 50)
    public String username;

    @Column(nullable = false, unique = true, length = 100)
    public String email;

    @Column(name = "full_name", nullable = false, length = 100)
    public String fullName;

    /** Mật khẩu đã được hash bằng BCrypt - KHÔNG lưu plain text */
    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Role role = Role.USER;

    @Column(nullable = false)
    public boolean active = true;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    // ── Phân quyền ──────────────────────────────────────────────
    public enum Role {
        /** Người mua hàng bình thường */
        USER,
        /** Quản trị viên - có thể CRUD sản phẩm, xem đơn hàng */
        ADMIN
    }

    // ── Static finders (Panache style) ──────────────────────────

    /** Tìm user theo username, không phân biệt hoa thường */
    public static AppUser findByUsername(String username) {
        return find("LOWER(username) = LOWER(?1)", username).firstResult();
    }

    /** Tìm user theo email */
    public static AppUser findByEmail(String email) {
        return find("LOWER(email) = LOWER(?1)", email).firstResult();
    }

    /** Kiểm tra username đã tồn tại chưa */
    public static boolean existsByUsername(String username) {
        return count("LOWER(username) = LOWER(?1)", username) > 0;
    }

    /** Kiểm tra email đã tồn tại chưa */
    public static boolean existsByEmail(String email) {
        return count("LOWER(email) = LOWER(?1)", email) > 0;
    }
}