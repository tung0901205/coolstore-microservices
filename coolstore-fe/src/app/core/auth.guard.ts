import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

/** Guard bảo vệ route cần đăng nhập */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.daDangNhap()) {
      this.snackBar.open('Vui lòng đăng nhập để tiếp tục.', 'Đóng', { duration: 3000 });
      this.router.navigate(['/dang-nhap']);
      return false;
    }

    // Kiểm tra quyền Admin
    const requiresAdmin = route.data?.['requiresAdmin'];
    if (requiresAdmin && !this.authService.laAdmin()) {
      this.snackBar.open('Bạn không có quyền truy cập trang này.', 'Đóng', { duration: 3000 });
      this.router.navigate(['/']);
      return false;
    }

    return true;
  }
}
