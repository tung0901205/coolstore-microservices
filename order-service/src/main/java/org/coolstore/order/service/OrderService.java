package org.coolstore.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.coolstore.order.entity.Order;
import org.coolstore.order.entity.OrderItem;
import org.coolstore.order.model.OrderDTOs.*;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Xử lý nghiệp vụ đơn hàng */
@ApplicationScoped
public class OrderService {

    private static final Logger log = Logger.getLogger(OrderService.class);

    // ── Tạo đơn hàng ────────────────────────────────────────────

    /**
     * Tạo đơn hàng mới từ giỏ hàng của user.
     * Trả về order với trạng thái CHUA_THANH_TOAN để chờ thanh toán.
     */
    @Transactional
    public DonHangResponse taoDonHang(Long userId, String tenNguoiDat,
                                      String email, TaoDonHangRequest req) {
        // Tạo đơn hàng
        Order order = new Order();
        order.userId            = userId;
        order.tenNguoiDat       = tenNguoiDat;
        order.email             = email;
        order.diaChiGiaoHang    = req.diaChiGiaoHang();
        order.soDienThoai       = req.soDienThoai();
        order.ghiChu            = req.ghiChu();
        order.phuongThucThanhToan = parsePhuongThuc(req.phuongThucThanhToan());

        // Tạo các mặt hàng và tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (MatHangRequest mhReq : req.cacMatHang()) {
            OrderItem item = new OrderItem();
            item.order       = order;
            item.itemId      = mhReq.itemId();
            item.tenSanPham  = mhReq.tenSanPham();
            item.hinhAnh     = mhReq.hinhAnh();
            item.donGia      = mhReq.donGia();
            item.soLuong     = mhReq.soLuong();
            order.cacMatHang.add(item);
            tongTien = tongTien.add(item.thanhTien());
        }
        order.tongTien = tongTien;

        order.persist();
        log.infof("Đơn hàng mới: %s - User: %d - Tổng: %.0f VNĐ",
                order.id, userId, tongTien);

        return DonHangResponse.from(order);
    }

    // ── Lấy danh sách đơn hàng ──────────────────────────────────

    /** Lấy đơn hàng của user hiện tại */
    public List<DonHangResponse> layDonHangCuaToi(Long userId) {
        return Order.findByUserId(userId)
                .stream()
                .map(DonHangResponse::from)
                .toList();
    }

    /** Lấy tất cả đơn hàng (Admin) */
    public List<DonHangResponse> layTatCaDonHang() {
        return Order.<Order>list("ORDER BY ngayTao DESC")
                .stream()
                .map(DonHangResponse::from)
                .toList();
    }

    /** Lấy chi tiết một đơn hàng */
    public DonHangResponse layChiTietDonHang(String orderId, Long userId, boolean laAdmin) {
        Order order = Order.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Không tìm thấy đơn hàng: " + orderId);
        }
        // User thường chỉ xem đơn của mình
        if (!laAdmin && !order.userId.equals(userId)) {
            throw new NotFoundException("Không tìm thấy đơn hàng: " + orderId);
        }
        return DonHangResponse.from(order);
    }

    // ── Cập nhật từ VNPay callback ──────────────────────────────

    /**
     * Gọi bởi payment-service khi VNPay callback thành công.
     * Cập nhật trạng thái đơn hàng sang ĐÃ THANH TOÁN.
     */
    @Transactional
    public void xacNhanThanhToanVnpay(String maGiaoDich, String txnRef) {
        Order order = Order.findById(txnRef);
        if (order == null) {
            order = Order.findByMaGiaoDich(maGiaoDich);
        }
        if (order == null) {
            log.warnf("Không tìm thấy đơn hàng để xác nhận: txnRef=%s", txnRef);
            return;
        }

        order.trangThaiThanhToan = Order.TrangThaiThanhToan.DA_THANH_TOAN;
        order.trangThaiDonHang   = Order.TrangThaiDonHang.DA_XAC_NHAN;
        order.maGiaoDichVnpay    = maGiaoDich;
        order.ngayThanhToan      = LocalDateTime.now();

        log.infof("Đơn hàng %s đã thanh toán thành công qua VNPay.", order.id);
    }

    /**
     * Gọi khi thanh toán VNPay thất bại.
     */
    @Transactional
    public void huyThanhToanVnpay(String txnRef) {
        Order order = Order.findById(txnRef);
        if (order != null) {
            order.trangThaiDonHang = Order.TrangThaiDonHang.DA_HUY;
            log.infof("Đơn hàng %s đã bị hủy do thanh toán thất bại.", order.id);
        }
    }

    /** Admin: Cập nhật trạng thái đơn hàng */
    @Transactional
    public DonHangResponse capNhatTrangThai(String orderId, String trangThai) {
        Order order = Order.findById(orderId);
        if (order == null) throw new NotFoundException("Không tìm thấy đơn hàng: " + orderId);

        try {
            order.trangThaiDonHang = Order.TrangThaiDonHang.valueOf(trangThai);
        } catch (Exception e) {
            throw new jakarta.ws.rs.BadRequestException("Trạng thái không hợp lệ: " + trangThai);
        }
        return DonHangResponse.from(order);
    }

    // ── Helper ──────────────────────────────────────────────────

    private Order.PhuongThucThanhToan parsePhuongThuc(String s) {
        try {
            return Order.PhuongThucThanhToan.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return Order.PhuongThucThanhToan.VNPAY;
        }
    }
}