import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CategoriaService, CategoriaForm } from '../../services/categoria.service';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { CategoriaResponseDTO } from '../../../../core/models/categoria.model';

@Component({
  selector: 'app-categorias-page',
  standalone: false,
  templateUrl: './categorias-page.component.html',
  styleUrl: './categorias-page.component.scss'
})
export class CategoriasPageComponent implements OnInit {
  categorias: CategoriaResponseDTO[] = [];
  carregando = true;
  mostrarForm = false;
  editandoId: string | null = null;
  salvando = false;

  form: CategoriaForm = { nome: '', descricao: '' };

  constructor(
    private categoriaService: CategoriaService,
    private alertaService: AlertaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.categoriaService.listar().subscribe({
      next: (data) => {
        this.categorias = data;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        this.alertaService.erro(err.message);
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  abrirNovo(): void {
    this.form = { nome: '', descricao: '' };
    this.editandoId = null;
    this.mostrarForm = true;
  }

  abrirEditar(cat: CategoriaResponseDTO): void {
    this.form = { nome: cat.nome, descricao: cat.descricao ?? '' };
    this.editandoId = cat.id;
    this.mostrarForm = true;
  }

  cancelar(): void {
    this.mostrarForm = false;
    this.editandoId = null;
  }

  salvar(): void {
    if (!this.form.nome.trim()) return;
    this.salvando = true;
    const idEditando = this.editandoId;

    const operacao = idEditando
      ? this.categoriaService.atualizar(idEditando, this.form)
      : this.categoriaService.criar(this.form);

    operacao.subscribe({
      next: (categoria: CategoriaResponseDTO) => {
        if (idEditando) {
          this.categorias = this.categorias.map(c => c.id === categoria.id ? categoria : c);
          this.alertaService.sucesso('Categoria atualizada.');
        } else {
          this.categorias = [...this.categorias, categoria];
          this.alertaService.sucesso('Categoria criada.');
        }
        this.mostrarForm = false;
        this.editandoId = null;
        this.salvando = false;
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        this.alertaService.erro(err.message);
        this.salvando = false;
        this.cdr.detectChanges();
      }
    });
  }

  excluir(cat: CategoriaResponseDTO): void {
    if (!confirm(`Excluir "${cat.nome}"?`)) return;
    this.categoriaService.excluir(cat.id).subscribe({
      next: () => {
        this.categorias = this.categorias.filter(c => c.id !== cat.id);
        this.alertaService.sucesso('Categoria excluída.');
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        this.alertaService.erro(err.message);
        this.cdr.detectChanges();
      }
    });
  }
}
