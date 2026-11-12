import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ApiService } from '../../../../core/services/api.service';
import { AlertasPageComponent } from './alertas-page.component';

describe('AlertasPageComponent', () => {
  let api: { get: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    api = {
      get: vi.fn((path: string) => {
        if (path === '/medicamentos/estoque-baixo') {
          return of([{ id: '1', nome: 'Med A', quantidade: 1, estoqueMinimo: 5 }]);
        }
        if (path === '/lotes/vencidos') {
          return of([
            { medicamentoId: 'm1', medicamentoNome: 'A', lote: 'L1', quantidade: 2, validade: '2020-01-01' },
          ]);
        }
        if (path.startsWith('/lotes/a-vencer')) {
          return of([
            { medicamentoId: 'm2', medicamentoNome: 'B', lote: 'L2', quantidade: 3, validade: '2030-01-01' },
          ]);
        }
        return of([]);
      }),
    };
    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule, RouterModule.forRoot([])],
      declarations: [AlertasPageComponent],
      providers: [{ provide: ApiService, useValue: api }],
    });
  });

  it('dispara três consultas no ngOnInit e preenche os resultados', () => {
    const fixture = TestBed.createComponent(AlertasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    expect(api.get).toHaveBeenCalledWith('/medicamentos/estoque-baixo');
    expect(api.get).toHaveBeenCalledWith('/lotes/vencidos');
    expect(api.get).toHaveBeenCalledWith('/lotes/a-vencer?dias=30');
    expect(cmp.estoque.itens.length).toBe(1);
    expect(cmp.vencidos.itens.length).toBe(1);
    expect(cmp.aVencer.itens.length).toBe(1);
    expect(cmp.estoque.carregando).toBe(false);
  });

  it('aplicarPeriodo refaz a consulta com o número informado', () => {
    const fixture = TestBed.createComponent(AlertasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.dias = 15;
    cmp.aplicarPeriodo();
    expect(api.get).toHaveBeenCalledWith('/lotes/a-vencer?dias=15');
    expect(cmp.diasConsultados).toBe(15);
  });

  it('diasValidos rejeita null, zero, negativos e não-inteiros', () => {
    const fixture = TestBed.createComponent(AlertasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.dias = null;
    expect(cmp.diasValidos).toBe(false);
    cmp.dias = 0;
    expect(cmp.diasValidos).toBe(false);
    cmp.dias = -3;
    expect(cmp.diasValidos).toBe(false);
    cmp.dias = 1.2;
    expect(cmp.diasValidos).toBe(false);
    cmp.dias = 1;
    expect(cmp.diasValidos).toBe(true);
  });

  it('aplicarPeriodo não faz nada se dias inválido', () => {
    const fixture = TestBed.createComponent(AlertasPageComponent);
    fixture.detectChanges();
    api.get.mockClear();
    const cmp = fixture.componentInstance;
    cmp.dias = null;
    cmp.aplicarPeriodo();
    expect(api.get).not.toHaveBeenCalled();
  });

  it('erro em uma consulta preenche campo erro sem impactar as demais', () => {
    api.get.mockImplementation((path: string) => {
      if (path === '/lotes/vencidos') return throwError(() => new Error('falhou'));
      return of([]);
    });
    const fixture = TestBed.createComponent(AlertasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    expect(cmp.vencidos.erro).toBe('falhou');
    expect(cmp.vencidos.itens).toEqual([]);
    expect(cmp.estoque.erro).toBe('');
    expect(cmp.aVencer.erro).toBe('');
  });

  it('troca de aba apenas muda a propriedade (sem refazer requisições)', () => {
    const fixture = TestBed.createComponent(AlertasPageComponent);
    fixture.detectChanges();
    api.get.mockClear();

    const cmp = fixture.componentInstance;
    cmp.aba = 'vencidos';
    expect(api.get).not.toHaveBeenCalled();
    cmp.aba = 'a-vencer';
    expect(api.get).not.toHaveBeenCalled();
  });
});
