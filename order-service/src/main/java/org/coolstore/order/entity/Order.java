package org.coolstore.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity đơn hàng.
 * Một đơn hàng thuộc về một user và chứa nhiều mặt hàng (OrderItem).
 */
@Entity
@Table(name = "don_hang")
public class Order extends PanacheEntityBase {

    @Id
    public String id; // UUID dạng string

    /** ID người dùng đặt hàng (từ JWT claim) */
    @Column(name = "user_id", nullable = false)
    public Long userId;

    /** Tên người dùng để hiển thị nhanh */
    @Column(name = "ten_nguoi_dat", nullable = false)
    public String tenNguoiDat;

    /** Email để gửi xác nhận */
    @Column(name = "email", nullable = false)
    public String email;

    /** Địa chỉ giao hàng */
    @Column(name = "dia_chi_giao_hang")
    public String diaChiGiaoHang;

    /** Số điện thoại nhận hàng */
    @Column(name = "so_dien_thoai", length = 15)
    public String soDienThoai;

    /** Tổng tiền = sum(quantity * price) + phí ship */
    @Column(name = "tong_tien", nullable = false, precision = 15, scale = 2)
    public BigDecimal tongTien;

    /** Trạng thái thanh toán */
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_thanh_toan", nullable = false)
    public TrangThaiThanhToan trangThaiThanhToan = TrangThaiThanhToan.CHUA_THANH_TOAN;

    /** Trạng thái đơn hàng */
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_don_hang", nullable = false)
    public TrangThaiDonHang trangThaiDonHang = TrangThaiDonHang.CHO_XAC_NHAN;

    /** Phương thức thanh toán */
    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc_thanh_toan")
    public PhuongThucThanhToan phuongThucThanhToan;

    /** Mã giao dịch VNPay (vnp_TxnRef) */
    @Column(name = "ma_giao_dich_vnpay", unique = true)
    public String maGiaoDichVnpay;

    /** Thời điểm tạo đơn */
    @Column(name = "ngay_tao", nullable = false)
    public LocalDateTime ngayTao;

    /** Thời điểm thanh toán thành công */
    @Column(name = "ngay_thanh_toan")
    public LocalDateTime ngayThanhToan;

    /** Ghi chú của khách hàng */
    @Column(name = "ghi_chu")
    public String ghiChu;

    /** Các mặt hàng trong đơn - cascade để tự lưu cùng đơn hàng */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderItem> cacMatHang = new ArrayList<>();

    // ── Enums ──────────────────────────────────────────────────

    public enum TrangThaiThanhToan {
        CHUA_THANH_TOAN("Chưa thanh toán"),
        DA_THANH_TOAN("Đã thanh toán"),
        HOAN_TIEN("Hoàn tiền");

        public final String tenHienThi;
        TrangThaiThanhToan(String ten) { this.tenHienThi = ten; }
    }

    public enum TrangThaiDonHang {
        CHO_XAC_NHAN("Chờ xác nhận"),
        DA_XAC_NHAN("Đã xác nhận"),
        DANG_GIAO("Đang giao"),
        DA_GIAO("Đã giao"),
        DA_HUY("Đã hủy");

        public final String tenHienThi;
        TrangThaiDonHang(String ten) { this.tenHienThi = ten; }
    }

    public enum PhuongThucThanhToan {
        VNPAY, TIEN_MAT, CHUYEN_KHOAN
    }

    // ── Lifecycle ──────────────────────────────────────────────

    @PrePersist
    public void truocKhiLuu() {
        if (this.id == null) {
            this.id = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        this.ngayTao = LocalDateTime.now();
    }

    // ── Static finders ─────────────────────────────────────────

    public static List<Order> findByUserId(Long userId) {
        return list("userId = ?1 ORDER BY ngayTao DESC", userId);
    }

    public static Order findByMaGiaoDich(String maGiaoDich) {
        return find("maGiaoDichVnpay", maGiaoDich).firstResult();
    }
}