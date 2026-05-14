import { Component, OnInit } from '@angular/core';
import { CartService } from '../services/cart.service';
import { Cart, CartItem } from '../model/models';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html'

})
export class CartComponent implements OnInit {

  cart: Cart = { items: [] };

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart.subscribe(c => this.cart = c);
  }

  tangSoLuong(item: CartItem): void {
    this.cartService.addToCart({ ...item, quantity: 1 });
  }

  giamSoLuong(item: CartItem): void {
    this.cartService.removeQuantity(item);
  }

  xoaKhoiGio(item: CartItem): void {
    this.cartService.removeFromCart(item);
  }

  xoaTatCa(): void {
    this.cartService.clearCart();
  }

  tinhTong(): number {
    return this.cartService.getTotal(this.cart.items);
  }

  anhSanPham(image: string): string {
    if (!image) return 'https://via.placeholder.com/80x80?text=SP';
    if (image.startsWith('http')) return image;
    return `assets/${image}`;
  }

  datHang(): void {
    if (!this.authService.daDangNhap()) {
      this.router.navigate(['/dang-nhap'], { queryParams: { returnUrl: '/dat-hang' } });
      return;
    }
    this.router.navigate(['/dat-hang']);
  }
}
