import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { MovimentacoesRoutingModule } from './movimentacoes-routing.module';
import { MovimentacoesPageComponent } from './pages/movimentacoes-page/movimentacoes-page.component';

@NgModule({
  declarations: [MovimentacoesPageComponent],
  imports: [CommonModule, FormsModule, SharedModule, MovimentacoesRoutingModule]
})
export class MovimentacoesModule {}
