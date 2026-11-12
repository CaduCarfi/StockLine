import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MedicamentosPageComponent } from './pages/medicamentos-page/medicamentos-page.component';
import { MedicamentoDetalhesComponent } from './pages/medicamento-detalhes/medicamento-detalhes.component';

const routes: Routes = [
  { path: '', component: MedicamentosPageComponent },
  { path: ':id', component: MedicamentoDetalhesComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MedicamentosRoutingModule {}
