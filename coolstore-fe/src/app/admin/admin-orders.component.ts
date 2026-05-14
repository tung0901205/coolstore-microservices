import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { OrderService } from '../services/order.service';
import { DonHang } from '../model/models';

@Component({
  selector: 'app-admin-orders',
  template: `
<div class="p-6">
  <div class="flex items-center justify-between mb-6 flex-wrap gap-3">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">🛵 Quản lý đơn hàng</h1>
      <p class="text-gray-500 text-sm mt-0.5">{{ filtered.length }} đơn hàng</p>
    </div>
  </div>

  <!-- Filters -->
  <div class="flex gap-3 mb-5 flex-wrap">
    <input [(ngModel)]="search" (ngModelChange)="loc()" placeholder="🔍 Mã đơn / Khách hàng..."
           class="form-input w-56 py-2 text-sm">
    <select [(ngModel)]="filterTT" (ngModelChange)="loc()" class="form-input w-44 py-2 text-sm">
      <option value="">Tất cả trạng thái</option>
      <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
      <option value="DA_XAC_NHAN">Đã xác nhận</option>
      <option value="DANG_GIAO">Đang giao</option>
      <option value="DA_GIAO">Đã giao</option>
      <option value="DA_HUY">Đã hủy</option>
    </select>
    <select [(ngModel)]="filterPay" (ngModelChange)="loc()" class="form-input w-44 py-2 text-sm">
      <option value="">Tất cả thanh toán</option>
      <option value="DA_THANH_TOAN">Đã thanh toán</option>
      <option value="CHUA_THANH_TOAN">Chưa thanh toán</option>
    </select>
  </div>

  <!-- Table -->
  <div class="cs-card overflow-hidden">
    <div *ngIf="dangTai" class="p-8 text-center text-gray-400">Đang tải đơn hàng...</div>
    <div *ngIf="!dangTai" class="overflow-x-auto">
      <table class="cs-table">
        <thead>
          <tr>
            <th>Mã đơn</th><th>Khách hàng</th><th>Sản phẩm</th>
            <th>Tổng tiền</th><th>Thanh toán</th><th>Trạng thái</th>
            <th class="text-right">Cập nhật</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let dh of filtered">
            <td>
              <a [routerLink]="['/don-hang', dh.id]" target="_blank"
                 class="font-mono text-xs text-red-600 hover:underline">{{ dh.id }}</a>
              <p class="text-gray-400 text-xs mt-0.5">{{ dh.ngayTao | date:'dd/MM HH:mm' }}</p>
            </td>
            <td>
              <p class="font-medium text-sm text-gray-800">{{ dh.tenNguoiDat }}</p>
              <p class="text-gray-400 text-xs">{{ dh.soDienThoai }}</p>
            </td>
            <td class="text-sm text-gray-600">{{ dh.cacMatHang.length }} sản phẩm</td>
            <td><span class="font-bold text-gray-800">{{ dh.tongTien | currency:'USD' }}</span></td>
            <td>
              <span class="badge text-xs"
                    [class]="dh.trangThaiThanhToan === 'DA_THANH_TOAN' ? 'badge-success' : 'badge-warning'">
                {{ dh.trangThaiThanhToanText }}
              </span>
            </td>
            <td>
              <span class="badge text-xs"
                    [class]="dh.trangThaiDonHang==='DA_GIAO'?'badge-success':dh.trangThaiDonHang==='DA_HUY'?'badge-danger':'badge-warning'">
                {{ dh.trangThaiDonHangText }}
              </span>
            </td>
            <td>
              <div class="flex justify-end">
                <select (change)="doiTrangThai(dh, $any($event.target).value)"
                        class="text-xs border border-gray-200 rounded-lg px-2 py-1.5 cursor-pointer focus:outline-none focus:border-red-400 bg-white">
                  <option value="">-- Đổi --</option>
                  <option value="DA_XAC_NHAN">✅ Xác nhận</option>
                  <option value="DANG_GIAO">🚚 Đang giao</option>
                  <option value="DA_GIAO">📬 Đã giao</option>
                  <option value="DA_HUY">❌ Hủy đơn</option>
                </select>
              </div>
            </td>
          </tr>
          <tr *ngIf="filtered.length === 0">
            <td colspan="7" class="text-center py-12 text-gray-400">Không có đơn hàng nào</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
  `,
  styles: []
})
export class AdminOrdersComponent implements OnInit {
  orders:   DonHang[] = [];
  filtered: DonHang[] = [];
  search    = '';
  filterTT  = '';
  filterPay = '';
  dangTai   = true;

  constructor(private order: OrderService, private snack: MatSnackBar) {}

  ngOnInit(): void { this.tai(); }

  tai(): void {
    this.dangTai = true;
    this.order.layTatCaDonHang().subscribe({
      next: r => { this.orders = r.duLieu || []; this.loc(); this.dangTai = false; },
      error: () => this.dangTai = false
    });
  }

  loc(): void {
    let r = [...this.orders];
    if (this.search)    r = r.filter(d => d.id.toLowerCase().includes(this.search.toLowerCase()) || d.tenNguoiDat?.toLowerCase().includes(this.search.toLowerCase()));
    if (this.filterTT)  r = r.filter(d => d.trangThaiDonHang === this.filterTT);
    if (this.filterPay) r = r.filter(d => d.trangThaiThanhToan === this.filterPay);
    this.filtered = r;
  }

  doiTrangThai(dh: DonHang, trangThai: string): void {
    if (!trangThai) return;
    this.order.capNhatTrangThai(dh.id, trangThai).subscribe({
      next: () => {
        this.snack.open('✅ Cập nhật trạng thái thành công!', 'Đóng', { duration: 2500, panelClass: ['snack-success'] });
        this.tai();
      },
      error: () => this.snack.open('❌ Cập nhật thất bại', 'Đóng', { duration: 3000 })
    });
  }
}
