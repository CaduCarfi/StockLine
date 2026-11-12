import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { MedicamentoService } from '../../../medicamentos/services/medicamento.service';
import { MovimentacoesPageComponent } from './movimentacoes-page.component';

const medicamentos: MedicamentoResponseDTO[] = [
  { id: 'b', nome: 'Beta', descricao: '', quantidade: 10, validade: '2030-01-01', lote: 'L', categoriaId: '', estoqueMinimo: null, lotes: [] },
  { id: 'a', nome: 'Alfa', descricao: '', quantidade: 5, validade: '2030-01-01', lote: 'L', categoriaId: '', estoqueMinimo: null, lotes: [
    { codigo: 'L1', saldo: 3, validade: '2030-01-01' },
    { codigo: 'L2', saldo: 0, validade: '2030-02-01' },
  ] },
];

describe('MovimentacoesPageComponent', () => {
  let medicamentoService: {
    listar: ReturnType<typeof vi.fn>;
    buscarPorId: ReturnType<typeof vi.fn>;
    registrarEntrada: ReturnType<typeof vi.fn>;
    registrarBaixa: ReturnType<typeof vi.fn>;
  };
  let alertaService: { sucesso: ReturnType<typeof vi.fn>; erro: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    medicamentoService = {
      listar: vi.fn().mockReturnValue(of(medicamentos)),
      buscarPorId: vi.fn().mockReturnValue(of(medicamentos[1])),
      registrarEntrada: vi.fn(),
      registrarBaixa: vi.fn(),
    };
    alertaService = { sucesso: vi.fn(), erro: vi.fn() };

    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule],
      declarations: [MovimentacoesPageComponent],
      providers: [
        { provide: MedicamentoService, useValue: medicamentoService },
        { provide: AlertaService, useValue: alertaService },
      ],
    });
  });

  it('ordena a lista inicial por nome', () => {
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.medicamentos.map(m => m.nome)).toEqual(['Alfa', 'Beta']);
    expect(fixture.componentInstance.carregandoMedicamentos).toBe(false);
  });

  it('propaga erro ao listar', () => {
    medicamentoService.listar.mockReturnValue(throwError(() => new Error('bum')));
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.erroMedicamentos).toBe('bum');
  });

  it('selecionarMedicamento("") limpa seleção sem buscar', () => {
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.medicamentoSelecionado = medicamentos[1];
    cmp.selecionarMedicamento('');
    expect(cmp.medicamentoId).toBe('');
    expect(cmp.medicamentoSelecionado).toBeNull();
    expect(medicamentoService.buscarPorId).not.toHaveBeenCalled();
  });

  it('selecionarMedicamento carrega detalhes', () => {
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    fixture.componentInstance.selecionarMedicamento('a');
    expect(medicamentoService.buscarPorId).toHaveBeenCalledWith('a');
    expect(fixture.componentInstance.medicamentoSelecionado?.id).toBe('a');
    expect(fixture.componentInstance.carregandoDetalhes).toBe(false);
  });

  it('selecionarMedicamento em caso de erro deixa seleção nula sem quebrar', () => {
    medicamentoService.buscarPorId.mockReturnValue(throwError(() => new Error('falhou')));
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    fixture.componentInstance.selecionarMedicamento('a');
    expect(fixture.componentInstance.medicamentoSelecionado).toBeNull();
    expect(fixture.componentInstance.carregandoDetalhes).toBe(false);
  });

  it('mudarTipo reseta campos do formulário', () => {
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.lote = 'x'; cmp.validade = 'x'; cmp.quantidade = 2;
    cmp.mudarTipo('ENTRADA');
    expect(cmp.tipo).toBe('ENTRADA');
    expect(cmp.lote).toBe('');
    expect(cmp.validade).toBe('');
    expect(cmp.quantidade).toBeNull();
  });

  it('lotesDisponiveis filtra lotes com saldo > 0', () => {
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.selecionarMedicamento('a');
    expect(cmp.lotesDisponiveis.map(l => l.codigo)).toEqual(['L1']);
  });

  it('lotesDisponiveis retorna [] sem medicamento selecionado', () => {
    const fixture = TestBed.createComponent(MovimentacoesPageComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.lotesDisponiveis).toEqual([]);
  });

  describe('formValido', () => {
    let cmp: MovimentacoesPageComponent;
    beforeEach(() => {
      const fixture = TestBed.createComponent(MovimentacoesPageComponent);
      fixture.detectChanges();
      cmp = fixture.componentInstance;
      cmp.selecionarMedicamento('a');
    });

    it('falso sem tipo', () => {
      cmp.lote = 'L1'; cmp.quantidade = 1;
      expect(cmp.formValido).toBe(false);
    });

    it('falso sem lote', () => {
      cmp.tipo = 'SAIDA'; cmp.quantidade = 1;
      expect(cmp.formValido).toBe(false);
    });

    it('falso com quantidade inválida', () => {
      cmp.tipo = 'SAIDA'; cmp.lote = 'L1';
      cmp.quantidade = 0;
      expect(cmp.formValido).toBe(false);
      cmp.quantidade = 1.5;
      expect(cmp.formValido).toBe(false);
    });

    it('entrada exige validade', () => {
      cmp.tipo = 'ENTRADA'; cmp.lote = 'LZ'; cmp.quantidade = 1;
      expect(cmp.formValido).toBe(false);
      cmp.validade = '2030-01-01';
      expect(cmp.formValido).toBe(true);
    });

    it('baixa não exige validade', () => {
      cmp.tipo = 'SAIDA'; cmp.lote = 'L1'; cmp.quantidade = 1;
      expect(cmp.formValido).toBe(true);
    });
  });

  describe('registrar', () => {
    it('ENTRADA chama registrarEntrada com validade e limpa form', () => {
      const atualizado = { ...medicamentos[1], quantidade: 20 };
      medicamentoService.registrarEntrada.mockReturnValue(of(atualizado));

      const fixture = TestBed.createComponent(MovimentacoesPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.selecionarMedicamento('a');
      cmp.mudarTipo('ENTRADA');
      cmp.lote = 'LX'; cmp.validade = '2030-05-01'; cmp.quantidade = 10;
      cmp.registrar();

      expect(medicamentoService.registrarEntrada).toHaveBeenCalledWith('a', {
        lote: 'LX', quantidade: 10, validade: '2030-05-01',
      });
      expect(cmp.medicamentoSelecionado?.quantidade).toBe(20);
      expect(cmp.tipo).toBe('');
      expect(cmp.lote).toBe('');
      expect(cmp.validade).toBe('');
      expect(cmp.quantidade).toBeNull();
      expect(alertaService.sucesso).toHaveBeenCalledWith('Movimentação registrada com sucesso.');
      expect(cmp.salvando).toBe(false);
    });

    it('SAIDA chama registrarBaixa sem validade', () => {
      const atualizado = { ...medicamentos[1], quantidade: 3 };
      medicamentoService.registrarBaixa.mockReturnValue(of(atualizado));

      const fixture = TestBed.createComponent(MovimentacoesPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.selecionarMedicamento('a');
      cmp.mudarTipo('SAIDA');
      cmp.lote = 'L1'; cmp.quantidade = 2;
      cmp.registrar();

      expect(medicamentoService.registrarBaixa).toHaveBeenCalledWith('a', {
        lote: 'L1', quantidade: 2,
      });
      expect(cmp.medicamentoSelecionado?.quantidade).toBe(3);
    });

    it('registra ignora se form inválido ou sem medicamento', () => {
      const fixture = TestBed.createComponent(MovimentacoesPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.registrar(); // sem nada
      cmp.selecionarMedicamento('a');
      cmp.mudarTipo('SAIDA');
      cmp.registrar(); // sem lote/quantidade
      expect(medicamentoService.registrarEntrada).not.toHaveBeenCalled();
      expect(medicamentoService.registrarBaixa).not.toHaveBeenCalled();
    });

    it('erro no registrar aciona alerta de erro', () => {
      medicamentoService.registrarBaixa.mockReturnValue(throwError(() => new Error('nope')));

      const fixture = TestBed.createComponent(MovimentacoesPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.selecionarMedicamento('a');
      cmp.mudarTipo('SAIDA');
      cmp.lote = 'L1'; cmp.quantidade = 1;
      cmp.registrar();
      expect(alertaService.erro).toHaveBeenCalledWith('nope');
      expect(cmp.salvando).toBe(false);
    });
  });
});
