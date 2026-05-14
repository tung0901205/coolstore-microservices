// payment.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PaymentResultComponent } from './payment-result.component';

@NgModule({
  declarations: [PaymentResultComponent],
  imports: [
    CommonModule,
    RouterModule.forChild([{ path: 'ket-qua', component: PaymentResultComponent }])
  ]
})
export class PaymentModule {}
