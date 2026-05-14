import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-shell',
  template: `
<div class="flex min-h-screen bg-gray-50">

  <!-- ── SIDEBAR ────────────────────────────────────────────── -->
  <aside class="w-60 bg-white border-r border-gray-100 flex flex-col flex-shrink-0 sticky top-0 h-screen">
    <!-- Logo -->
    <div class="h-16 flex items-center px-5 border-b border-gray-100">
      <div class="flex items-center gap-2.5">
        <div class="w-8 h-8 bg-red-600 rounded-lg flex items-center justify-center">
          <span class="text-white font-black text-sm">C</span>
        </div>
        <div>
          <p class="font-black text-gray-900 text-sm leading-none">COOLSTORE</p>
          <p class="text-gray-400 text-xs leading-none mt-0.5">Quản trị hệ thống</p>
        </div>
      </div>
    </div>

    <!-- Nav -->
    <nav class="flex-1 p-3 space-y-1 overflow-y-auto">
      <p class="px-3 py-2 text-xs font-semibold text-gray-400 uppercase tracking-wider">Tổng quan</p>
      <a routerLink="/admin/dashboard" routerLinkActive="active" class="admin-sidebar-link">
        <span>📊</span> Dashboard
      </a>

      <p class="px-3 py-2 text-xs font-semibold text-gray-400 uppercase tracking-wider mt-2">Quản lý</p>
      <a routerLink="/admin/san-pham" routerLinkActive="active" class="admin-sidebar-link">
        <span>📦</span> Sản phẩm
      </a>
      <a routerLink="/admin/ton-kho" routerLinkActive="active" class="admin-sidebar-link">
        <span>🏭</span> Tồn kho
      </a>
      <a routerLink="/admin/don-hang" routerLinkActive="active" class="admin-sidebar-link">
        <span>🛵</span> Đơn hàng
      </a>
      <a routerLink="/admin/nguoi-dung" routerLinkActive="active" class="admin-sidebar-link">
        <span>👥</span> Người dùng
      </a>

      <p class="px-3 py-2 text-xs font-semibold text-gray-400 uppercase tracking-wider mt-2">Khác</p>
      <a routerLink="/" class="admin-sidebar-link">
        <span>🏠</span> Về trang chủ
      </a>
    </nav>

    <!-- User info at bottom -->
    <div class="p-4 border-t border-gray-100">
      <div class="flex items-center gap-2.5">
        <div class="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center flex-shrink-0">
          <span class="text-purple-700 font-bold text-sm">{{ initials }}</span>
        </div>
        <div class="min-w-0 flex-1">
          <p class="font-semibold text-gray-800 text-xs truncate">{{ user?.fullName }}</p>
          <p class="text-gray-400 text-xs">👑 Quản trị viên</p>
        </div>
        <button (click)="dangXuat()" title="Đăng xuất"
                class="text-gray-400 hover:text-red-500 transition-colors text-sm">🚪</button>
      </div>
    </div>
  </aside>

  <!-- ── MAIN CONTENT ────────────────────────────────────────── -->
  <main class="flex-1 min-w-0 overflow-auto">
    <router-outlet></router-outlet>
  </main>

</div>
  `,
  styles: []
})
export class AdminShellComponent {
  get user() { return this.auth.layNguoiDungHienTai(); }
  get initials() { return (this.user?.fullName || 'A').charAt(0).toUpperCase(); }

  constructor(private auth: AuthService, private router: Router) {}

  dangXuat(): void { this.auth.dangXuat(); }
}
