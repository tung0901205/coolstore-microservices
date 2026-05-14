import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { DangNhapResponse, KetQua, NguoiDung } from '../model/models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly apiUrl = environment.authApiUrl;

  // BehaviorSubject để các component subscribe trạng thái đăng nhập
  private nguoiDungSubject = new BehaviorSubject<NguoiDung | null>(this.layNguoiDungTuStorage());
  public nguoiDung$ = this.nguoiDungSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  // ── Đăng ký / Đăng nhập ─────────────────────────────────────

  dangKy(username: string, email: string, fullName: string, password: string): Observable<KetQua<NguoiDung>> {
    return this.http.post<KetQua<NguoiDung>>(`${this.apiUrl}/auth/dang-ky`,
      { username, email, fullName, password });
  }

  dangNhap(username: string, password: string): Observable<KetQua<DangNhapResponse>> {

    console.log("DỮ LIỆU THỰC TẾ GỬI LÊN BACKEND:", { username: username, password: password });

    return this.http.post<KetQua<DangNhapResponse>>(`${this.apiUrl}/auth/dang-nhap`,
      { username, password })
      .pipe(
        tap(res => {
          if (res.thanhCong && res.duLieu) {
            // Lưu token và thông tin user vào localStorage
            localStorage.setItem(environment.TOKEN_KEY, res.duLieu.token);
            localStorage.setItem(environment.USER_KEY, JSON.stringify(res.duLieu.nguoiDung));
            this.nguoiDungSubject.next(res.duLieu.nguoiDung);
          }
        })
      );
  }

  dangXuat(): void {
    localStorage.removeItem(environment.TOKEN_KEY);
    localStorage.removeItem(environment.USER_KEY);
    this.nguoiDungSubject.next(null);
    this.router.navigate(['/']);
  }

  // ── Getters ──────────────────────────────────────────────────

  layToken(): string | null {
    return localStorage.getItem(environment.TOKEN_KEY);
  }

  daDangNhap(): boolean {
    return this.layToken() !== null && this.layNguoiDungHienTai() !== null;
  }

  laAdmin(): boolean {
    return this.layNguoiDungHienTai()?.role === 'ADMIN';
  }

  layNguoiDungHienTai(): NguoiDung | null {
    return this.nguoiDungSubject.value;
  }

  // ── Admin APIs ───────────────────────────────────────────────

  layDanhSachNguoiDung(): Observable<KetQua<NguoiDung[]>> {
    return this.http.get<KetQua<NguoiDung[]>>(`${this.apiUrl}/auth/nguoi-dung`);
  }

  capNhatTrangThai(id: number, active: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/auth/nguoi-dung/${id}/trang-thai?active=${active}`, {});
  }

  capNhatQuyen(id: number, role: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/auth/nguoi-dung/${id}/quyen?role=${role}`, {});
  }

  // ── Private helpers ──────────────────────────────────────────

  private layNguoiDungTuStorage(): NguoiDung | null {
    try {
      const raw = localStorage.getItem(environment.USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
