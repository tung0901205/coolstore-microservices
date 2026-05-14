import { NgModule } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { AdminShellComponent }     from './admin-shell.component';
import { AdminDashboardComponent } from './dashboard/admin-dashboard.component';
import { AdminProductsComponent }  from './products/admin-products.component';
import { AdminInventoryComponent } from './inventory/admin-inventory.component';
import { AdminOrdersComponent }    from './orders/admin-orders.component';
import { AdminUsersComponent }     from './users/admin-users.component';

const routes: Routes = [
  {
    path: '', component: AdminShellComponent,
    children: [
      { path: '',          redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'san-pham',  component: AdminProductsComponent  },
      { path: 'ton-kho',   component: AdminInventoryComponent },
      { path: 'don-hang',  component: AdminOrdersComponent    },
      { path: 'nguoi-dung',component: AdminUsersComponent     },
    ]
  }
];

@NgModule({
  declarations: [
    AdminShellComponent,
    AdminDashboardComponent,
    AdminProductsComponent,
    AdminInventoryComponent,
    AdminOrdersComponent,
    AdminUsersComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes)
  ]
})
export class AdminModule {}
