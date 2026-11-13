import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { MedicamentoService, MedicamentoForm } from '../../services/medicamento.service';
import { CategoriaService } from '../../../categorias/services/categoria.service';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { CategoriaResponseDTO } from '../../../../core/models/categoria.model';

@Component({
  selector: 'app-medicamentos-page',
  standalone: false,
  templateUrl: './medicamentos-page.component.html',
  styleUrl: './medicamentos-page.component.scss'
})
export class MedicamentosPageComponent implements OnInit {
  medicamentos: MedicamentoResponseDTO[] = [];
  categorias = new Map<string, string>();
  categoriasList: CategoriaResponseDTO[] = [];
  carregando = true;
  busca = '';

  mostrarForm = false;
  editandoId: string | null = null;
  salvando = false;
  form: MedicamentoForm = this.formVazio();

  private get hoje(): string {
    return new Date().toISOString().split('T')[0];
  }

  private get limite30(): string {
    const d = new Date();
    d.setDate(d.getDate() + 30);
    return d.toISOString().split('T')[0];
  }

  constructor(
    private medicamentoService: MedicamentoService,
    private categoriaService: CategoriaService,
    private alertaService: AlertaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    forkJoin({
      medicamentos: this.medicamentoService.listar(),
      categorias: this.categoriaService.listar()
    }).subscribe({
      next: ({ medicamentos, categorias }) => {
        this.medicamentos = medicamentos;
        this.categoriasList = categorias;
        this.categorias = new Map(categorias.map(c => [c.id, c.nome]));
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

  get medicamentosFiltrados(): MedicamentoResponseDTO[] {
    const termo = this.busca.toLowerCase().trim();
    if (!termo) return this.medicamentos;
    return this.medicamentos.filter(m => m.nome.toLowerCase().includes(termo));
  }

  validadeMaisProxima(med: MedicamentoResponseDTO): string {
    if (med.lotes?.length) {
      return med.lotes
        .map(l => l.validade)
        .filter(Boolean)
        .sort()[0] ?? '';
    }
    return med.validade ?? '';
  }

  lotesResumo(med: MedicamentoResponseDTO): string {
    if (med.lotes?.length) {
      return med.lotes.length === 1 ? med.lotes[0].codigo : `${med.lotes.length} lotes`;
    }
    return med.lote ?? '—';
  }

  estaVencido(validade: string): boolean {
    return !!validade && validade < this.hoje;
  }

  estaProximoVencimento(validade: string): boolean {
    return !!validade && validade >= this.hoje && validade <= this.limite30;
  }

  estoqueAbaixoMinimo(med: MedicamentoResponseDTO): boolean {
    if (med.estoqueMinimo == null) return false;
    return med.quantidade <= med.estoqueMinimo;
  }

  classeLinha(med: MedicamentoResponseDTO): string {
    const validade = this.validadeMaisProxima(med);
    if (this.estaVencido(validade)) return 'linha--vencido';
    if (this.estaProximoVencimento(validade)) return 'linha--vencimento-proximo';
    if (this.estoqueAbaixoMinimo(med)) return 'linha--estoque-baixo';
    return '';
  }

  abrirNovo(): void {
    this.form = this.formVazio();
    this.editandoId = null;
    this.mostrarForm = true;
    this.cdr.detectChanges();
  }

  abrirEditar(med: MedicamentoResponseDTO): void {
    this.form = {
      nome: med.nome,
      descricao: med.descricao ?? '',
      quantidade: med.quantidade,
      validade: med.validade,
      lote: med.lote,
      categoriaId: med.categoriaId,
      estoqueMinimo: med.estoqueMinimo
    };
    this.editandoId = med.id;
    this.mostrarForm = true;
    this.cdr.detectChanges();
  }

  cancelar(): void {
    this.mostrarForm = false;
    this.editandoId = null;
    this.cdr.detectChanges();
  }

  get formValido(): boolean {
    return !!(
      this.form.nome.trim() &&
      this.form.lote.trim() &&
      this.form.validade &&
      this.form.categoriaId &&
      this.form.quantidade !== null && this.form.quantidade >= 0 &&
      (this.form.estoqueMinimo === null || this.form.estoqueMinimo >= 0)
    );
  }

  salvar(): void {
    if (!this.formValido) return;
    this.salvando = true;
    const idEditando = this.editandoId;
    const operacao = idEditando
      ? this.medicamentoService.atualizar(idEditando, this.form)
      : this.medicamentoService.criar(this.form);

    operacao.subscribe({
      next: (med: MedicamentoResponseDTO) => {
        if (idEditando) {
          this.medicamentos = this.medicamentos.map(m => m.id === med.id ? med : m);
          this.alertaService.sucesso('Medicamento atualizado com sucesso.');
        } else {
          this.medicamentos = [...this.medicamentos, med];
          this.alertaService.sucesso('Medicamento cadastrado com sucesso.');
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

  excluir(med: MedicamentoResponseDTO): void {
    if (!confirm(`Excluir "${med.nome}"?`)) return;
    this.medicamentoService.excluir(med.id).subscribe({
      next: () => {
        this.medicamentos = this.medicamentos.filter(m => m.id !== med.id);
        this.alertaService.sucesso('Medicamento excluído com sucesso.');
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        this.alertaService.erro(err.message);
        this.cdr.detectChanges();
      }
    });
  }

  private formVazio(): MedicamentoForm {
    return { nome: '', descricao: '', quantidade: null, validade: '', lote: '', categoriaId: '', estoqueMinimo: null };
  }
}
