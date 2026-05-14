import { Component, OnInit } from '@angular/core';
// Sửa 2 dòng import bị lỗi thành thế này:
import { OrderService } from '../services/order.service';
import { DonHang } from '../model/models';

@Component({
  selector: 'app-orders',
  template: `
<div class="max-w-4xl mx-auto px-4 py-8">
  <h1 class="text-2xl font-bold text-gray-900 mb-6">📦 Đơn hàng của tôi</h1>

  <!-- Loading -->
  <div *ngIf="dangTai" class="space-y-4">
    <div *ngFor="let i of [1,2,3]" class="cs-card p-5">
      <div class="skeleton h-4 w-1/3 mb-3"></div>
      <div class="skeleton h-4 w-2/3 mb-2"></div>
      <div class="skeleton h-4 w-1/4"></div>
    </div>
  </div>

  <!-- Rỗng -->
  <div *ngIf="!dangTai && donHangList.length === 0" class="empty-state">
    <div class="empty-state-icon">📭</div>
    <p class="empty-state-text">Bạn chưa có đơn hàng nào</p>
    <a routerLink="/" class="btn-primary mt-4 py-2.5 px-6 inline-block text-sm">Mua sắm ngay</a>
  </div>

  <!-- Danh sách -->
  <div *ngIf="!dangTai" class="space-y-4">
    <div *ngFor="let dh of donHangList"
         [routerLink]="['/don-hang', dh.id]"
         class="cs-card p-5 cursor-pointer hover:border-red-200 transition-all">
      <div class="flex items-start justify-between gap-4 flex-wrap">
        <div>
          <p class="font-bold text-gray-900">{{ dh.id }}</p>
          <p class="text-gray-500 text-sm mt-0.5">{{ dh.ngayTao | date:'dd/MM/yyyy HH:mm' }}</p>
          <p class="text-gray-600 text-sm mt-1">{{ dh.cacMatHang.length }} sản phẩm</p>
        </div>
        <div class="text-right">
          <p class="font-bold text-red-600 text-lg">{{ dh.tongTien | currency:'USD' }}</p>
          <span class="badge mt-1" [class]="badgeClass(dh.trangThaiDonHang)">
            {{ dh.trangThaiDonHangText }}
          </span>
          <br>
          <span class="badge mt-1" [class]="badgePay(dh.trangThaiThanhToan)">
            {{ dh.trangThaiThanhToanText }}
          </span>
        </div>
      </div>
      <!-- Sản phẩm preview -->
      <div class="flex gap-2 mt-3 flex-wrap">
        <div *ngFor="let sp of dh.cacMatHang.slice(0,4)"
             class="flex items-center gap-1.5 bg-gray-50 rounded-lg px-2 py-1">
          <img [src]="anh(sp.hinhAnh)" class="w-6 h-6 object-cover rounded">
          <span class="text-xs text-gray-600 truncate max-w-[100px]">{{ sp.tenSanPham }}</span>
        </div>
        <span *ngIf="dh.cacMatHang.length > 4"
              class="text-xs text-gray-400 flex items-center">+{{ dh.cacMatHang.length - 4 }} sản phẩm khác</span>
      </div>
    </div>
  </div>
</div>
  `,
  styles: []
})
export class OrdersComponent implements OnInit {
  donHangList: DonHang[] = [];
  dangTai = true;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.layDonHangCuaToi().subscribe({
      next: r => { this.donHangList = r.duLieu || []; this.dangTai = false; },
      error: () => { this.dangTai = false; }
    });
  }

  anh(img: string): string {
    if (!img) return 'https://via.placeholder.com/24?text=SP';
    return img.startsWith('http') ? img : `assets/${img}`;
  }

  badgeClass(trangThai: string): string {
    const map: Record<string,string> = {
      CHO_XAC_NHAN: 'badge-warning', DA_XAC_NHAN: 'badge-info',
      DANG_GIAO: 'badge-info', DA_GIAO: 'badge-success', DA_HUY: 'badge-danger'
    };
    return map[trangThai] || 'badge-info';
  }

  badgePay(trangThai: string): string {
    return trangThai === 'DA_THANH_TOAN' ? 'badge-success' : 'badge-warning';
  }
}
