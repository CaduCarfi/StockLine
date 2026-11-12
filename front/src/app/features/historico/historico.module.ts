import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { HistoricoRoutingModule } from './historico-routing.module';
import { HistoricoPageComponent } from './pages/historico-page/historico-page.component';

@NgModule({
  declarations: [HistoricoPageComponent],
  imports: [CommonModule, FormsModule, SharedModule, HistoricoRoutingModule]
})
export class HistoricoModule {}
