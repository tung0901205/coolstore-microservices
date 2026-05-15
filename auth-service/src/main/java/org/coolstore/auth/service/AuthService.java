package org.coolstore.auth.service;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.coolstore.auth.entity.AppUser;
import org.coolstore.auth.model.AuthDTOs.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@ApplicationScoped
public class AuthService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@coolstore.vn";
    private static final String ADMIN_PASSWORD = "Admin@123";

    @Inject
    JwtService jwtService;

    @Transactional
    public ThongTinNguoiDung dangKy(DangKyRequest request) {
        String username = request.username().trim().toLowerCase();
        String email = request.email().trim().toLowerCase();

        if (AppUser.existsByUsername(username)) {
            throw new BadRequestException("Ten dang nhap '" + username + "' da duoc su dung.");
        }

        if (AppUser.existsByEmail(email)) {
            throw new BadRequestException("Email '" + email + "' da duoc dang ky.");
        }

        AppUser user = new AppUser();
        user.username = username;
        user.email = email;
        user.fullName = request.fullName().trim();
        user.passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt(12));
        user.role = AppUser.Role.USER;
        user.active = true;

        user.persist();
        return ThongTinNguoiDung.from(user);
    }

    public DangNhapResponse dangNhap(DangNhapRequest request) {
        String usernameOrEmail = request.username().trim().toLowerCase();

        AppUser user = AppUser.findByUsername(usernameOrEmail);
        if (user == null) {
            user = AppUser.findByEmail(usernameOrEmail);
        }

        if (user == null || !BCrypt.checkpw(request.password(), user.passwordHash)) {
            throw new BadRequestException("Ten dang nhap hoac mat khau khong dung.");
        }

        if (!user.active) {
            throw new BadRequestException("Tai khoan cua ban da bi vo hieu hoa. Vui long lien he admin.");
        }

        String token = jwtService.taoToken(user);
        return new DangNhapResponse(token, "Bearer", 86400L, ThongTinNguoiDung.from(user));
    }

    public List<ThongTinNguoiDung> layTatCaNguoiDung() {
        return AppUser.<AppUser>listAll()
                .stream()
                .map(ThongTinNguoiDung::from)
                .toList();
    }

    public ThongTinNguoiDung layNguoiDungTheoId(Long id) {
        AppUser user = AppUser.findById(id);
        if (user == null) {
            throw new NotFoundException("Khong tim thay nguoi dung voi ID: " + id);
        }
        return ThongTinNguoiDung.from(user);
    }

    @Transactional
    public ThongTinNguoiDung capNhatTrangThai(Long id, boolean active) {
        AppUser user = AppUser.findById(id);
        if (user == null) {
            throw new NotFoundException("Khong tim thay nguoi dung voi ID: " + id);
        }
        user.active = active;
        return ThongTinNguoiDung.from(user);
    }

    @Transactional
    public ThongTinNguoiDung capNhatQuyen(Long id, AppUser.Role role) {
        AppUser user = AppUser.findById(id);
        if (user == null) {
            throw new NotFoundException("Khong tim thay nguoi dung voi ID: " + id);
        }
        user.role = role;
        return ThongTinNguoiDung.from(user);
    }

    @Transactional
    public void initAdmin(@Observes StartupEvent ev) {
        AppUser admin = AppUser.findByUsername(ADMIN_USERNAME);
        if (admin == null) {
            admin = new AppUser();
            admin.username = ADMIN_USERNAME;
        }

        admin.email = ADMIN_EMAIL;
        admin.fullName = "Quan Tri Vien";
        admin.passwordHash = BCrypt.hashpw(ADMIN_PASSWORD, BCrypt.gensalt(12));
        admin.role = AppUser.Role.ADMIN;
        admin.active = true;

        if (!admin.isPersistent()) {
            admin.persist();
        }

        System.out.println("--------------------------------------------------");
        System.out.println("[AUTH] Admin account ready: admin / Admin@123");
        System.out.println("--------------------------------------------------");
    }
}