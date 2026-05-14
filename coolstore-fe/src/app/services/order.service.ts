import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DonHang, KetQua } from '../model/models';
import { CartItem } from '../model/models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OrderService {

  private readonly orderUrl  = `${environment.orderApiUrl}/orders`;
  private readonly payUrl    = `${environment.paymentApiUrl}/payment`;

  constructor(private http: HttpClient) {}

  // ── Tạo đơn hàng ────────────────────────────────────────────

  datHang(payload: {
    diaChiGiaoHang: string;
    soDienThoai: string;
    cacMatHang: { itemId: string; tenSanPham: string; hinhAnh: string; donGia: number; soLuong: number }[];
    phuongThucThanhToan: string;
    ghiChu?: string;
  }): Observable<KetQua<DonHang>> {
    return this.http.post<KetQua<DonHang>>(this.orderUrl, payload);
  }

  // ── Lấy danh sách đơn hàng ──────────────────────────────────

  layDonHangCuaToi(): Observable<KetQua<DonHang[]>> {
    return this.http.get<KetQua<DonHang[]>>(`${this.orderUrl}/cua-toi`);
  }

  layChiTietDonHang(id: string): Observable<KetQua<DonHang>> {
    return this.http.get<KetQua<DonHang>>(`${this.orderUrl}/${id}`);
  }

  layTatCaDonHang(): Observable<KetQua<DonHang[]>> {
    return this.http.get<KetQua<DonHang[]>>(this.orderUrl);
  }

  capNhatTrangThai(id: string, trangThai: string): Observable<any> {
    return this.http.put(`${this.orderUrl}/${id}/trang-thai?trangThai=${trangThai}`, {});
  }

  // ── Thanh toán VNPay ─────────────────────────────────────────

  taoUrlThanhToanVnpay(orderId: string, soTienVnd: number, moTa: string): Observable<KetQua<{ paymentUrl: string }>> {
    return this.http.post<KetQua<{ paymentUrl: string }>>(
      `${this.payUrl}/tao-url-thanh-toan`,
      { orderId, soTienVnd, moTaDonHang: moTa }
    );
  }
}
