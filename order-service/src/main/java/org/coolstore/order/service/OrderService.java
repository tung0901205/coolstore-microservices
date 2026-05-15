package org.coolstore.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.coolstore.order.client.InventoryServiceClient;
import org.coolstore.order.entity.Order;
import org.coolstore.order.entity.OrderItem;
import org.coolstore.order.model.OrderDTOs.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderService {

    private static final Logger log = Logger.getLogger(OrderService.class);

    @RestClient
    InventoryServiceClient inventoryClient;

    @Transactional
    public DonHangResponse taoDonHang(Long userId, String tenNguoiDat,
                                      String email, TaoDonHangRequest req) {
        Order order = new Order();
        order.userId = userId;
        order.tenNguoiDat = tenNguoiDat;
        order.email = email;
        order.diaChiGiaoHang = req.diaChiGiaoHang();
        order.soDienThoai = req.soDienThoai();
        order.ghiChu = req.ghiChu();
        order.phuongThucThanhToan = parsePhuongThuc(req.phuongThucThanhToan());

        BigDecimal tongTien = BigDecimal.ZERO;
        for (MatHangRequest mhReq : req.cacMatHang()) {
            if (mhReq.soLuong() <= 0) {
                throw new BadRequestException("So luong san pham phai lon hon 0.");
            }

            OrderItem item = new OrderItem();
            item.order = order;
            item.itemId = mhReq.itemId();
            item.tenSanPham = mhReq.tenSanPham();
            item.hinhAnh = mhReq.hinhAnh();
            item.donGia = mhReq.donGia();
            item.soLuong = mhReq.soLuong();
            order.cacMatHang.add(item);
            tongTien = tongTien.add(item.thanhTien());
        }
        order.tongTien = tongTien;

        kiemTraTonKho(order);
        order.persist();
        truTonKho(order);

        log.infof("Don hang moi: %s - User: %d - Tong: %.0f", order.id, userId, tongTien);
        return DonHangResponse.from(order);
    }

    public List<DonHangResponse> layDonHangCuaToi(Long userId) {
        return Order.findByUserId(userId)
                .stream()
                .map(DonHangResponse::from)
                .toList();
    }

    public List<DonHangResponse> layTatCaDonHang() {
        return Order.<Order>list("ORDER BY ngayTao DESC")
                .stream()
                .map(DonHangResponse::from)
                .toList();
    }

    public DonHangResponse layChiTietDonHang(String orderId, Long userId, boolean laAdmin) {
        Order order = Order.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Khong tim thay don hang: " + orderId);
        }
        if (!laAdmin && !order.userId.equals(userId)) {
            throw new NotFoundException("Khong tim thay don hang: " + orderId);
        }
        return DonHangResponse.from(order);
    }

    @Transactional
    public void xacNhanThanhToanVnpay(String maGiaoDich, String txnRef) {
        Order order = Order.findById(txnRef);
        if (order == null) {
            order = Order.findByMaGiaoDich(maGiaoDich);
        }
        if (order == null) {
            log.warnf("Khong tim thay don hang de xac nhan: txnRef=%s", txnRef);
            return;
        }

        order.trangThaiThanhToan = Order.TrangThaiThanhToan.DA_THANH_TOAN;
        order.trangThaiDonHang = Order.TrangThaiDonHang.DA_XAC_NHAN;
        order.maGiaoDichVnpay = maGiaoDich;
        order.ngayThanhToan = LocalDateTime.now();

        log.infof("Don hang %s da thanh toan thanh cong qua VNPay.", order.id);
    }

    @Transactional
    public void huyThanhToanVnpay(String txnRef) {
        Order order = Order.findById(txnRef);
        if (order != null) {
            order.trangThaiDonHang = Order.TrangThaiDonHang.DA_HUY;
            log.infof("Don hang %s da bi huy do thanh toan that bai.", order.id);
        }
    }

    @Transactional
    public DonHangResponse capNhatTrangThai(String orderId, String trangThai) {
        Order order = Order.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Khong tim thay don hang: " + orderId);
        }

        try {
            order.trangThaiDonHang = Order.TrangThaiDonHang.valueOf(trangThai);
        } catch (Exception e) {
            throw new BadRequestException("Trang thai khong hop le: " + trangThai);
        }
        return DonHangResponse.from(order);
    }

    private void truTonKho(Order order) {
        for (OrderItem item : order.cacMatHang) {
            try (Response response = inventoryClient.giamSoLuong(item.itemId, item.soLuong)) {
                if (response.getStatus() >= 400) {
                    throw new BadRequestException("Ton kho khong du cho san pham: " + item.tenSanPham);
                }
            } catch (WebApplicationException e) {
                throw new BadRequestException("Ton kho khong du cho san pham: " + item.tenSanPham);
            } catch (Exception e) {
                throw new BadRequestException("Khong the cap nhat ton kho cho san pham: " + item.tenSanPham);
            }
        }
    }

    private void kiemTraTonKho(Order order) {
        for (OrderItem item : order.cacMatHang) {
            try {
                InventoryServiceClient.InventorySnapshot inventory = inventoryClient.layTheoItemId(item.itemId);
                int quantity = inventory != null ? inventory.quantity() : 0;
                if (quantity < item.soLuong) {
                    throw new BadRequestException("Ton kho khong du cho san pham: " + item.tenSanPham);
                }
            } catch (BadRequestException e) {
                throw e;
            } catch (Exception e) {
                throw new BadRequestException("Khong the kiem tra ton kho cho san pham: " + item.tenSanPham);
            }
        }
    }

    private Order.PhuongThucThanhToan parsePhuongThuc(String s) {
        try {
            return Order.PhuongThucThanhToan.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return Order.PhuongThucThanhToan.VNPAY;
        }
    }
}
