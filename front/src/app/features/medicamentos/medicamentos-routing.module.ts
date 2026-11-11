import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MedicamentosPageComponent } from './pages/medicamentos-page/medicamentos-page.component';

const routes: Routes = [
  { path: '', component: MedicamentosPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MedicamentosRoutingModule {}
