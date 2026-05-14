import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/auth.guard';

const routes: Routes = [
  // ── Trang công khai ──────────────────────────────────────────
  {
    path: '',
    loadChildren: () => import('./home/home.module').then(m => m.HomeModule)
  },
  {
    path: 'dang-nhap',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'gio-hang',
    loadChildren: () => import('./cart/cart.module').then(m => m.CartModule)
  },
  {
    path: 'dat-hang',
    canActivate: [AuthGuard],
    loadChildren: () => import('./checkout/checkout.module').then(m => m.CheckoutModule)
  },
  {
    path: 'don-hang',
    canActivate: [AuthGuard],
    loadChildren: () => import('./orders/orders.module').then(m => m.OrdersModule)
  },
  {
    path: 'thanh-toan',
    loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule)
  },

  // ── Admin (yêu cầu role ADMIN) ────────────────────────────────
  // {
  //   path: 'admin',
  //   canActivate: [AuthGuard],
  //   data: { requiresAdmin: true },
  //   loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
  // },

  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
