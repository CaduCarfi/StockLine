import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { MedicamentoForm, MedicamentoService } from './medicamento.service';

describe('MedicamentoService', () => {
  let service: MedicamentoService;
  let api: { get: ReturnType<typeof vi.fn>; post: ReturnType<typeof vi.fn>;
             put: ReturnType<typeof vi.fn>; patch: ReturnType<typeof vi.fn>;
             delete: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    api = {
      get: vi.fn().mockReturnValue(of('ok')),
      post: vi.fn().mockReturnValue(of('ok')),
      put: vi.fn().mockReturnValue(of('ok')),
      patch: vi.fn().mockReturnValue(of('ok')),
      delete: vi.fn().mockReturnValue(of('ok')),
    };
    TestBed.configureTestingModule({
      providers: [
        MedicamentoService,
        { provide: ApiService, useValue: api },
      ],
    });
    service = TestBed.inject(MedicamentoService);
  });

  it('listar chama GET /medicamentos', () => {
    service.listar().subscribe();
    expect(api.get).toHaveBeenCalledWith('/medicamentos');
  });

  it('buscarPorId chama GET /medicamentos/:id', () => {
    service.buscarPorId('42').subscribe();
    expect(api.get).toHaveBeenCalledWith('/medicamentos/42');
  });

  it('criar envia POST /medicamentos com o body', () => {
    const form: MedicamentoForm = {
      nome: 'A', descricao: 'd', quantidade: 10, validade: '2030-01-01',
      lote: 'L', categoriaId: 'C', estoqueMinimo: 2,
    };
    service.criar(form).subscribe();
    expect(api.post).toHaveBeenCalledWith('/medicamentos', form);
  });

  it('atualizar envia PUT /medicamentos/:id com o body', () => {
    const form: MedicamentoForm = {
      nome: 'A', descricao: '', quantidade: 0, validade: '2030-01-01',
      lote: 'L', categoriaId: 'C', estoqueMinimo: null,
    };
    service.atualizar('77', form).subscribe();
    expect(api.put).toHaveBeenCalledWith('/medicamentos/77', form);
  });

  it('excluir envia DELETE /medicamentos/:id', () => {
    service.excluir('9').subscribe();
    expect(api.delete).toHaveBeenCalledWith('/medicamentos/9');
  });

  it('registrarEntrada envia POST /medicamentos/:id/entradas com validade', () => {
    service.registrarEntrada('1', { lote: 'A', quantidade: 5, validade: '2030-02-02' }).subscribe();
    expect(api.post).toHaveBeenCalledWith(
      '/medicamentos/1/entradas',
      { lote: 'A', quantidade: 5, validade: '2030-02-02' },
    );
  });

  it('registrarBaixa envia POST /medicamentos/:id/baixas sem validade', () => {
    service.registrarBaixa('1', { lote: 'A', quantidade: 2 }).subscribe();
    expect(api.post).toHaveBeenCalledWith('/medicamentos/1/baixas', { lote: 'A', quantidade: 2 });
  });

  it('consultarHistorico chama GET /medicamentos/:id/historico', () => {
    service.consultarHistorico('5').subscribe();
    expect(api.get).toHaveBeenCalledWith('/medicamentos/5/historico');
  });

  it('ajustarQuantidade envia PATCH com { delta } (positivo e negativo)', () => {
    service.ajustarQuantidade('1', 3).subscribe();
    expect(api.patch).toHaveBeenCalledWith('/medicamentos/1/quantidade', { delta: 3 });

    service.ajustarQuantidade('1', -4).subscribe();
    expect(api.patch).toHaveBeenLastCalledWith('/medicamentos/1/quantidade', { delta: -4 });
  });
});
