// ============================================================
// AUTH MODULE
// ============================================================
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthPageComponent } from './auth-page.component';

@NgModule({
  declarations: [AuthPageComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    RouterModule.forChild([{ path: '', component: AuthPageComponent }])
  ]
})
export class AuthModule {}
