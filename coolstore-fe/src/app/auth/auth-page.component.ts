import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-auth-page',
  templateUrl: './auth-page.component.html'
})
export class AuthPageComponent implements OnInit {

  cheDoHienTai: 'dang-nhap' | 'dang-ky' = 'dang-nhap';
  dangGui = false;
  hienMatKhau = false;

  formDangNhap!: FormGroup;
  formDangKy!:   FormGroup;

  private returnUrl = '/';

  constructor(
    private fb:          FormBuilder,
    private authService: AuthService,
    private router:      Router,
    private route:       ActivatedRoute,
    private snackBar:    MatSnackBar
  ) {}

  ngOnInit(): void {
    // Nếu đã đăng nhập rồi → về trang chủ
    if (this.authService.daDangNhap()) {
      this.router.navigate(['/']);
      return;
    }

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    // Form đăng nhập
    this.formDangNhap = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Form đăng ký
    this.formDangKy = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50),
                      Validators.pattern('^[a-zA-Z0-9_]+$')]],
      email:    ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      xacNhanMatKhau: ['', Validators.required]
    }, { validators: this.kiemTraMatKhauKhop });
  }

  // ── Validators ───────────────────────────────────────────────

  kiemTraMatKhauKhop(group: AbstractControl) {
    const pw  = group.get('password')?.value;
    const cpw = group.get('xacNhanMatKhau')?.value;
    return pw === cpw ? null : { matKhauKhongKhop: true };
  }

  // ── Submit ───────────────────────────────────────────────────

  guiDangNhap(): void {
    if (this.formDangNhap.invalid) {
      this.formDangNhap.markAllAsTouched();
      return;
    }
    this.dangGui = true;
    const { username, password } = this.formDangNhap.value;

    this.authService.dangNhap(username, password).subscribe({
      next: (res) => {
        this.dangGui = false;
        if (res.thanhCong) {
          this.snackBar.open(
            `🎉 Chào mừng ${res.duLieu.nguoiDung.fullName}!`, 'Đóng',
            { duration: 3000, panelClass: ['snack-success'] }
          );
          this.router.navigate([this.returnUrl]);
        }
      },
      error: (err) => {
        this.dangGui = false;
        const msg = err.error?.thongBao || 'Đăng nhập thất bại. Vui lòng thử lại.';
        this.snackBar.open(`❌ ${msg}`, 'Đóng', { duration: 4000, panelClass: ['snack-error'] });
      }
    });
  }

  guiDangKy(): void {
    if (this.formDangKy.invalid) {
      this.formDangKy.markAllAsTouched();
      return;
    }
    this.dangGui = true;
    const { username, email, fullName, password } = this.formDangKy.value;

    this.authService.dangKy(username, email, fullName, password).subscribe({
      next: (res) => {
        this.dangGui = false;
        if (res.thanhCong) {
          this.snackBar.open('✅ Đăng ký thành công! Vui lòng đăng nhập.', 'Đóng', {
            duration: 4000, panelClass: ['snack-success']
          });
          this.cheDoHienTai = 'dang-nhap';
          this.formDangNhap.patchValue({ username, password: '' });
        }
      },
      error: (err) => {
        this.dangGui = false;
        const msg = err.error?.thongBao || 'Đăng ký thất bại. Vui lòng thử lại.';
        this.snackBar.open(`❌ ${msg}`, 'Đóng', { duration: 4000, panelClass: ['snack-error'] });
      }
    });
  }

  // ── Getters ──────────────────────────────────────────────────

  get f() { return this.formDangNhap.controls; }
  get r() { return this.formDangKy.controls; }

  hasError(form: FormGroup, field: string, error: string): boolean {
    const ctrl = form.get(field);
    return !!(ctrl?.hasError(error) && ctrl?.touched);
  }
}
