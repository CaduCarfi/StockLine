import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { Lote } from '../../../../core/models/lote.model';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { MedicamentoService } from '../../../medicamentos/services/medicamento.service';

@Component({
  selector: 'app-movimentacoes-page',
  standalone: false,
  templateUrl: './movimentacoes-page.component.html',
  styleUrl: './movimentacoes-page.component.scss'
})
export class MovimentacoesPageComponent implements OnInit {
  medicamentos: MedicamentoResponseDTO[] = [];
  medicamentoSelecionado: MedicamentoResponseDTO | null = null;
  medicamentoId = '';
  tipo: 'ENTRADA' | 'SAIDA' | '' = '';
  lote = '';
  validade = '';
  quantidade: number | null = null;
  carregandoMedicamentos = true;
  carregandoDetalhes = false;
  salvando = false;
  erroMedicamentos = '';

  constructor(
    private medicamentosService: MedicamentoService,
    private alertas: AlertaService,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  ngOnInit(): void {
    this.medicamentosService.listar().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: lista => {
        this.medicamentos = [...lista].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
        this.carregandoMedicamentos = false;
        this.cdr.detectChanges();
      },
      error: (erro: Error) => {
        this.erroMedicamentos = erro.message;
        this.carregandoMedicamentos = false;
        this.cdr.detectChanges();
      }
    });
  }

  selecionarMedicamento(id: string): void {
    this.medicamentoId = id;
    this.medicamentoSelecionado = null;
    this.tipo = '';
    this.lote = '';
    this.validade = '';
    this.quantidade = null;
    if (!id) return;
    this.carregandoDetalhes = true;
    this.medicamentosService.buscarPorId(id).pipe(
      catchError(() => of(null)),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(med => {
      this.medicamentoSelecionado = med;
      this.carregandoDetalhes = false;
      this.cdr.detectChanges();
    });
  }

  mudarTipo(tipo: 'ENTRADA' | 'SAIDA' | ''): void {
    this.tipo = tipo;
    this.lote = '';
    this.validade = '';
    this.quantidade = null;
  }

  get lotesDisponiveis(): Lote[] {
    return (this.medicamentoSelecionado?.lotes ?? []).filter(l => l.saldo > 0);
  }

  get formValido(): boolean {
    if (!this.medicamentoId || !this.tipo || !this.lote.trim()) return false;
    if (!this.quantidade || !Number.isInteger(this.quantidade) || this.quantidade < 1) return false;
    if (this.tipo === 'ENTRADA' && !this.validade) return false;
    return true;
  }

  registrar(): void {
    if (!this.formValido || this.salvando || !this.medicamentoId || this.quantidade === null) return;
    this.salvando = true;
    const dto = { lote: this.lote.trim(), quantidade: this.quantidade };
    const operacao = this.tipo === 'ENTRADA'
      ? this.medicamentosService.registrarEntrada(this.medicamentoId, { ...dto, validade: this.validade })
      : this.medicamentosService.registrarBaixa(this.medicamentoId, dto);

    operacao.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: medicamento => {
        this.medicamentoSelecionado = medicamento;
        this.tipo = '';
        this.lote = '';
        this.validade = '';
        this.quantidade = null;
        this.salvando = false;
        this.alertas.sucesso('Movimentação registrada com sucesso.');
        this.cdr.detectChanges();
      },
      error: (erro: Error) => {
        this.salvando = false;
        this.alertas.erro(erro.message);
        this.cdr.detectChanges();
      }
    });
  }
}
