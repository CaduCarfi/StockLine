import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { MedicamentosRoutingModule } from './medicamentos-routing.module';
import { MedicamentosPageComponent } from './pages/medicamentos-page/medicamentos-page.component';

@NgModule({
  declarations: [MedicamentosPageComponent],
  imports: [CommonModule, SharedModule, MedicamentosRoutingModule]
})
export class MedicamentosModule {}
