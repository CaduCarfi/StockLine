import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'medicamentos', pathMatch: 'full' },
  {
    path: 'medicamentos',
    loadChildren: () =>
      import('./features/medicamentos/medicamentos.module').then(m => m.MedicamentosModule)
  },
  {
    path: 'categorias',
    loadChildren: () =>
      import('./features/categorias/categorias.module').then(m => m.CategoriasModule)
  },
  {
    path: 'alertas',
    loadChildren: () =>
      import('./features/alertas/alertas.module').then(m => m.AlertasModule)
  },
  {
    path: 'historico',
    loadChildren: () =>
      import('./features/historico/historico.module').then(m => m.HistoricoModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
