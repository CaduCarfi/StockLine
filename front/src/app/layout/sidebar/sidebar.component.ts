import { Component } from '@angular/core';

interface ItemMenu {
  label: string;
  rota: string;
  icone: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  itens: ItemMenu[] = [
    { label: 'Medicamentos', rota: '/medicamentos', icone: 'M' },
    { label: 'Categorias',   rota: '/categorias',   icone: 'C' },
    { label: 'Alertas',      rota: '/alertas',      icone: 'A' },
    { label: 'Histórico',    rota: '/historico',    icone: 'H' }
  ];
}
