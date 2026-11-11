import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { MedicamentoService } from '../../services/medicamento.service';
import { CategoriaService } from '../../../categorias/services/categoria.service';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';

@Component({
  selector: 'app-medicamentos-page',
  standalone: false,
  templateUrl: './medicamentos-page.component.html',
  styleUrl: './medicamentos-page.component.scss'
})
export class MedicamentosPageComponent implements OnInit {
  medicamentos: MedicamentoResponseDTO[] = [];
  categorias = new Map<string, string>();
  carregando = true;
  busca = '';

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
    private alertaService: AlertaService
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
        this.categorias = new Map(categorias.map(c => [c.id, c.nome]));
        this.carregando = false;
      },
      error: (err: Error) => {
        this.alertaService.erro(err.message);
        this.carregando = false;
      }
    });
  }

  get medicamentosFiltrados(): MedicamentoResponseDTO[] {
    const termo = this.busca.toLowerCase().trim();
    if (!termo) return this.medicamentos;
    return this.medicamentos.filter(m => m.nome.toLowerCase().includes(termo));
  }

  estaVencido(validade: string): boolean {
    return validade < this.hoje;
  }

  estaProximoVencimento(validade: string): boolean {
    return validade >= this.hoje && validade <= this.limite30;
  }

  estoqueAbaixoMinimo(med: MedicamentoResponseDTO): boolean {
    if (med.estoqueMinimo == null) return false;
    return med.quantidade <= med.estoqueMinimo;
  }

  classeLinha(med: MedicamentoResponseDTO): string {
    if (this.estaVencido(med.validade)) return 'linha--vencido';
    if (this.estaProximoVencimento(med.validade)) return 'linha--vencimento-proximo';
    if (this.estoqueAbaixoMinimo(med)) return 'linha--estoque-baixo';
    return '';
  }

  excluir(med: MedicamentoResponseDTO): void {
    if (!confirm(`Excluir "${med.nome}"?`)) return;
    this.medicamentoService.excluir(med.id).subscribe({
      next: () => {
        this.alertaService.sucesso('Medicamento excluído com sucesso.');
        this.carregar();
      },
      error: (err: Error) => this.alertaService.erro(err.message)
    });
  }

  editar(_med: MedicamentoResponseDTO): void {
    this.alertaService.erro('Funcionalidade em breve.');
  }

  ajustar(_med: MedicamentoResponseDTO): void {
    this.alertaService.erro('Funcionalidade em breve.');
  }

  verDetalhes(_med: MedicamentoResponseDTO): void {
    this.alertaService.erro('Funcionalidade em breve.');
  }
}
