import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-payment-result',
  template: `
<div class="min-h-screen bg-gray-50 flex items-center justify-center px-4 py-16">
  <div class="max-w-md w-full text-center">

    <!-- SUCCESS -->
    <div *ngIf="trangThai === 'thanh-cong'" class="cs-card p-10">
      <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <span class="text-5xl">✅</span>
      </div>
      <h1 class="text-2xl font-black text-gray-900 mb-2">Thanh toán thành công!</h1>
      <p class="text-gray-500 mb-2">Đơn hàng của bạn đã được xác nhận.</p>
      <p *ngIf="orderId" class="font-mono text-sm bg-gray-100 rounded-lg px-3 py-2 inline-block mb-1">
        Mã đơn: <strong>{{ orderId }}</strong>
      </p>
      <p *ngIf="maGiaoDich" class="font-mono text-xs text-gray-400 mb-6">
        Mã GD VNPay: {{ maGiaoDich }}
      </p>
      <div class="bg-green-50 border border-green-100 rounded-xl p-4 text-sm text-green-700 mb-6 text-left">
        <p>🎉 Cảm ơn bạn đã mua sắm tại CoolStore!</p>
        <p class="mt-1">Chúng tôi sẽ xử lý và giao hàng trong vòng 2-5 ngày làm việc.</p>
      </div>
      <div class="flex gap-3">
        <a [routerLink]="['/don-hang', orderId]" class="btn-primary flex-1 py-3 text-sm">
          📦 Xem đơn hàng
        </a>
        <a routerLink="/" class="btn-secondary flex-1 py-3 text-sm">🛍️ Mua tiếp</a>
      </div>
    </div>

    <!-- FAILED -->
    <div *ngIf="trangThai === 'that-bai'" class="cs-card p-10">
      <div class="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <span class="text-5xl">❌</span>
      </div>
      <h1 class="text-2xl font-black text-gray-900 mb-2">Thanh toán thất bại</h1>
      <p class="text-gray-500 mb-1">Giao dịch không thành công.</p>
      <p *ngIf="maLoi" class="text-xs text-gray-400 mb-6">Mã lỗi VNPay: {{ maLoi }}</p>
      <div class="bg-red-50 border border-red-100 rounded-xl p-4 text-sm text-red-700 mb-6 text-left">
        <p>Đơn hàng <strong>{{ orderId }}</strong> đã bị hủy do thanh toán không thành công.</p>
        <p class="mt-1">Vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>
      </div>
      <div class="flex gap-3">
        <a routerLink="/gio-hang" class="btn-primary flex-1 py-3 text-sm">🔄 Thử lại</a>
        <a routerLink="/" class="btn-secondary flex-1 py-3 text-sm">🏠 Về trang chủ</a>
      </div>
    </div>

    <!-- LOADING / UNKNOWN -->
    <div *ngIf="!trangThai" class="cs-card p-10">
      <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <span class="text-5xl animate-spin inline-block">⏳</span>
      </div>
      <p class="text-gray-500">Đang kiểm tra kết quả thanh toán...</p>
    </div>

  </div>
</div>
  `,
  styles: []
})
export class PaymentResultComponent implements OnInit {
  trangThai: string | null = null;
  orderId:   string | null = null;
  maGiaoDich: string | null = null;
  maLoi: string | null = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.trangThai  = this.route.snapshot.queryParamMap.get('trangThai');
    this.orderId    = this.route.snapshot.queryParamMap.get('orderId');
    this.maGiaoDich = this.route.snapshot.queryParamMap.get('maGiaoDich');
    this.maLoi      = this.route.snapshot.queryParamMap.get('maLoi');
  }
}
