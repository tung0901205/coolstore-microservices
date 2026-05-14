import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../services/order.service';
import { DonHang } from '../model/models';

@Component({
  selector: 'app-order-detail',
  template: `
<div class="max-w-3xl mx-auto px-4 py-8">
  <div class="flex items-center gap-3 mb-6">
    <a routerLink="/don-hang" class="text-red-600 hover:text-red-700 font-medium text-sm">← Danh sách đơn hàng</a>
  </div>

  <div *ngIf="dangTai" class="cs-card p-8 text-center text-gray-400">Đang tải...</div>

  <div *ngIf="!dangTai && donHang">
    <!-- Header -->
    <div class="cs-card p-6 mb-4">
      <div class="flex items-start justify-between flex-wrap gap-4">
        <div>
          <p class="text-gray-500 text-xs uppercase tracking-wide">Mã đơn hàng</p>
          <p class="font-black text-gray-900 text-xl">{{ donHang.id }}</p>
          <p class="text-gray-400 text-sm mt-1">Đặt lúc {{ donHang.ngayTao | date:'HH:mm dd/MM/yyyy' }}</p>
        </div>
        <div class="text-right space-y-1">
          <span class="badge" [class]="badgeClass(donHang.trangThaiDonHang)">
            {{ donHang.trangThaiDonHangText }}
          </span>
          <br>
          <span class="badge" [class]="donHang.trangThaiThanhToan==='DA_THANH_TOAN'?'badge-success':'badge-warning'">
            {{ donHang.trangThaiThanhToanText }}
          </span>
          <p *ngIf="donHang.maGiaoDichVnpay" class="text-xs text-gray-400 mt-1">
            Mã GD: {{ donHang.maGiaoDichVnpay }}
          </p>
        </div>
      </div>
    </div>

    <!-- Thông tin giao hàng -->
    <div class="cs-card p-5 mb-4">
      <h3 class="font-bold text-gray-800 mb-3">📍 Thông tin giao hàng</h3>
      <div class="grid grid-cols-2 gap-3 text-sm">
        <div><p class="text-gray-400 text-xs">Người nhận</p><p class="font-medium">{{ donHang.tenNguoiDat }}</p></div>
        <div><p class="text-gray-400 text-xs">Số điện thoại</p><p class="font-medium">{{ donHang.soDienThoai }}</p></div>
        <div class="col-span-2"><p class="text-gray-400 text-xs">Địa chỉ</p><p class="font-medium">{{ donHang.diaChiGiaoHang }}</p></div>
        <div><p class="text-gray-400 text-xs">Thanh toán</p>
          <p class="font-medium">{{ donHang.phuongThucThanhToan === 'VNPAY' ? '💳 VNPay' : '💵 COD' }}</p></div>
        <div *ngIf="donHang.ghiChu"><p class="text-gray-400 text-xs">Ghi chú</p><p class="font-medium">{{ donHang.ghiChu }}</p></div>
      </div>
    </div>

    <!-- Sản phẩm -->
    <div class="cs-card p-5 mb-4">
      <h3 class="font-bold text-gray-800 mb-4">🛍️ Sản phẩm đặt hàng</h3>
      <div *ngFor="let sp of donHang.cacMatHang" class="flex gap-3 py-3 border-b border-gray-50 last:border-0">
        <img [src]="anh(sp.hinhAnh)" [alt]="sp.tenSanPham"
             class="w-14 h-14 object-cover rounded-xl border border-gray-100">
        <div class="flex-1 min-w-0">
          <p class="font-semibold text-gray-800 text-sm truncate">{{ sp.tenSanPham }}</p>
          <p class="text-gray-400 text-xs mt-0.5">Đơn giá: {{ sp.donGia | currency:'USD' }} × {{ sp.soLuong }}</p>
        </div>
        <p class="font-bold text-gray-800">{{ sp.thanhTien | currency:'USD' }}</p>
      </div>
      <div class="flex justify-between font-bold text-base text-gray-900 mt-4 pt-3 border-t">
        <span>Tổng cộng</span>
        <span class="text-red-600 text-lg">{{ donHang.tongTien | currency:'USD' }}</span>
      </div>
    </div>
  </div>
</div>
  `,
  styles: []
})
export class OrderDetailComponent implements OnInit {
  donHang: DonHang | null = null;
  dangTai = true;

  constructor(private route: ActivatedRoute, private orderService: OrderService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.orderService.layChiTietDonHang(id).subscribe({
      next: (r: any) => { this.donHang = r.duLieu; this.dangTai = false; },
      error: () => this.dangTai = false
    });
  }

  anh(img: string): string {
    if (!img) return 'https://via.placeholder.com/56?text=SP';
    return img.startsWith('http') ? img : `assets/${img}`;
  }

  badgeClass(s: string): string {
    const m: Record<string,string> = {
      CHO_XAC_NHAN:'badge-warning', DA_XAC_NHAN:'badge-info',
      DANG_GIAO:'badge-info', DA_GIAO:'badge-success', DA_HUY:'badge-danger'
    };
    return m[s] || 'badge-info';
  }
}
