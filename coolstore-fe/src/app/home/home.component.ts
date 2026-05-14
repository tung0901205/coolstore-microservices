import { Component, OnInit } from '@angular/core';
import { CatalogService } from '../services/catalog.service';
import { CartService } from '../services/cart.service';
import { Product } from '../model/models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  tatCaSanPham: Product[] = [];
  sanPhamHienThi: Product[] = [];
  danhMuc: string[] = [];
  danhMucDuocChon = '';
  tuKhoanTimKiem = '';
  dangTai = true;
  sapXepTheo = 'mac-dinh';

  // Phân trang
  trangHienTai = 1;
  soItemMoiTrang = 12;

  constructor(
    private catalogService: CatalogService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.taiSanPham();
    this.taiDanhMuc();
  }

  taiSanPham(): void {
    this.dangTai = true;
    this.catalogService.layTatCaSanPham().subscribe({
      next: (products) => {
        this.tatCaSanPham = products;
        this.locVaSapXep();
        this.dangTai = false;
      },
      error: () => {
        this.snackBar.open('Không thể tải danh sách sản phẩm.', 'Đóng', { duration: 3000 });
        this.dangTai = false;
      }
    });
  }

  taiDanhMuc(): void {
    this.catalogService.layDanhMuc().subscribe({
      next: (cats) => this.danhMuc = cats,
      error: () => {}
    });
  }

  locTheoCategory(cat: string): void {
    this.danhMucDuocChon = cat;
    this.trangHienTai = 1;
    this.locVaSapXep();
  }

  timKiem(): void {
    this.trangHienTai = 1;
    this.locVaSapXep();
  }

  locVaSapXep(): void {
    let ket = [...this.tatCaSanPham];

    if (this.danhMucDuocChon) {
      ket = ket.filter(p =>
        p.category?.toLowerCase() === this.danhMucDuocChon.toLowerCase());
    }

    if (this.tuKhoanTimKiem.trim()) {
      const kw = this.tuKhoanTimKiem.toLowerCase();
      ket = ket.filter(p =>
        p.title?.toLowerCase().includes(kw) || p.category?.toLowerCase().includes(kw));
    }

    switch (this.sapXepTheo) {
      case 'gia-tang':  ket.sort((a, b) => a.price - b.price);        break;
      case 'gia-giam':  ket.sort((a, b) => b.price - a.price);        break;
      case 'ten-az':    ket.sort((a, b) => a.title.localeCompare(b.title)); break;
    }

    this.sanPhamHienThi = ket;
  }

  themVaoGio(product: Product): void {
    this.cartService.addToCart({
      itemId:   product.itemId,
      name:     product.title,
      price:    product.price,
      quantity: 1,
      product:  product.image,
      image:    product.image
    });
    this.snackBar.open(`✅ Đã thêm "${product.title}" vào giỏ hàng!`, 'Đóng', {
      duration: 2500,
      panelClass: ['snack-success']
    });
  }

  get sanPhamTrangHienTai(): Product[] {
    const start = (this.trangHienTai - 1) * this.soItemMoiTrang;
    return this.sanPhamHienThi.slice(start, start + this.soItemMoiTrang);
  }

  get tongSoTrang(): number {
    return Math.ceil(this.sanPhamHienThi.length / this.soItemMoiTrang);
  }

  get danhSachTrang(): number[] {
    return Array.from({ length: this.tongSoTrang }, (_, i) => i + 1);
  }

  formatGia(price: number): string {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'USD' }).format(price);
  }

  anhSanPham(image: string): string {
    if (!image) return 'assets/placeholder.png';
    if (image.startsWith('http')) return image;
    return `assets/${image}`;
  }

  resetFilter(): void {
    this.danhMucDuocChon = '';
    this.tuKhoanTimKiem = '';
    this.sapXepTheo = 'mac-dinh';
    this.trangHienTai = 1;
    this.locVaSapXep();
  }
}
