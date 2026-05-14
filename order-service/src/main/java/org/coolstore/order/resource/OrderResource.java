package org.coolstore.order.resource;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.coolstore.order.model.OrderDTOs.*;
import org.coolstore.order.service.OrderService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API quản lý đơn hàng.
 *
 * Tất cả endpoint đều yêu cầu JWT (Bearer token từ auth-service).
 *
 * User thường: tạo đơn, xem đơn của mình
 * Admin: xem tất cả đơn, cập nhật trạng thái
 *
 * Endpoint đặc biệt cho payment-service (internal):
 *   PUT /api/orders/{id}/thanh-toan - Cập nhật sau VNPay callback
 */
@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@Tag(name = "Đơn Hàng", description = "Quản lý đơn hàng CoolStore")
public class OrderResource {

    @Inject OrderService orderService;
    @Inject JsonWebToken jwt;

    // ── Tạo đơn hàng ────────────────────────────────────────────

    @POST
    @Operation(summary = "Tạo đơn hàng mới từ giỏ hàng")
    public Response taoDonHang(@Valid TaoDonHangRequest request) {
        Long userId     = Long.parseLong(jwt.getSubject());
        String tenNguoi = (String) jwt.getClaim("fullName");
        String email    = (String) jwt.getClaim("email");

        DonHangResponse donHang = orderService.taoDonHang(userId, tenNguoi, email, request);
        return Response.status(Response.Status.CREATED)
                .entity(KetQua.ok("Đặt hàng thành công!", donHang))
                .build();
    }

    // ── Đơn hàng của tôi ────────────────────────────────────────

    @GET
    @Path("/cua-toi")
    @Operation(summary = "Lấy danh sách đơn hàng của tôi")
    public Response layDonHangCuaToi() {
        Long userId = Long.parseLong(jwt.getSubject());
        return Response.ok(KetQua.ok("Thành công", orderService.layDonHangCuaToi(userId))).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Xem chi tiết đơn hàng")
    public Response layChiTiet(@PathParam("id") String id) {
        Long userId   = Long.parseLong(jwt.getSubject());
        boolean admin = jwt.getGroups().contains("ADMIN");
        DonHangResponse dh = orderService.layChiTietDonHang(id, userId, admin);
        return Response.ok(KetQua.ok("Thành công", dh)).build();
    }

    // ── Admin ─────────────────────────────────────────────────

    @GET
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Lấy tất cả đơn hàng")
    public Response layTatCaDonHang() {
        return Response.ok(KetQua.ok("Thành công", orderService.layTatCaDonHang())).build();
    }

    @PUT
    @Path("/{id}/trang-thai")
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Cập nhật trạng thái đơn hàng")
    public Response capNhatTrangThai(@PathParam("id") String id,
                                     @QueryParam("trangThai") String trangThai) {
        DonHangResponse dh = orderService.capNhatTrangThai(id, trangThai);
        return Response.ok(KetQua.ok("Cập nhật thành công", dh)).build();
    }

    // ── Internal: Gọi từ payment-service sau VNPay callback ─────

    /**
     * Endpoint nội bộ - payment-service gọi để cập nhật trạng thái sau VNPay.
     * Trong production nên bảo vệ bằng network policy hoặc service mesh.
     */
    @PUT
    @Path("/{id}/xac-nhan-thanh-toan")
    @Operation(summary = "[Internal] Xác nhận thanh toán VNPay thành công")
    public Response xacNhanThanhToan(@PathParam("id") String orderId,
                                     @QueryParam("maGiaoDich") String maGiaoDich) {
        orderService.xacNhanThanhToanVnpay(maGiaoDich, orderId);
        return Response.ok(KetQua.ok("Đơn hàng đã được xác nhận thanh toán.", null)).build();
    }

    @PUT
    @Path("/{id}/huy-thanh-toan")
    @Operation(summary = "[Internal] Hủy đơn hàng khi thanh toán thất bại")
    public Response huyThanhToan(@PathParam("id") String orderId) {
        orderService.huyThanhToanVnpay(orderId);
        return Response.ok(KetQua.ok("Đơn hàng đã bị hủy.", null)).build();
    }
}