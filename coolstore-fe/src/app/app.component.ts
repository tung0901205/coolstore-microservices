import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';
import { CartService } from './services/cart.service';
import { NguoiDung } from './models/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: []
})
export class AppComponent implements OnInit {

  nguoiDung: NguoiDung | null = null;
  soLuongGioHang = 0;
  moMenu = false;

  constructor(
    public authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe trạng thái đăng nhập
    this.authService.nguoiDung$.subscribe(u => this.nguoiDung = u);

    // Subscribe số lượng giỏ hàng
    this.cartService.cart.subscribe(cart => {
      this.soLuongGioHang = cart.items.reduce((sum, i) => sum + i.quantity, 0);
    });
  }

  dangXuat(): void {
    this.authService.dangXuat();
  }
}
