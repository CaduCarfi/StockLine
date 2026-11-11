import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { AlertasRoutingModule } from './alertas-routing.module';
import { AlertasPageComponent } from './pages/alertas-page/alertas-page.component';

@NgModule({
  declarations: [AlertasPageComponent],
  imports: [CommonModule, SharedModule, AlertasRoutingModule]
})
export class AlertasModule {}
