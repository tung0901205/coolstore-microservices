import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InventoryService } from '../services/inventory.service';
import { TonKho } from '../model/models';

@Component({
  selector: 'app-admin-inventory',
  template: `
<div class="p-6">
  <div class="flex items-center justify-between mb-6 flex-wrap gap-3">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">🏭 Quản lý tồn kho</h1>
      <p class="text-gray-500 text-sm mt-0.5">{{ tonKho.length }} bản ghi</p>
    </div>
  </div>

  <!-- Filter -->
  <div class="flex gap-3 mb-5">
    <input [(ngModel)]="search" (ngModelChange)="loc()"
           placeholder="🔍 Tìm theo ItemID..."
           class="form-input w-56 py-2 text-sm">
    <select [(ngModel)]="tinhTrang" (ngModelChange)="loc()"
            class="form-input w-44 py-2 text-sm cursor-pointer">
      <option value="">Tất cả</option>
      <option value="het">Hết hàng (0)</option>
      <option value="sap-het">Sắp hết (≤10)</option>
    </select>
  </div>

  <!-- Table -->
  <div class="cs-card overflow-hidden">
    <div *ngIf="dangTai" class="p-6 space-y-3">
      <div *ngFor="let i of [1,2,3,4,5,6]" class="skeleton h-10 w-full rounded-lg"></div>
    </div>
    <div *ngIf="!dangTai" class="overflow-x-auto">
      <table class="cs-table">
        <thead>
          <tr>
            <th>Item ID</th><th>Kho hàng</th><th>Vị trí</th>
            <th>Số lượng</th><th>Trạng thái</th><th class="text-right">Cập nhật</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let tk of filtered">
            <td><code class="text-xs bg-gray-100 px-2 py-0.5 rounded">{{ tk.itemId }}</code></td>
            <td class="text-sm">{{ tk.location }}</td>
            <td>
              <a *ngIf="tk.link" [href]="tk.link" target="_blank"
                 class="text-xs text-blue-500 hover:underline">📍 Xem bản đồ</a>
            </td>
            <td>
              <span class="font-bold text-lg"
                    [class]="tk.quantity === 0 ? 'text-red-600' : tk.quantity <= 10 ? 'text-orange-500' : 'text-gray-800'">
                {{ tk.quantity }}
              </span>
            </td>
            <td>
              <span class="badge text-xs"
                    [class]="tk.quantity === 0 ? 'badge-danger' : tk.quantity <= 10 ? 'badge-warning' : 'badge-success'">
                {{ tk.quantity === 0 ? 'Hết hàng' : tk.quantity <= 10 ? 'Sắp hết' : 'Còn hàng' }}
              </span>
            </td>
            <td>
              <div class="flex justify-end items-center gap-2">
                <!-- Inline edit -->
                <ng-container *ngIf="editingId !== tk.itemId; else editMode">
                  <button (click)="batDauSua(tk)"
                          class="px-3 py-1.5 text-xs font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors">
                    ✏️ Sửa
                  </button>
                </ng-container>
                <ng-template #editMode>
                  <input [(ngModel)]="editSoLuong" type="number" min="0"
                         class="form-input w-20 py-1.5 text-sm text-center">
                  <button (click)="luuSoLuong(tk)"
                          class="px-3 py-1.5 text-xs font-medium text-green-700 bg-green-50 hover:bg-green-100 rounded-lg transition-colors">
                    💾
                  </button>
                  <button (click)="editingId=''"
                          class="px-2 py-1.5 text-xs text-gray-500 hover:bg-gray-100 rounded-lg">✕</button>
                </ng-template>
              </div>
            </td>
          </tr>
          <tr *ngIf="filtered.length === 0">
            <td colspan="6" class="text-center text-gray-400 py-10">Không có dữ liệu</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Stats cards -->
  <div class="grid grid-cols-3 gap-4 mt-6">
    <div class="cs-card p-4 text-center">
      <p class="text-2xl font-black text-red-600">{{ hetHang }}</p>
      <p class="text-sm text-gray-500 mt-1">Hết hàng</p>
    </div>
    <div class="cs-card p-4 text-center">
      <p class="text-2xl font-black text-orange-500">{{ sapHet }}</p>
      <p class="text-sm text-gray-500 mt-1">Sắp hết (≤10)</p>
    </div>
    <div class="cs-card p-4 text-center">
      <p class="text-2xl font-black text-green-600">{{ conHang }}</p>
      <p class="text-sm text-gray-500 mt-1">Còn hàng tốt</p>
    </div>
  </div>
</div>
  `,
  styles: []
})
export class AdminInventoryComponent implements OnInit {
  tonKho:    TonKho[] = [];
  filtered:  TonKho[] = [];
  search     = '';
  tinhTrang  = '';
  dangTai    = true;
  editingId  = '';
  editSoLuong = 0;

  get hetHang() { return this.tonKho.filter(t => t.quantity === 0).length; }
  get sapHet()  { return this.tonKho.filter(t => t.quantity > 0 && t.quantity <= 10).length; }
  get conHang() { return this.tonKho.filter(t => t.quantity > 10).length; }

  constructor(private inv: InventoryService, private snack: MatSnackBar) {}

  ngOnInit(): void { this.tai(); }

  tai(): void {
    this.dangTai = true;
    this.inv.layTatCaTonKho().subscribe({
      next: t => { this.tonKho = t; this.loc(); this.dangTai = false; },
      error: () => this.dangTai = false
    });
  }

  loc(): void {
    let r = [...this.tonKho];
    if (this.search)    r = r.filter(t => t.itemId.toLowerCase().includes(this.search.toLowerCase()) || t.location?.toLowerCase().includes(this.search.toLowerCase()));
    if (this.tinhTrang === 'het')     r = r.filter(t => t.quantity === 0);
    if (this.tinhTrang === 'sap-het') r = r.filter(t => t.quantity > 0 && t.quantity <= 10);
    this.filtered = r;
  }

  batDauSua(tk: TonKho): void { this.editingId = tk.itemId; this.editSoLuong = tk.quantity; }

  luuSoLuong(tk: TonKho): void {
    this.inv.capNhatSoLuong(tk.itemId, this.editSoLuong, tk.location).subscribe({
      next: () => {
        tk.quantity = this.editSoLuong;
        this.editingId = '';
        this.snack.open('✅ Cập nhật tồn kho thành công!', 'Đóng', { duration: 2500, panelClass: ['snack-success'] });
        this.loc();
      },
      error: () => this.snack.open('❌ Cập nhật thất bại', 'Đóng', { duration: 3000 })
    });
  }
}
