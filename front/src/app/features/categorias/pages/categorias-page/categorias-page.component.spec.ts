import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { CategoriaResponseDTO } from '../../../../core/models/categoria.model';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { CategoriaService } from '../../services/categoria.service';
import { CategoriasPageComponent } from './categorias-page.component';

describe('CategoriasPageComponent', () => {
  let categoriaService: {
    listar: ReturnType<typeof vi.fn>;
    criar: ReturnType<typeof vi.fn>;
    atualizar: ReturnType<typeof vi.fn>;
    excluir: ReturnType<typeof vi.fn>;
  };
  let alertaService: {
    sucesso: ReturnType<typeof vi.fn>;
    erro: ReturnType<typeof vi.fn>;
  };

  const cat1: CategoriaResponseDTO = { id: '1', nome: 'Analgésicos', descricao: 'd1' };
  const cat2: CategoriaResponseDTO = { id: '2', nome: 'Antibióticos' };

  beforeEach(() => {
    categoriaService = {
      listar: vi.fn().mockReturnValue(of([cat1, cat2])),
      criar: vi.fn(),
      atualizar: vi.fn(),
      excluir: vi.fn(),
    };
    alertaService = { sucesso: vi.fn(), erro: vi.fn() };

    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule],
      declarations: [CategoriasPageComponent],
      providers: [
        { provide: CategoriaService, useValue: categoriaService },
        { provide: AlertaService, useValue: alertaService },
      ],
    });
  });

  it('carrega categorias no ngOnInit e renderiza as linhas', () => {
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();

    const cmp = fixture.componentInstance;
    expect(cmp.carregando).toBe(false);
    expect(cmp.categorias.length).toBe(2);
    const linhas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(linhas.length).toBe(2);
  });

  it('exibe erro via alertaService quando a listagem falha', () => {
    categoriaService.listar.mockReturnValue(throwError(() => new Error('boom')));
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    expect(alertaService.erro).toHaveBeenCalledWith('boom');
    expect(fixture.componentInstance.carregando).toBe(false);
  });

  it('abrirNovo zera o form e mostra o painel; cancelar esconde', () => {
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.form = { nome: 'Lixo', descricao: 'Lixo' };
    cmp.abrirNovo();
    expect(cmp.mostrarForm).toBe(true);
    expect(cmp.editandoId).toBeNull();
    expect(cmp.form).toEqual({ nome: '', descricao: '' });

    cmp.cancelar();
    expect(cmp.mostrarForm).toBe(false);
    expect(cmp.editandoId).toBeNull();
  });

  it('abrirEditar preenche form com dados da categoria', () => {
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.abrirEditar(cat2);
    expect(cmp.editandoId).toBe('2');
    expect(cmp.form.nome).toBe('Antibióticos');
    expect(cmp.form.descricao).toBe(''); // cat2 sem descrição vira string vazia
    expect(cmp.mostrarForm).toBe(true);
  });

  it('não salva quando o nome é vazio', () => {
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.form = { nome: '   ', descricao: '' };
    cmp.salvar();
    expect(categoriaService.criar).not.toHaveBeenCalled();
    expect(categoriaService.atualizar).not.toHaveBeenCalled();
  });

  it('cria categoria: adiciona à lista, notifica sucesso e fecha form', () => {
    const nova: CategoriaResponseDTO = { id: '3', nome: 'Novos' };
    categoriaService.criar.mockReturnValue(of(nova));

    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.abrirNovo();
    cmp.form = { nome: 'Novos', descricao: '' };
    cmp.salvar();

    expect(categoriaService.criar).toHaveBeenCalledWith({ nome: 'Novos', descricao: '' });
    expect(cmp.categorias.map(c => c.id)).toContain('3');
    expect(alertaService.sucesso).toHaveBeenCalledWith('Categoria criada.');
    expect(cmp.mostrarForm).toBe(false);
  });

  it('atualiza categoria: substitui item na lista e mantém restante', () => {
    const atualizada: CategoriaResponseDTO = { id: '1', nome: 'Analgésicos NOVO', descricao: 'x' };
    categoriaService.atualizar.mockReturnValue(of(atualizada));

    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.abrirEditar(cat1);
    cmp.form.nome = 'Analgésicos NOVO';
    cmp.salvar();

    expect(categoriaService.atualizar)
      .toHaveBeenCalledWith('1', { nome: 'Analgésicos NOVO', descricao: 'd1' });
    expect(cmp.categorias.find(c => c.id === '1')?.nome).toBe('Analgésicos NOVO');
    expect(cmp.categorias.length).toBe(2);
    expect(alertaService.sucesso).toHaveBeenCalledWith('Categoria atualizada.');
  });

  it('tratamento de erro no salvar preserva form aberto e sinaliza alerta', () => {
    categoriaService.criar.mockReturnValue(throwError(() => new Error('nope')));

    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.abrirNovo();
    cmp.form.nome = 'X';
    cmp.salvar();

    expect(alertaService.erro).toHaveBeenCalledWith('nope');
    expect(cmp.salvando).toBe(false);
  });

  it('excluir confirmado remove da lista e notifica', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    categoriaService.excluir.mockReturnValue(of(undefined));

    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;

    cmp.excluir(cat1);
    expect(categoriaService.excluir).toHaveBeenCalledWith('1');
    expect(cmp.categorias.map(c => c.id)).toEqual(['2']);
    expect(alertaService.sucesso).toHaveBeenCalledWith('Categoria excluída.');
  });

  it('excluir cancelado no confirm não chama service', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    fixture.componentInstance.excluir(cat1);
    expect(categoriaService.excluir).not.toHaveBeenCalled();
  });

  it('erro no excluir notifica e mantém a categoria na lista', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    categoriaService.excluir.mockReturnValue(throwError(() => new Error('nao foi')));
    const fixture = TestBed.createComponent(CategoriasPageComponent);
    fixture.detectChanges();
    const cmp = fixture.componentInstance;
    cmp.excluir(cat1);
    expect(alertaService.erro).toHaveBeenCalledWith('nao foi');
    expect(cmp.categorias.length).toBe(2);
  });
});
