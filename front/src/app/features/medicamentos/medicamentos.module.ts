import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { MedicamentosRoutingModule } from './medicamentos-routing.module';
import { MedicamentosPageComponent } from './pages/medicamentos-page/medicamentos-page.component';

@NgModule({
  declarations: [MedicamentosPageComponent],
  imports: [CommonModule, FormsModule, SharedModule, MedicamentosRoutingModule]
})
export class MedicamentosModule {}
