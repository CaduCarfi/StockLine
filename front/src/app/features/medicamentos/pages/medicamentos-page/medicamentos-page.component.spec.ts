import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CategoriaResponseDTO } from '../../../../core/models/categoria.model';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { CategoriaService } from '../../../categorias/services/categoria.service';
import { MedicamentoService } from '../../services/medicamento.service';
import { MedicamentosPageComponent } from './medicamentos-page.component';

function dataOffset(dias: number): string {
  const d = new Date();
  d.setDate(d.getDate() + dias);
  return d.toISOString().split('T')[0];
}

function medFactory(over: Partial<MedicamentoResponseDTO> = {}): MedicamentoResponseDTO {
  return {
    id: '1', nome: 'Paracetamol', descricao: 'd', quantidade: 10,
    validade: dataOffset(60), lote: 'L', categoriaId: 'C1',
    estoqueMinimo: null, lotes: [],
    ...over,
  };
}

describe('MedicamentosPageComponent', () => {
  let medicamentoService: {
    listar: ReturnType<typeof vi.fn>;
    criar: ReturnType<typeof vi.fn>;
    atualizar: ReturnType<typeof vi.fn>;
    excluir: ReturnType<typeof vi.fn>;
  };
  let categoriaService: { listar: ReturnType<typeof vi.fn> };
  let alertaService: { sucesso: ReturnType<typeof vi.fn>; erro: ReturnType<typeof vi.fn> };

  const categorias: CategoriaResponseDTO[] = [
    { id: 'C1', nome: 'Dor' },
    { id: 'C2', nome: 'Infecção' },
  ];
  const medicamentos: MedicamentoResponseDTO[] = [
    medFactory(),
    medFactory({ id: '2', nome: 'Amoxicilina', categoriaId: 'C2', quantidade: 2, estoqueMinimo: 5 }),
  ];

  beforeEach(() => {
    medicamentoService = {
      listar: vi.fn().mockReturnValue(of(medicamentos)),
      criar: vi.fn(),
      atualizar: vi.fn(),
      excluir: vi.fn(),
    };
    categoriaService = { listar: vi.fn().mockReturnValue(of(categorias)) };
    alertaService = { sucesso: vi.fn(), erro: vi.fn() };

    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule, RouterModule.forRoot([])],
      declarations: [MedicamentosPageComponent],
      providers: [
        { provide: MedicamentoService, useValue: medicamentoService },
        { provide: CategoriaService, useValue: categoriaService },
        { provide: AlertaService, useValue: alertaService },
      ],
    });
  });

  it('carrega medicamentos e categorias e monta o Map por id', () => {
    const fixture = TestBed.createComponent(MedicamentosPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    expect(cmp.carregando).toBe(false);
    expect(cmp.medicamentos.length).toBe(2);
    expect(cmp.categorias.get('C1')).toBe('Dor');
    expect(cmp.categorias.get('C2')).toBe('Infecção');
  });

  it('erro ao carregar dispara alerta e para o loading', () => {
    medicamentoService.listar.mockReturnValue(throwError(() => new Error('fail')));
    const fixture = TestBed.createComponent(MedicamentosPageComponent);
    fixture.detectChanges();
    expect(alertaService.erro).toHaveBeenCalledWith('fail');
    expect(fixture.componentInstance.carregando).toBe(false);
  });

  it('medicamentosFiltrados retorna todos quando não há termo', () => {
    const fixture = TestBed.createComponent(MedicamentosPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.busca = '   ';
    expect(cmp.medicamentosFiltrados.length).toBe(2);
  });

  it('medicamentosFiltrados filtra pelo nome ignorando caixa', () => {
    const fixture = TestBed.createComponent(MedicamentosPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.busca = 'AMOX';
    expect(cmp.medicamentosFiltrados.map(m => m.id)).toEqual(['2']);
  });

  describe('regras de validade / estoque', () => {
    let cmp: MedicamentosPageComponent;

    beforeEach(() => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      cmp = fixture.componentInstance;
    });

    it('estaVencido verdadeiro para data anterior a hoje', () => {
      expect(cmp.estaVencido(dataOffset(-1))).toBe(true);
    });

    it('estaVencido falso para hoje em diante', () => {
      expect(cmp.estaVencido(dataOffset(0))).toBe(false);
      expect(cmp.estaVencido(dataOffset(60))).toBe(false);
    });

    it('estaProximoVencimento verdadeiro para datas entre hoje e +30', () => {
      expect(cmp.estaProximoVencimento(dataOffset(0))).toBe(true);
      expect(cmp.estaProximoVencimento(dataOffset(30))).toBe(true);
    });

    it('estaProximoVencimento falso fora da janela (vencido ou >30)', () => {
      expect(cmp.estaProximoVencimento(dataOffset(-1))).toBe(false);
      expect(cmp.estaProximoVencimento(dataOffset(31))).toBe(false);
    });

    it('estoqueAbaixoMinimo falso quando estoqueMinimo é null', () => {
      expect(cmp.estoqueAbaixoMinimo(medFactory({ estoqueMinimo: null, quantidade: 0 }))).toBe(false);
    });

    it('estoqueAbaixoMinimo verdadeiro quando quantidade <= estoqueMinimo', () => {
      expect(cmp.estoqueAbaixoMinimo(medFactory({ estoqueMinimo: 5, quantidade: 5 }))).toBe(true);
      expect(cmp.estoqueAbaixoMinimo(medFactory({ estoqueMinimo: 5, quantidade: 1 }))).toBe(true);
    });

    it('estoqueAbaixoMinimo falso quando quantidade > estoqueMinimo', () => {
      expect(cmp.estoqueAbaixoMinimo(medFactory({ estoqueMinimo: 5, quantidade: 6 }))).toBe(false);
    });

    it('classeLinha prioriza vencido > próximo > baixo > nenhuma', () => {
      const vencido = medFactory({ validade: dataOffset(-1), quantidade: 0, estoqueMinimo: 1 });
      const proximo = medFactory({ validade: dataOffset(10), quantidade: 0, estoqueMinimo: 1 });
      const baixo = medFactory({ validade: dataOffset(60), quantidade: 0, estoqueMinimo: 1 });
      const normal = medFactory({ validade: dataOffset(60), estoqueMinimo: null });

      expect(cmp.classeLinha(vencido)).toBe('linha--vencido');
      expect(cmp.classeLinha(proximo)).toBe('linha--vencimento-proximo');
      expect(cmp.classeLinha(baixo)).toBe('linha--estoque-baixo');
      expect(cmp.classeLinha(normal)).toBe('');
    });
  });

  describe('formulário', () => {
    it('formValido falso enquanto campos obrigatórios estão vazios', () => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      expect(cmp.formValido).toBe(false);
    });

    it('formValido verdadeiro com campos preenchidos e quantidade >= 0', () => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.form = {
        nome: 'X', descricao: '', quantidade: 0, validade: '2030-01-01',
        lote: 'L', categoriaId: 'C1', estoqueMinimo: null,
      };
      expect(cmp.formValido).toBe(true);
    });

    it('formValido falso se quantidade negativa', () => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.form = {
        nome: 'X', descricao: '', quantidade: -1, validade: '2030-01-01',
        lote: 'L', categoriaId: 'C1', estoqueMinimo: null,
      };
      expect(cmp.formValido).toBe(false);
    });

    it('formValido falso se estoqueMinimo negativo', () => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.form = {
        nome: 'X', descricao: '', quantidade: 1, validade: '2030-01-01',
        lote: 'L', categoriaId: 'C1', estoqueMinimo: -1,
      };
      expect(cmp.formValido).toBe(false);
    });

    it('abrirEditar preenche formulário e seta editandoId', () => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;

      cmp.abrirEditar(medicamentos[1]);
      expect(cmp.editandoId).toBe('2');
      expect(cmp.form.nome).toBe('Amoxicilina');
      expect(cmp.mostrarForm).toBe(true);
    });
  });

  describe('salvar / excluir', () => {
    it('não dispara requisição quando form é inválido', () => {
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      fixture.componentInstance.salvar();
      expect(medicamentoService.criar).not.toHaveBeenCalled();
    });

    it('criar: adiciona ao array e notifica sucesso', () => {
      const criado = medFactory({ id: '99', nome: 'Novo' });
      medicamentoService.criar.mockReturnValue(of(criado));

      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;

      cmp.abrirNovo();
      cmp.form = {
        nome: 'Novo', descricao: '', quantidade: 1, validade: '2030-01-01',
        lote: 'L', categoriaId: 'C1', estoqueMinimo: null,
      };
      cmp.salvar();

      expect(medicamentoService.criar).toHaveBeenCalled();
      expect(cmp.medicamentos.map(m => m.id)).toContain('99');
      expect(alertaService.sucesso)
        .toHaveBeenCalledWith('Medicamento cadastrado com sucesso.');
      expect(cmp.mostrarForm).toBe(false);
    });

    it('editar: substitui o medicamento existente', () => {
      const atualizado = medFactory({ id: '1', nome: 'Paracetamol Plus' });
      medicamentoService.atualizar.mockReturnValue(of(atualizado));

      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;

      cmp.abrirEditar(medicamentos[0]);
      cmp.form.nome = 'Paracetamol Plus';
      cmp.salvar();

      expect(medicamentoService.atualizar).toHaveBeenCalledWith('1', expect.any(Object));
      expect(cmp.medicamentos.find(m => m.id === '1')?.nome).toBe('Paracetamol Plus');
      expect(alertaService.sucesso)
        .toHaveBeenCalledWith('Medicamento atualizado com sucesso.');
    });

    it('erro no salvar sinaliza alerta e libera botão', () => {
      medicamentoService.criar.mockReturnValue(throwError(() => new Error('oops')));

      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.abrirNovo();
      cmp.form = {
        nome: 'A', descricao: '', quantidade: 1, validade: '2030-01-01',
        lote: 'L', categoriaId: 'C1', estoqueMinimo: null,
      };
      cmp.salvar();

      expect(alertaService.erro).toHaveBeenCalledWith('oops');
      expect(cmp.salvando).toBe(false);
    });

    it('excluir confirmado remove o item e notifica', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      medicamentoService.excluir.mockReturnValue(of(undefined));

      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.excluir(medicamentos[0]);

      expect(cmp.medicamentos.map(m => m.id)).toEqual(['2']);
      expect(alertaService.sucesso)
        .toHaveBeenCalledWith('Medicamento excluído com sucesso.');
    });

    it('excluir cancelado no confirm não chama service', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(false);
      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      fixture.componentInstance.excluir(medicamentos[0]);
      expect(medicamentoService.excluir).not.toHaveBeenCalled();
    });

    it('erro no excluir notifica e preserva lista', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      medicamentoService.excluir.mockReturnValue(throwError(() => new Error('xxx')));

      const fixture = TestBed.createComponent(MedicamentosPageComponent);
      fixture.detectChanges();
      const cmp = fixture.componentInstance;
      cmp.excluir(medicamentos[0]);

      expect(alertaService.erro).toHaveBeenCalledWith('xxx');
      expect(cmp.medicamentos.length).toBe(2);
    });
  });
});
