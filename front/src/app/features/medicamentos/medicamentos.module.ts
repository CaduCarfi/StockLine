import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { MedicamentosRoutingModule } from './medicamentos-routing.module';
import { MedicamentosPageComponent } from './pages/medicamentos-page/medicamentos-page.component';
import { MedicamentoDetalhesComponent } from './pages/medicamento-detalhes/medicamento-detalhes.component';

@NgModule({
  declarations: [MedicamentosPageComponent, MedicamentoDetalhesComponent],
  imports: [CommonModule, FormsModule, SharedModule, MedicamentosRoutingModule]
})
export class MedicamentosModule {}
