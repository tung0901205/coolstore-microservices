import { Component, OnInit, HostListener } from '@angular/core';
import { AuthService } from './services/auth.service';
import { CartService } from './services/cart.service';
import { NguoiDung } from './model/models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  nguoiDung: NguoiDung | null = null;
  soLuongGioHang = 0;
  moMenu = false;

  constructor(
    public authService: AuthService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.authService.nguoiDung$.subscribe(u => this.nguoiDung = u);
    this.cartService.cart.subscribe(cart => {
      this.soLuongGioHang = cart.items.reduce((s, i) => s + i.quantity, 0);
    });
  }

  toggleMenu(): void {
    this.moMenu = !this.moMenu;
  }

  /**
   * FIX LỖI 3: Thay thế (clickOutside) bằng @HostListener
   * (clickOutside) không phải directive chuẩn của Angular
   * @HostListener('document:click') là cách Angular chuẩn để bắt click ngoài
   */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    // Chỉ đóng menu nếu click NGOÀI phần tử có class 'user-menu-container'
    if (!target.closest('.user-menu-container')) {
      this.moMenu = false;
    }
  }

  dangXuat(): void {
    this.moMenu = false;
    this.authService.dangXuat();
  }
}
