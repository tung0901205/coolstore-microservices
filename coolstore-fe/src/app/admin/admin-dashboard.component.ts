import { Component, OnInit } from '@angular/core';
import { CatalogService } from '../services/catalog.service';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-admin-dashboard',
  template: `
<div class="p-6">
  <!-- Page header -->
  <div class="mb-6">
    <h1 class="text-2xl font-bold text-gray-900">📊 Dashboard</h1>
    <p class="text-gray-500 text-sm mt-1">Tổng quan hệ thống CoolStore</p>
  </div>

  <!-- Stats cards -->
  <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">

    <div class="cs-card p-5">
      <div class="flex items-center justify-between mb-3">
        <p class="text-gray-500 text-sm font-medium">Tổng sản phẩm</p>
        <div class="w-10 h-10 bg-blue-50 rounded-xl flex items-center justify-center text-xl">📦</div>
      </div>
      <p class="text-3xl font-black text-gray-900">{{ tongSanPham }}</p>
      <p class="text-green-500 text-xs mt-1 font-medium">Trong kho</p>
    </div>

    <div class="cs-card p-5">
      <div class="flex items-center justify-between mb-3">
        <p class="text-gray-500 text-sm font-medium">Đơn hàng hôm nay</p>
        <div class="w-10 h-10 bg-orange-50 rounded-xl flex items-center justify-center text-xl">🛵</div>
      </div>
      <p class="text-3xl font-black text-gray-900">{{ donHangHomNay }}</p>
      <p class="text-orange-500 text-xs mt-1 font-medium">Cần xử lý</p>
    </div>

    <div class="cs-card p-5">
      <div class="flex items-center justify-between mb-3">
        <p class="text-gray-500 text-sm font-medium">Doanh thu tháng</p>
        <div class="w-10 h-10 bg-green-50 rounded-xl flex items-center justify-center text-xl">💰</div>
      </div>
      <p class="text-2xl font-black text-gray-900">{{ doanhThuThang | currency:'USD':'symbol':'1.0-0' }}</p>
      <p class="text-green-500 text-xs mt-1 font-medium">Đã thanh toán</p>
    </div>

    <div class="cs-card p-5">
      <div class="flex items-center justify-between mb-3">
        <p class="text-gray-500 text-sm font-medium">Người dùng</p>
        <div class="w-10 h-10 bg-purple-50 rounded-xl flex items-center justify-center text-xl">👥</div>
      </div>
      <p class="text-3xl font-black text-gray-900">{{ tongNguoiDung }}</p>
      <p class="text-purple-500 text-xs mt-1 font-medium">Tài khoản hoạt động</p>
    </div>

  </div>

  <!-- Recent orders + Quick actions -->
  <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">

    <!-- Đơn hàng gần đây -->
    <div class="xl:col-span-2 cs-card">
      <div class="p-5 border-b border-gray-50 flex items-center justify-between">
        <h2 class="font-bold text-gray-800">🛵 Đơn hàng gần đây</h2>
        <a routerLink="/admin/don-hang" class="text-red-600 hover:text-red-700 text-sm font-medium">Xem tất cả →</a>
      </div>
      <div class="overflow-x-auto">
        <table class="cs-table">
          <thead>
            <tr>
              <th>Mã đơn</th><th>Khách hàng</th><th>Tổng tiền</th><th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let dh of donHangGanDay">
              <td>
                <a [routerLink]="['/don-hang', dh.id]"
                   class="font-mono text-red-600 hover:underline text-xs">{{ dh.id }}</a>
              </td>
              <td class="text-sm">{{ dh.tenNguoiDat }}</td>
              <td class="font-semibold text-sm">{{ dh.tongTien | currency:'USD' }}</td>
              <td>
                <span class="badge text-xs"
                      [class]="dh.trangThaiDonHang === 'DA_GIAO' ? 'badge-success' :
                               dh.trangThaiDonHang === 'DA_HUY' ? 'badge-danger' : 'badge-warning'">
                  {{ dh.trangThaiDonHangText }}
                </span>
              </td>
            </tr>
            <tr *ngIf="donHangGanDay.length === 0">
              <td colspan="4" class="text-center text-gray-400 py-6">Chưa có đơn hàng</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Quick links -->
    <div class="space-y-4">
      <div class="cs-card p-5">
        <h2 class="font-bold text-gray-800 mb-4">⚡ Thao tác nhanh</h2>
        <div class="space-y-2">
          <a routerLink="/admin/san-pham"
             class="flex items-center gap-3 p-3 rounded-xl bg-blue-50 hover:bg-blue-100 transition-colors">
            <span class="text-xl">➕</span>
            <span class="text-sm font-medium text-blue-700">Thêm sản phẩm mới</span>
          </a>
          <a routerLink="/admin/don-hang"
             class="flex items-center gap-3 p-3 rounded-xl bg-orange-50 hover:bg-orange-100 transition-colors">
            <span class="text-xl">📋</span>
            <span class="text-sm font-medium text-orange-700">Xử lý đơn hàng</span>
          </a>
          <a routerLink="/admin/ton-kho"
             class="flex items-center gap-3 p-3 rounded-xl bg-green-50 hover:bg-green-100 transition-colors">
            <span class="text-xl">🏭</span>
            <span class="text-sm font-medium text-green-700">Cập nhật kho</span>
          </a>
          <a routerLink="/admin/nguoi-dung"
             class="flex items-center gap-3 p-3 rounded-xl bg-purple-50 hover:bg-purple-100 transition-colors">
            <span class="text-xl">👥</span>
            <span class="text-sm font-medium text-purple-700">Quản lý tài khoản</span>
          </a>
        </div>
      </div>
    </div>

  </div>
</div>
  `,
  styles: []
})
export class AdminDashboardComponent implements OnInit {
  tongSanPham    = 0;
  tongNguoiDung  = 0;
  donHangHomNay  = 0;
  doanhThuThang  = 0;
  donHangGanDay: any[] = [];

  constructor(
    private catalog: CatalogService,
    private order:   OrderService,
    private auth:    AuthService
  ) {}

  ngOnInit(): void {
    this.catalog.layTatCaSanPham().subscribe(p => this.tongSanPham = p.length);

    this.order.layTatCaDonHang().subscribe(r => {
      const list = r.duLieu || [];
      this.donHangGanDay = list.slice(0, 8);

      const homNay = new Date().toDateString();
      this.donHangHomNay = list.filter(d =>
        new Date(d.ngayTao).toDateString() === homNay).length;

      const thangNay  = new Date().getMonth();
      this.doanhThuThang = list
        .filter(d => d.trangThaiThanhToan === 'DA_THANH_TOAN'
               && new Date(d.ngayTao).getMonth() === thangNay)
        .reduce((s, d) => s + d.tongTien, 0);
    });

    this.auth.layDanhSachNguoiDung().subscribe(r => {
      this.tongNguoiDung = (r.duLieu || []).filter((u: any) => u.active).length;
    });
  }
}
