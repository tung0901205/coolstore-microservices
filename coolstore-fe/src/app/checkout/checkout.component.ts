import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CartService } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { Cart } from '../model/models';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html'
})
export class CheckoutComponent implements OnInit {

  form!: FormGroup;
  cart: Cart = { items: [] };
  dangGui = false;
  buocHienTai = 1; // 1=thông tin, 2=xác nhận

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cartService.cart.subscribe(c => {
      this.cart = c;
      if (c.items.length === 0) this.router.navigate(['/gio-hang']);
    });

    const user = this.authService.layNguoiDungHienTai();
    this.form = this.fb.group({
      hoTen:           [user?.fullName || '', Validators.required],
      soDienThoai:     ['', [Validators.required, Validators.pattern('^[0-9]{9,11}$')]],
      diaChiGiaoHang:  ['', Validators.required],
      phuongThucThanhToan: ['VNPAY', Validators.required],
      ghiChu:          ['']
    });
  }

  get tongTien(): number { return this.cartService.getTotal(this.cart.items); }

  qua_buoc2(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.buocHienTai = 2;
    window.scrollTo(0, 0);
  }

  quayLai(): void { this.buocHienTai = 1; }

  datHang(): void {
    this.dangGui = true;
    const user = this.authService.layNguoiDungHienTai()!;
    const v    = this.form.value;

    const payload = {
      diaChiGiaoHang:  v.diaChiGiaoHang,
      soDienThoai:     v.soDienThoai,
      phuongThucThanhToan: v.phuongThucThanhToan,
      ghiChu:          v.ghiChu || null,
      cacMatHang: this.cart.items.map(i => ({
        itemId:     i.itemId,
        tenSanPham: i.name,
        hinhAnh:    i.image || i.product,
        donGia:     i.price,
        soLuong:    i.quantity
      }))
    };

    this.orderService.datHang(payload).subscribe({
      next: (res) => {
        if (!res.thanhCong) {
          this.snackBar.open('❌ ' + res.thongBao, 'Đóng', { duration: 4000 });
          this.dangGui = false;
          return;
        }
        const order = res.duLieu;

        if (v.phuongThucThanhToan === 'VNPAY') {
          // Tạo URL VNPay rồi redirect
          const moTa = `Thanh toan don hang ${order.id}`;
          this.orderService.taoUrlThanhToanVnpay(order.id, Math.round(this.tongTien), moTa).subscribe({
            next: (r) => {
              if (r.thanhCong && r.duLieu.paymentUrl) {
                this.cartService.clearCart();
                window.location.href = r.duLieu.paymentUrl; // Redirect sang VNPay
              }
            },
            error: () => {
              this.snackBar.open('Không thể kết nối VNPay. Vui lòng thử lại.', 'Đóng', { duration: 4000 });
              this.dangGui = false;
            }
          });
        } else {
          // COD - xong ngay
          this.cartService.clearCart();
          this.snackBar.open('🎉 Đặt hàng thành công!', 'Đóng', { duration: 3000, panelClass: ['snack-success'] });
          this.router.navigate(['/don-hang', order.id]);
        }
      },
      error: (err) => {
        this.snackBar.open('❌ ' + (err.error?.thongBao || 'Lỗi đặt hàng'), 'Đóng', { duration: 4000 });
        this.dangGui = false;
      }
    });
  }

  anh(img: string): string {
    if (!img) return 'https://via.placeholder.com/60x60?text=SP';
    return img.startsWith('http') ? img : `assets/${img}`;
  }

  loi(field: string, err: string): boolean {
    const c = this.form.get(field);
    return !!(c?.hasError(err) && c?.touched);
  }
}
