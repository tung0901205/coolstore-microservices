import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CatalogService } from '../services/catalog.service';
import { Product } from '../model/models';

@Component({
  selector: 'app-admin-products',
  templateUrl: './admin-products.component.html',
  styles: []
})
export class AdminProductsComponent implements OnInit {

  products: Product[] = [];
  filtered: Product[] = [];
  search    = '';
  catFilter = '';
  categories: string[] = [];
  dangTai   = true;

  // Modal
  hienModal  = false;
  dangSua    = false;
  suaItemId  = '';
  form!: FormGroup;
  dangLuu    = false;

  // Confirm delete
  hienXoa   = false;
  xoaItemId = '';
  xoaTen    = '';

  constructor(
    private catalog: CatalogService,
    private fb:      FormBuilder,
    private snack:   MatSnackBar
  ) {}

  ngOnInit(): void {
    this.taiDuLieu();
    this.khoiTaoForm();
  }

  khoiTaoForm(p?: Product): void {
    this.form = this.fb.group({
      itemId:   [p?.itemId   || '', []],
      title:    [p?.title    || '', Validators.required],
      desc:     [p?.desc     || '', Validators.required],
      price:    [p?.price    || 0,  [Validators.required, Validators.min(0.01)]],
      category: [p?.category || '', Validators.required],
      image:    [p?.image    || '']
    });
  }

  taiDuLieu(): void {
    this.dangTai = true;
    this.catalog.layTatCaSanPham().subscribe({
      next: (ps) => {
        this.products = ps;
        this.loc();
        this.dangTai = false;
      },
      error: () => this.dangTai = false
    });
    this.catalog.layDanhMuc().subscribe(c => this.categories = c);
  }

  loc(): void {
    let r = [...this.products];
    if (this.search)    r = r.filter(p => p.title.toLowerCase().includes(this.search.toLowerCase()));
    if (this.catFilter) r = r.filter(p => p.category === this.catFilter);
    this.filtered = r;
  }

  // ── Modal thêm mới ───────────────────────────────────────────

  moModalThem(): void {
    this.dangSua = false;
    this.suaItemId = '';
    this.khoiTaoForm();
    this.hienModal = true;
  }

  moModalSua(p: Product): void {
    this.dangSua   = true;
    this.suaItemId = p.itemId;
    this.khoiTaoForm(p);
    this.hienModal = true;
  }

  dongModal(): void { this.hienModal = false; }

  luu(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.dangLuu = true;
    const v = this.form.value;

    const obs = this.dangSua
      ? this.catalog.suaSanPham(this.suaItemId, v)
      : this.catalog.themSanPham(v);

    obs.subscribe({
      next: () => {
        this.snack.open(
          this.dangSua ? '✅ Cập nhật sản phẩm thành công!' : '✅ Thêm sản phẩm thành công!',
          'Đóng', { duration: 3000, panelClass: ['snack-success'] }
        );
        this.hienModal = false;
        this.dangLuu   = false;
        this.taiDuLieu();
      },
      error: (e) => {
        this.snack.open('❌ ' + (e.error?.thongBao || 'Lỗi lưu sản phẩm'), 'Đóng', { duration: 4000 });
        this.dangLuu = false;
      }
    });
  }

  // ── Xóa ─────────────────────────────────────────────────────

  moXac(p: Product): void { this.xoaItemId = p.itemId; this.xoaTen = p.title; this.hienXoa = true; }
  dongXac(): void { this.hienXoa = false; }

  xoa(): void {
    this.catalog.xoaSanPham(this.xoaItemId).subscribe({
      next: () => {
        this.snack.open('🗑️ Đã xóa sản phẩm!', 'Đóng', { duration: 3000 });
        this.hienXoa = false;
        this.taiDuLieu();
      },
      error: () => this.snack.open('❌ Không thể xóa', 'Đóng', { duration: 3000 })
    });
  }

  anh(img: string): string {
    if (!img) return 'https://via.placeholder.com/48x48?text=SP';
    return img.startsWith('http') ? img : `assets/${img}`;
  }

  loi(f: string, e: string): boolean {
    const c = this.form.get(f);
    return !!(c?.hasError(e) && c?.touched);
  }
}
