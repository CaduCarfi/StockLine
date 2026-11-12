import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { CategoriaService } from '../../../categorias/services/categoria.service';
import { MedicamentoService } from '../../services/medicamento.service';
import { MedicamentoDetalhesComponent } from './medicamento-detalhes.component';

const medicamento: MedicamentoResponseDTO = {
  id: '1', nome: 'Paracetamol', descricao: 'd', quantidade: 10,
  validade: '2030-01-01', lote: 'L', categoriaId: 'C1',
  estoqueMinimo: 5,
  lotes: [
    { codigo: 'L1', saldo: 5, validade: '2030-01-01' },
    { codigo: 'L2', saldo: 0, validade: '2030-02-01' },
  ],
};

describe('MedicamentoDetalhesComponent', () => {
  let medicamentoService: {
    buscarPorId: ReturnType<typeof vi.fn>;
    registrarEntrada: ReturnType<typeof vi.fn>;
    registrarBaixa: ReturnType<typeof vi.fn>;
    consultarHistorico: ReturnType<typeof vi.fn>;
    ajustarQuantidade: ReturnType<typeof vi.fn>;
  };
  let categoriaService: { listar: ReturnType<typeof vi.fn> };
  let alertaService: { sucesso: ReturnType<typeof vi.fn>; erro: ReturnType<typeof vi.fn> };
  let activatedRoute: { snapshot: { paramMap: { get: (k: string) => string | null } } };

  beforeEach(() => {
    medicamentoService = {
      buscarPorId: vi.fn().mockReturnValue(of(medicamento)),
      registrarEntrada: vi.fn(),
      registrarBaixa: vi.fn(),
      consultarHistorico: vi.fn().mockReturnValue(of([])),
      ajustarQuantidade: vi.fn(),
    };
    categoriaService = {
      listar: vi.fn().mockReturnValue(of([{ id: 'C1', nome: 'Dor' }])),
    };
    alertaService = { sucesso: vi.fn(), erro: vi.fn() };
    activatedRoute = { snapshot: { paramMap: { get: () => '1' } } };

    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule, RouterModule.forRoot([])],
      declarations: [MedicamentoDetalhesComponent],
      providers: [
        { provide: MedicamentoService, useValue: medicamentoService },
        { provide: CategoriaService, useValue: categoriaService },
        { provide: AlertaService, useValue: alertaService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    });
  });

  it('carrega medicamento e nome da categoria no ngOnInit', () => {
    const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    expect(cmp.medicamento?.id).toBe('1');
    expect(cmp.carregando).toBe(false);
    expect(cmp.categoria).toBe('Dor');
  });

  it('erro na busca do medicamento preenche campo erro', () => {
    medicamentoService.buscarPorId.mockReturnValue(throwError(() => new Error('nao foi')));
    const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.erro).toBe('nao foi');
    expect(fixture.componentInstance.carregando).toBe(false);
  });

  it('erro ao listar categorias deixa nome da categoria vazio sem quebrar', () => {
    categoriaService.listar.mockReturnValue(throwError(() => new Error('falhou')));
    const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.medicamento?.id).toBe('1');
    expect(fixture.componentInstance.categoria).toBe('');
  });

  describe('validações', () => {
    let cmp: MedicamentoDetalhesComponent;
    beforeEach(() => {
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      cmp = fixture.componentInstance;
    });

    it('movimentacaoValida exige lote, quantidade > 0 inteira; entrada exige validade', () => {
      cmp.secao = 'baixa';
      cmp.lote = 'L1';
      cmp.quantidade = 1;
      expect(cmp.movimentacaoValida).toBe(true);

      cmp.secao = 'entrada';
      expect(cmp.movimentacaoValida).toBe(false);
      cmp.validade = '2030-01-01';
      expect(cmp.movimentacaoValida).toBe(true);

      cmp.quantidade = 0;
      expect(cmp.movimentacaoValida).toBe(false);
      cmp.quantidade = 1.5;
      expect(cmp.movimentacaoValida).toBe(false);
      cmp.quantidade = 2;
      cmp.lote = '   ';
      expect(cmp.movimentacaoValida).toBe(false);
    });

    it('ajusteValido respeita limites [-10, 10] excluindo 0 e não-inteiros', () => {
      cmp.delta = null;
      expect(cmp.ajusteValido).toBe(false);
      cmp.delta = 0;
      expect(cmp.ajusteValido).toBe(false);
      cmp.delta = 11;
      expect(cmp.ajusteValido).toBe(false);
      cmp.delta = -11;
      expect(cmp.ajusteValido).toBe(false);
      cmp.delta = 1.5;
      expect(cmp.ajusteValido).toBe(false);
      cmp.delta = 10;
      expect(cmp.ajusteValido).toBe(true);
      cmp.delta = -10;
      expect(cmp.ajusteValido).toBe(true);
    });

    it('incrementar/decrementar fazem clamp em ±10 e começam em 0 quando null', () => {
      cmp.delta = null;
      cmp.incrementarDelta();
      expect(cmp.delta).toBe(1);

      cmp.delta = 10;
      cmp.incrementarDelta();
      expect(cmp.delta).toBe(10);

      cmp.delta = null;
      cmp.decrementarDelta();
      expect(cmp.delta).toBe(-1);

      cmp.delta = -10;
      cmp.decrementarDelta();
      expect(cmp.delta).toBe(-10);
    });
  });

  describe('abrir seções', () => {
    it('abrir("entrada") zera campos e não dispara histórico', () => {
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.lote = 'x'; cmp.validade = 'x'; cmp.quantidade = 5; cmp.delta = 2;

      cmp.abrir('entrada');

      expect(cmp.secao).toBe('entrada');
      expect(cmp.lote).toBe('');
      expect(cmp.validade).toBe('');
      expect(cmp.quantidade).toBeNull();
      expect(cmp.delta).toBeNull();
      expect(medicamentoService.consultarHistorico).not.toHaveBeenCalled();
    });

    it('abrir("historico") busca histórico com sucesso', () => {
      medicamentoService.consultarHistorico.mockReturnValue(of([
        { tipo: 'ENTRADA', lote: 'L1', quantidade: 5, data: '2030-01-01T10:00:00Z' },
      ]));

      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('historico');

      expect(medicamentoService.consultarHistorico).toHaveBeenCalledWith('1');
      expect(cmp.carregandoHistorico).toBe(false);
      expect(cmp.historico.length).toBe(1);
    });

    it('abrir("historico") preenche erroHistorico em caso de falha', () => {
      medicamentoService.consultarHistorico.mockReturnValue(throwError(() => new Error('fail')));
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('historico');
      expect(cmp.erroHistorico).toBe('fail');
      expect(cmp.carregandoHistorico).toBe(false);
    });
  });

  describe('registrar entradas/baixas', () => {
    it('registrar entrada chama service com validade e exibe sucesso', () => {
      const atualizado = { ...medicamento, quantidade: 15 };
      medicamentoService.registrarEntrada.mockReturnValue(of(atualizado));

      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('entrada');
      cmp.lote = 'LX';
      cmp.validade = '2030-05-01';
      cmp.quantidade = 5;
      cmp.registrar();

      expect(medicamentoService.registrarEntrada).toHaveBeenCalledWith('1', {
        lote: 'LX', quantidade: 5, validade: '2030-05-01',
      });
      expect(cmp.medicamento?.quantidade).toBe(15);
      expect(cmp.secao).toBeNull();
      expect(alertaService.sucesso).toHaveBeenCalledWith('Movimentação registrada com sucesso.');
      expect(cmp.salvando).toBe(false);
    });

    it('registrar baixa chama service sem validade', () => {
      const atualizado = { ...medicamento, quantidade: 7 };
      medicamentoService.registrarBaixa.mockReturnValue(of(atualizado));

      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('baixa');
      cmp.lote = 'L1';
      cmp.quantidade = 3;
      cmp.registrar();

      expect(medicamentoService.registrarBaixa).toHaveBeenCalledWith('1', {
        lote: 'L1', quantidade: 3,
      });
      expect(cmp.medicamento?.quantidade).toBe(7);
    });

    it('registrar ignora chamada quando movimentação é inválida', () => {
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('baixa');
      cmp.registrar();
      expect(medicamentoService.registrarBaixa).not.toHaveBeenCalled();
    });

    it('erro no registrar aciona alerta de erro e libera salvando', () => {
      medicamentoService.registrarBaixa.mockReturnValue(throwError(() => new Error('nope')));
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('baixa');
      cmp.lote = 'L1';
      cmp.quantidade = 2;
      cmp.registrar();
      expect(alertaService.erro).toHaveBeenCalledWith('nope');
      expect(cmp.salvando).toBe(false);
    });
  });

  describe('ajustar estoque', () => {
    it('ajustar chama service e aplica atualização', () => {
      const atualizado = { ...medicamento, quantidade: 12 };
      medicamentoService.ajustarQuantidade.mockReturnValue(of(atualizado));

      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('ajuste');
      cmp.delta = 2;
      cmp.ajustar();

      expect(medicamentoService.ajustarQuantidade).toHaveBeenCalledWith('1', 2);
      expect(cmp.medicamento?.quantidade).toBe(12);
      expect(cmp.secao).toBeNull();
      expect(cmp.delta).toBeNull();
      expect(alertaService.sucesso).toHaveBeenCalledWith('Estoque ajustado com sucesso.');
    });

    it('ajustar ignora chamada com delta inválido', () => {
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('ajuste');
      cmp.delta = 0;
      cmp.ajustar();
      expect(medicamentoService.ajustarQuantidade).not.toHaveBeenCalled();
    });

    it('erro no ajustar notifica e libera salvando', () => {
      medicamentoService.ajustarQuantidade.mockReturnValue(throwError(() => new Error('x')));
      const fixture = TestBed.createComponent(MedicamentoDetalhesComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrir('ajuste');
      cmp.delta = 3;
      cmp.ajustar();
      expect(alertaService.erro).toHaveBeenCalledWith('x');
      expect(cmp.salvando).toBe(false);
    });
  });
});
