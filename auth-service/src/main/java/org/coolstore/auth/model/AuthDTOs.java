package org.coolstore.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.coolstore.auth.entity.AppUser;
import java.time.LocalDateTime;

/** Tập hợp các DTO (Data Transfer Object) cho Auth Service */
public class AuthDTOs {

    // ── Request: Đăng ký ────────────────────────────────────────
    public record DangKyRequest(
            @NotBlank(message = "Tên đăng nhập không được để trống")
            @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
            String username,

            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không hợp lệ")
            String email,

            @NotBlank(message = "Họ tên không được để trống")
            String fullName,

            @NotBlank(message = "Mật khẩu không được để trống")
            @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
            String password
    ) {}

    // ── Request: Đăng nhập ──────────────────────────────────────
    public record DangNhapRequest(
            @NotBlank(message = "Tên đăng nhập không được để trống")
            String username,

            @NotBlank(message = "Mật khẩu không được để trống")
            String password
    ) {}

    // ── Response: Kết quả đăng nhập ─────────────────────────────
    public record DangNhapResponse(
            String token,
            String loaiToken,       // "Bearer"
            long thoiHanGiay,       // Token hết hạn sau bao nhiêu giây
            ThongTinNguoiDung nguoiDung
    ) {}

    // ── DTO: Thông tin người dùng (không có password) ────────────
    public record ThongTinNguoiDung(
            Long id,
            String username,
            String email,
            String fullName,
            String role,
            boolean active,
            LocalDateTime createdAt
    ) {
        /** Chuyển đổi từ Entity sang DTO */
        public static ThongTinNguoiDung from(AppUser user) {
            return new ThongTinNguoiDung(
                    user.id, user.username, user.email,
                    user.fullName, user.role.name(),
                    user.active, user.createdAt
            );
        }
    }

    // ── Response: Kết quả chung ──────────────────────────────────
    public record KetQua<T>(
            boolean thanhCong,
            String thongBao,
            T duLieu
    ) {
        public static <T> KetQua<T> ok(String msg, T data) {
            return new KetQua<>(true, msg, data);
        }
        public static <T> KetQua<T> loi(String msg) {
            return new KetQua<>(false, msg, null);
        }
    }
}