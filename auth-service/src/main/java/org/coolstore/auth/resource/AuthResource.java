package org.coolstore.auth.resource;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.coolstore.auth.entity.AppUser;
import org.coolstore.auth.model.AuthDTOs.*;
import org.coolstore.auth.service.AuthService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API cho xác thực và quản lý người dùng.
 *
 * Endpoints công khai:
 *   POST /api/auth/dang-ky    - Tạo tài khoản mới
 *   POST /api/auth/dang-nhap  - Đăng nhập, nhận JWT
 *
 * Endpoints cần xác thực:
 *   GET  /api/auth/toi        - Xem thông tin cá nhân
 *
 * Endpoints chỉ Admin:
 *   GET  /api/auth/nguoi-dung         - Danh sách tất cả user
 *   PUT  /api/auth/nguoi-dung/{id}/trang-thai - Bật/tắt tài khoản
 *   PUT  /api/auth/nguoi-dung/{id}/quyen      - Đổi quyền user
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Xác Thực", description = "API đăng ký, đăng nhập và quản lý tài khoản")
public class AuthResource {

    @Inject AuthService authService;
    @Inject JsonWebToken jwt; // Token của user hiện tại (null nếu chưa đăng nhập)

    // ── Đăng ký ─────────────────────────────────────────────────

    @POST
    @Path("/dang-ky")
    @Operation(summary = "Đăng ký tài khoản mới")
    public Response dangKy(@Valid DangKyRequest request) {
        ThongTinNguoiDung user = authService.dangKy(request);
        return Response.status(Response.Status.CREATED)
                .entity(KetQua.ok("Đăng ký thành công! Chào mừng " + user.fullName(), user))
                .build();
    }

    // ── Đăng nhập ───────────────────────────────────────────────

    @POST
    @Path("/dang-nhap")
    @Operation(summary = "Đăng nhập và nhận JWT token")
    public Response dangNhap(@Valid DangNhapRequest request) {
        DangNhapResponse response = authService.dangNhap(request);
        return Response.ok(KetQua.ok("Đăng nhập thành công!", response)).build();
    }

    // ── Thông tin cá nhân ────────────────────────────────────────

    @GET
    @Path("/toi")
    @Authenticated
    @Operation(summary = "Xem thông tin tài khoản hiện tại")
    public Response layThongTinToi() {
        Long userId = Long.parseLong(jwt.getSubject());
        ThongTinNguoiDung user = authService.layNguoiDungTheoId(userId);
        return Response.ok(KetQua.ok("Thành công", user)).build();
    }

    // ── Admin: Quản lý người dùng ────────────────────────────────

    @GET
    @Path("/nguoi-dung")
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Lấy danh sách tất cả người dùng")
    public Response layTatCaNguoiDung() {
        return Response.ok(KetQua.ok("Thành công", authService.layTatCaNguoiDung())).build();
    }

    @GET
    @Path("/nguoi-dung/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Lấy thông tin người dùng theo ID")
    public Response layNguoiDungTheoId(@PathParam("id") Long id) {
        ThongTinNguoiDung user = authService.layNguoiDungTheoId(id);
        return Response.ok(KetQua.ok("Thành công", user)).build();
    }

    @PUT
    @Path("/nguoi-dung/{id}/trang-thai")
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Kích hoạt hoặc vô hiệu hóa tài khoản")
    public Response capNhatTrangThai(@PathParam("id") Long id,
                                     @QueryParam("active") boolean active) {
        ThongTinNguoiDung user = authService.capNhatTrangThai(id, active);
        String msg = active ? "Tài khoản đã được kích hoạt." : "Tài khoản đã bị vô hiệu hóa.";
        return Response.ok(KetQua.ok(msg, user)).build();
    }

    @PUT
    @Path("/nguoi-dung/{id}/quyen")
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Thay đổi quyền người dùng")
    public Response capNhatQuyen(@PathParam("id") Long id,
                                 @QueryParam("role") String role) {
        AppUser.Role newRole;
        try {
            newRole = AppUser.Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            return Response.status(400)
                    .entity(KetQua.loi("Quyền không hợp lệ. Chọn: USER hoặc ADMIN"))
                    .build();
        }
        ThongTinNguoiDung user = authService.capNhatQuyen(id, newRole);
        return Response.ok(KetQua.ok("Cập nhật quyền thành công.", user)).build();
    }
}