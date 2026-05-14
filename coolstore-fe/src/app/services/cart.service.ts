import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Cart, CartItem } from '../model/models';
import { environment } from '../../environments/environment';

/**
 * CartService - quản lý giỏ hàng phía client.
 * Lưu vào localStorage để không mất khi refresh trang.
 */
@Injectable({ providedIn: 'root' })
export class CartService {

  cart = new BehaviorSubject<Cart>(this._loadFromStorage());

  constructor() {
    // Tự động lưu mỗi khi giỏ hàng thay đổi
    this.cart.subscribe(cart => {
      localStorage.setItem('coolstore_cart', JSON.stringify(cart));
    });
  }

  addToCart(item: CartItem): void {
    const items = [...this.cart.value.items];
    const found = items.find(i => i.itemId === item.itemId);
    if (found) {
      found.quantity += item.quantity ?? 1;
    } else {
      items.push({ ...item, quantity: item.quantity ?? 1 });
    }
    this.cart.next({ items });
  }

  removeFromCart(item: CartItem, update = true): CartItem[] {
    const filtered = this.cart.value.items.filter(i => i.itemId !== item.itemId);
    if (update) this.cart.next({ items: filtered });
    return filtered;
  }

  removeQuantity(item: CartItem): void {
    let toRemove: CartItem | null = null;
    const mapped = this.cart.value.items.map(i => {
      if (i.itemId === item.itemId) {
        i.quantity--;
        if (i.quantity === 0) toRemove = i;
      }
      return i;
    });
    const final = toRemove ? mapped.filter(i => i.itemId !== (toRemove as CartItem).itemId) : mapped;
    this.cart.next({ items: final });
  }

  clearCart(): void {
    this.cart.next({ items: [] });
  }

  getTotal(items: CartItem[]): number {
    return items.reduce((sum, i) => sum + i.price * i.quantity, 0);
  }

  getSoLuong(): number {
    return this.cart.value.items.reduce((s, i) => s + i.quantity, 0);
  }

  private _loadFromStorage(): Cart {
    try {
      const raw = localStorage.getItem('coolstore_cart');
      return raw ? JSON.parse(raw) : { items: [] };
    } catch { return { items: [] }; }
  }
}
