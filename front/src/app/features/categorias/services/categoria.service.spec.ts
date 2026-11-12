import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { CategoriaForm, CategoriaService } from './categoria.service';

describe('CategoriaService', () => {
  let service: CategoriaService;
  let api: { get: ReturnType<typeof vi.fn>; post: ReturnType<typeof vi.fn>;
             put: ReturnType<typeof vi.fn>; delete: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    api = {
      get: vi.fn().mockReturnValue(of([])),
      post: vi.fn().mockReturnValue(of({})),
      put: vi.fn().mockReturnValue(of({})),
      delete: vi.fn().mockReturnValue(of(undefined)),
    };
    TestBed.configureTestingModule({
      providers: [
        CategoriaService,
        { provide: ApiService, useValue: api },
      ],
    });
    service = TestBed.inject(CategoriaService);
  });

  it('listar chama GET /categorias', () => {
    service.listar().subscribe();
    expect(api.get).toHaveBeenCalledWith('/categorias');
  });

  it('criar envia POST /categorias com o body', () => {
    const form: CategoriaForm = { nome: 'X', descricao: 'd' };
    service.criar(form).subscribe();
    expect(api.post).toHaveBeenCalledWith('/categorias', form);
  });

  it('atualizar envia PUT /categorias/:id com o body', () => {
    const form: CategoriaForm = { nome: 'Y', descricao: '' };
    service.atualizar('10', form).subscribe();
    expect(api.put).toHaveBeenCalledWith('/categorias/10', form);
  });

  it('excluir envia DELETE /categorias/:id', () => {
    service.excluir('abc').subscribe();
    expect(api.delete).toHaveBeenCalledWith('/categorias/abc');
  });
});
