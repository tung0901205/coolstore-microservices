package org.coolstore.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

/** Mặt hàng trong một đơn hàng */
@Entity
@Table(name = "mat_hang_don_hang")
public class OrderItem extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", allocationSize = 1)
    public Long id;

    /** Quan hệ Many-to-One với Order */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    /** itemId từ catalog-service */
    @Column(name = "item_id", nullable = false)
    public String itemId;

    /** Tên sản phẩm - lưu snapshot tại thời điểm đặt hàng */
    @Column(name = "ten_san_pham", nullable = false)
    public String tenSanPham;

    /** Hình ảnh sản phẩm */
    @Column(name = "hinh_anh")
    public String hinhAnh;

    /** Đơn giá tại thời điểm đặt hàng */
    @Column(name = "don_gia", nullable = false, precision = 12, scale = 2)
    public BigDecimal donGia;

    /** Số lượng */
    @Column(name = "so_luong", nullable = false)
    public int soLuong;

    /** Thành tiền = donGia * soLuong */
    public BigDecimal thanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
}