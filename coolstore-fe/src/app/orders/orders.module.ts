// orders.module.ts
import { NgModule } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrdersComponent } from './orders.component';
import { OrderDetailComponent } from './order-detail.component';

@NgModule({
  declarations: [OrdersComponent, OrderDetailComponent],
  imports: [
    CommonModule,
    RouterModule.forChild([
      { path: '', component: OrdersComponent },
      { path: ':id', component: OrderDetailComponent }
    ])
  ]
})
export class OrdersModule {}
