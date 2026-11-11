import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { CategoriasRoutingModule } from './categorias-routing.module';
import { CategoriasPageComponent } from './pages/categorias-page/categorias-page.component';

@NgModule({
  declarations: [CategoriasPageComponent],
  imports: [CommonModule, FormsModule, SharedModule, CategoriasRoutingModule]
})
export class CategoriasModule {}
