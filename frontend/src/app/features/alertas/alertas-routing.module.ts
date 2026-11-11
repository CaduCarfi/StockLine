import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlertasPageComponent } from './pages/alertas-page/alertas-page.component';

const routes: Routes = [
  { path: '', component: AlertasPageComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AlertasRoutingModule {}
