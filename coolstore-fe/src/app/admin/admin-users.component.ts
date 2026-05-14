import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../services/auth.service';
import { NguoiDung } from '../model/models';

@Component({
  selector: 'app-admin-users',
  template: `
<div class="p-6">
  <div class="flex items-center justify-between mb-6 flex-wrap gap-3">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">👥 Quản lý người dùng</h1>
      <p class="text-gray-500 text-sm mt-0.5">{{ filtered.length }} tài khoản</p>
    </div>
  </div>

  <!-- Filter -->
  <div class="flex gap-3 mb-5 flex-wrap">
    <input [(ngModel)]="search" (ngModelChange)="loc()"
           placeholder="🔍 Tìm theo tên / email..."
           class="form-input w-64 py-2 text-sm">
    <select [(ngModel)]="filterRole" (ngModelChange)="loc()"
            class="form-input w-36 py-2 text-sm cursor-pointer">
      <option value="">Tất cả</option>
      <option value="ADMIN">Admin</option>
      <option value="USER">User</option>
    </select>
    <select [(ngModel)]="filterActive" (ngModelChange)="loc()"
            class="form-input w-40 py-2 text-sm cursor-pointer">
      <option value="">Tất cả trạng thái</option>
      <option value="true">Đang hoạt động</option>
      <option value="false">Đã vô hiệu hóa</option>
    </select>
  </div>

  <!-- Table -->
  <div class="cs-card overflow-hidden">
    <div *ngIf="dangTai" class="p-6 space-y-3">
      <div *ngFor="let i of [1,2,3,4]" class="flex gap-3 items-center">
        <div class="skeleton w-10 h-10 rounded-full"></div>
        <div class="flex-1 space-y-2">
          <div class="skeleton h-4 w-1/3"></div>
          <div class="skeleton h-3 w-1/2"></div>
        </div>
      </div>
    </div>
    <div *ngIf="!dangTai" class="overflow-x-auto">
      <table class="cs-table">
        <thead>
          <tr>
            <th>Người dùng</th><th>Email</th><th>Quyền</th>
            <th>Trạng thái</th><th>Ngày tạo</th><th class="text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let u of filtered">
            <td>
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-full flex items-center justify-center font-bold text-sm flex-shrink-0"
                     [class]="u.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-600'">
                  {{ u.fullName?.charAt(0)?.toUpperCase() }}
                </div>
                <div>
                  <p class="font-semibold text-gray-800 text-sm">{{ u.fullName }}</p>
                  <p class="text-gray-400 text-xs">&#64;{{ u.username }}</p>
                </div>
              </div>
            </td>
            <td class="text-sm text-gray-600">{{ u.email }}</td>
            <td>
              <span class="badge text-xs" [class]="u.role === 'ADMIN' ? 'badge-purple' : 'badge-info'">
                {{ u.role === 'ADMIN' ? '👑 Admin' : '👤 User' }}
              </span>
            </td>
            <td>
              <span class="badge text-xs" [class]="u.active ? 'badge-success' : 'badge-danger'">
                {{ u.active ? '✅ Hoạt động' : '🚫 Vô hiệu hóa' }}
              </span>
            </td>
            <td class="text-gray-400 text-xs">{{ u.createdAt | date:'dd/MM/yyyy' }}</td>
            <td>
              <div class="flex justify-end gap-2">
                <!-- Đổi quyền -->
                <button *ngIf="u.role === 'USER'" (click)="doiQuyen(u, 'ADMIN')"
                        class="px-2.5 py-1.5 text-xs font-medium text-purple-600 bg-purple-50 hover:bg-purple-100 rounded-lg transition-colors whitespace-nowrap">
                  👑 Nâng Admin
                </button>
                <button *ngIf="u.role === 'ADMIN'" (click)="doiQuyen(u, 'USER')"
                        class="px-2.5 py-1.5 text-xs font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors whitespace-nowrap">
                  👤 Hạ xuống
                </button>
                <!-- Bật/tắt tài khoản -->
                <button (click)="doiTrangThai(u)"
                        class="px-2.5 py-1.5 text-xs font-medium rounded-lg transition-colors whitespace-nowrap"
                        [class]="u.active
                          ? 'text-red-600 bg-red-50 hover:bg-red-100'
                          : 'text-green-600 bg-green-50 hover:bg-green-100'">
                  {{ u.active ? '🚫 Khóa' : '✅ Mở khóa' }}
                </button>
              </div>
            </td>
          </tr>
          <tr *ngIf="filtered.length === 0">
            <td colspan="6" class="text-center py-10 text-gray-400">Không tìm thấy người dùng</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
  `,
  styles: []
})
export class AdminUsersComponent implements OnInit {
  users:     NguoiDung[] = [];
  filtered:  NguoiDung[] = [];
  search      = '';
  filterRole  = '';
  filterActive = '';
  dangTai     = true;

  constructor(private auth: AuthService, private snack: MatSnackBar) {}

  ngOnInit(): void { this.tai(); }

  tai(): void {
    this.dangTai = true;
    this.auth.layDanhSachNguoiDung().subscribe({
      next: r => { this.users = r.duLieu || []; this.loc(); this.dangTai = false; },
      error: () => this.dangTai = false
    });
  }

  loc(): void {
    let r = [...this.users];
    if (this.search)      r = r.filter(u => u.fullName.toLowerCase().includes(this.search.toLowerCase()) || u.email.toLowerCase().includes(this.search.toLowerCase()) || u.username.toLowerCase().includes(this.search.toLowerCase()));
    if (this.filterRole)   r = r.filter(u => u.role === this.filterRole);
    if (this.filterActive) r = r.filter(u => String(u.active) === this.filterActive);
    this.filtered = r;
  }

  doiTrangThai(u: NguoiDung): void {
    this.auth.capNhatTrangThai(u.id, !u.active).subscribe({
      next: () => {
        u.active = !u.active;
        this.snack.open(u.active ? '✅ Đã mở khóa tài khoản' : '🚫 Đã khóa tài khoản', 'Đóng', {
          duration: 2500, panelClass: ['snack-success']
        });
        this.loc();
      },
      error: () => this.snack.open('❌ Cập nhật thất bại', 'Đóng', { duration: 3000 })
    });
  }

  doiQuyen(u: NguoiDung, role: string): void {
    this.auth.capNhatQuyen(u.id, role).subscribe({
      next: () => {
        u.role = role as 'USER' | 'ADMIN';
        this.snack.open(`✅ Đã cập nhật quyền: ${role}`, 'Đóng', { duration: 2500, panelClass: ['snack-success'] });
        this.loc();
      },
      error: () => this.snack.open('❌ Cập nhật thất bại', 'Đóng', { duration: 3000 })
    });
  }
}
