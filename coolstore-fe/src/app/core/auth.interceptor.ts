import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Interceptor tự động gắn JWT token vào header của mọi HTTP request.
 * Nếu nhận 401 → logout và về trang đăng nhập.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.layToken();

    // Gắn token vào header nếu đã đăng nhập
    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token hết hạn hoặc không hợp lệ → đăng xuất
          this.authService.dangXuat();
          this.snackBar.open('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.', 'Đóng', {
            duration: 4000,
            panelClass: ['snack-error']
          });
          this.router.navigate(['/dang-nhap']);
        }
        return throwError(() => error);
      })
    );
  }
}
