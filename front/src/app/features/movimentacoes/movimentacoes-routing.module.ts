import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MovimentacoesPageComponent } from './pages/movimentacoes-page/movimentacoes-page.component';

const routes: Routes = [
  { path: '', component: MovimentacoesPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MovimentacoesRoutingModule {}
