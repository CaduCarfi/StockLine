import { TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from './sidebar.component';

describe('SidebarComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      declarations: [SidebarComponent],
    });
  });

  it('renderiza um item para cada rota do menu', () => {
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();
    const links = (fixture.nativeElement as HTMLElement).querySelectorAll('.sidebar__item');
    expect(links.length).toBe(fixture.componentInstance.itens.length);
  });

  it('expõe as rotas principais da aplicação', () => {
    const fixture = TestBed.createComponent(SidebarComponent);
    const rotas = fixture.componentInstance.itens.map(i => i.rota);
    expect(rotas).toEqual([
      '/medicamentos',
      '/categorias',
      '/movimentacoes',
      '/alertas',
      '/historico',
    ]);
  });
});
