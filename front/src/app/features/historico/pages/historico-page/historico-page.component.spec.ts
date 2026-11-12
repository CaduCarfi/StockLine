import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule, convertToParamMap, ParamMap } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { MedicamentoService, Movimentacao } from '../../../medicamentos/services/medicamento.service';
import { HistoricoPageComponent } from './historico-page.component';

const medicamentos: MedicamentoResponseDTO[] = [
  { id: 'b', nome: 'Beta', descricao: '', quantidade: 1, validade: '2030-01-01', lote: 'L', categoriaId: '', estoqueMinimo: null, lotes: [] },
  { id: 'a', nome: 'Alfa', descricao: '', quantidade: 2, validade: '2030-01-01', lote: 'L', categoriaId: '', estoqueMinimo: null, lotes: [] },
];

const historico: Movimentacao[] = [
  { tipo: 'ENTRADA', lote: 'L1', quantidade: 5, data: '2030-01-01T00:00:00Z' },
  { tipo: 'BAIXA', lote: 'L1', quantidade: 2, data: '2030-02-01T00:00:00Z' },
];

describe('HistoricoPageComponent', () => {
  let medicamentoService: {
    listar: ReturnType<typeof vi.fn>;
    consultarHistorico: ReturnType<typeof vi.fn>;
  };
  let router: { navigate: ReturnType<typeof vi.fn> };
  let queryParamMap$: BehaviorSubject<ParamMap>;
  let activatedRoute: Partial<ActivatedRoute>;

  beforeEach(() => {
    medicamentoService = {
      listar: vi.fn().mockReturnValue(of(medicamentos)),
      consultarHistorico: vi.fn().mockReturnValue(of(historico)),
    };
    router = { navigate: vi.fn().mockResolvedValue(true) };
    queryParamMap$ = new BehaviorSubject<ParamMap>(convertToParamMap({}));
    activatedRoute = { queryParamMap: queryParamMap$.asObservable() };

    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule, RouterModule.forRoot([])],
      declarations: [HistoricoPageComponent],
      providers: [
        { provide: MedicamentoService, useValue: medicamentoService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: Router, useValue: router },
      ],
    });
  });

  it('lista medicamentos ordenados alfabeticamente', () => {
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();
    const nomes = fixture.componentInstance.medicamentos.map(m => m.nome);
    expect(nomes).toEqual(['Alfa', 'Beta']);
    expect(fixture.componentInstance.carregandoMedicamentos).toBe(false);
  });

  it('propaga erro ao listar medicamentos', () => {
    medicamentoService.listar.mockReturnValue(throwError(() => new Error('bum')));
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.erroMedicamentos).toBe('bum');
    expect(fixture.componentInstance.carregandoMedicamentos).toBe(false);
  });

  it('sem queryParam medicamento, não chama consultarHistorico e lista vazia', () => {
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();
    expect(medicamentoService.consultarHistorico).not.toHaveBeenCalled();
    expect(fixture.componentInstance.movimentacoes).toEqual([]);
  });

  it('com queryParam medicamento, carrega histórico e ordena descendente por data', () => {
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();

    queryParamMap$.next(convertToParamMap({ medicamento: 'a' }));
    fixture.detectChanges();

    const cmp = fixture.componentInstance;
    expect(medicamentoService.consultarHistorico).toHaveBeenCalledWith(encodeURIComponent('a'));
    expect(cmp.movimentacoes.map(m => m.tipo)).toEqual(['BAIXA', 'ENTRADA']);
    expect(cmp.carregandoHistorico).toBe(false);
  });

  it('erro no histórico preenche erroHistorico e esvazia lista', () => {
    medicamentoService.consultarHistorico.mockReturnValue(throwError(() => new Error('oops')));
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();

    queryParamMap$.next(convertToParamMap({ medicamento: 'a' }));
    fixture.detectChanges();

    expect(fixture.componentInstance.erroHistorico).toBe('oops');
    expect(fixture.componentInstance.movimentacoes).toEqual([]);
  });

  it('filtradas aplica filtro de tipo quando selecionado', () => {
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();
    queryParamMap$.next(convertToParamMap({ medicamento: 'a' }));
    fixture.detectChanges();

    const cmp = fixture.componentInstance;
    cmp.tipo = 'ENTRADA';
    expect(cmp.filtradas.map(m => m.tipo)).toEqual(['ENTRADA']);
    cmp.tipo = '';
    expect(cmp.filtradas.length).toBe(2);
  });

  it('selecionarMedicamento navega com o novo queryParam', () => {
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();
    fixture.componentInstance.selecionarMedicamento('x');
    expect(router.navigate).toHaveBeenCalledWith(
      [], { relativeTo: activatedRoute, queryParams: { medicamento: 'x' } }
    );
  });

  it('selecionarMedicamento com id vazio passa null para limpar o queryParam', () => {
    const fixture = TestBed.createComponent(HistoricoPageComponent);
    fixture.detectChanges();
    fixture.componentInstance.selecionarMedicamento('');
    expect(router.navigate).toHaveBeenCalledWith(
      [], { relativeTo: activatedRoute, queryParams: { medicamento: null } }
    );
  });
});
