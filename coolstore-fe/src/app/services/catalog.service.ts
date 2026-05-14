import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product, KetQua } from '../model/models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CatalogService {

  private readonly apiUrl = `${environment.catalogApiUrl}/products`;

  constructor(private http: HttpClient) {}

  layTatCaSanPham(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  layTheoCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}?category=${encodeURIComponent(category)}`);
  }

  timKiem(keyword: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}?search=${encodeURIComponent(keyword)}`);
  }

  layChiTiet(itemId: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${itemId}`);
  }

  layDanhMuc(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }

  // ── Admin ─────────────────────────────────────────────────

  themSanPham(product: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  suaSanPham(itemId: string, product: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${itemId}`, product);
  }

  xoaSanPham(itemId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${itemId}`);
  }
}
