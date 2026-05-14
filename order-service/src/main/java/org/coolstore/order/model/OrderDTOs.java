package org.coolstore.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.coolstore.order.entity.Order;
import org.coolstore.order.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Tất cả DTO của Order Service */
public class OrderDTOs {

    // ── Request: Tạo đơn hàng mới ────────────────────────────────
    public record TaoDonHangRequest(
            @NotBlank(message = "Địa chỉ giao hàng không được để trống")
            String diaChiGiaoHang,

            @NotBlank(message = "Số điện thoại không được để trống")
            String soDienThoai,

            @NotEmpty(message = "Giỏ hàng không được rỗng")
            List<MatHangRequest> cacMatHang,

            @NotBlank(message = "Phương thức thanh toán không được để trống")
            String phuongThucThanhToan,

            String ghiChu
    ) {}

    public record MatHangRequest(
            @NotBlank String itemId,
            @NotBlank String tenSanPham,
            String hinhAnh,
            @NotNull @Positive BigDecimal donGia,
            @Positive int soLuong
    ) {}

    // ── Response: Thông tin đơn hàng ─────────────────────────────
    public record DonHangResponse(
            String id,
            Long userId,
            String tenNguoiDat,
            String email,
            String diaChiGiaoHang,
            String soDienThoai,
            BigDecimal tongTien,
            String trangThaiThanhToan,
            String trangThaiThanhToanText,
            String trangThaiDonHang,
            String trangThaiDonHangText,
            String phuongThucThanhToan,
            String maGiaoDichVnpay,
            LocalDateTime ngayTao,
            LocalDateTime ngayThanhToan,
            String ghiChu,
            List<MatHangResponse> cacMatHang
    ) {
        /** Chuyển đổi từ Entity sang Response DTO */
        public static DonHangResponse from(Order order) {
            List<MatHangResponse> items = order.cacMatHang.stream()
                    .map(MatHangResponse::from)
                    .toList();
            return new DonHangResponse(
                    order.id, order.userId, order.tenNguoiDat, order.email,
                    order.diaChiGiaoHang, order.soDienThoai, order.tongTien,
                    order.trangThaiThanhToan.name(),
                    order.trangThaiThanhToan.tenHienThi,
                    order.trangThaiDonHang.name(),
                    order.trangThaiDonHang.tenHienThi,
                    order.phuongThucThanhToan != null ? order.phuongThucThanhToan.name() : null,
                    order.maGiaoDichVnpay, order.ngayTao, order.ngayThanhToan,
                    order.ghiChu, items
            );
        }
    }

    public record MatHangResponse(
            Long id, String itemId, String tenSanPham,
            String hinhAnh, BigDecimal donGia, int soLuong, BigDecimal thanhTien
    ) {
        public static MatHangResponse from(OrderItem item) {
            return new MatHangResponse(
                    item.id, item.itemId, item.tenSanPham,
                    item.hinhAnh, item.donGia, item.soLuong, item.thanhTien()
            );
        }
    }

    // ── Response chung ───────────────────────────────────────────
    public record KetQua<T>(boolean thanhCong, String thongBao, T duLieu) {
        public static <T> KetQua<T> ok(String msg, T d) { return new KetQua<>(true, msg, d); }
        public static <T> KetQua<T> loi(String msg) { return new KetQua<>(false, msg, null); }
    }
}