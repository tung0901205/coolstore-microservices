import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TonKho } from '../model/models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class InventoryService {

  private readonly apiUrl = `${environment.inventoryApiUrl}/inventory`;

  constructor(private http: HttpClient) {}

  layTatCaTonKho(): Observable<TonKho[]> {
    return this.http.get<TonKho[]>(this.apiUrl);
  }

  layTheoItemId(itemId: string): Observable<TonKho> {
    return this.http.get<TonKho>(`${this.apiUrl}/${itemId}`);
  }

  capNhatSoLuong(itemId: string, soLuong: number, location?: string): Observable<TonKho> {
    return this.http.put<TonKho>(`${this.apiUrl}/${itemId}`, { soLuong, location });
  }
}
