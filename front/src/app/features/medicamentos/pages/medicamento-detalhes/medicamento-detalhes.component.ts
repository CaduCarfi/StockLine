import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { catchError, of } from 'rxjs';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { AlertaService } from '../../../../shared/services/alerta.service';
import { CategoriaService } from '../../../categorias/services/categoria.service';
import { MedicamentoService, Movimentacao } from '../../services/medicamento.service';

@Component({
  selector: 'app-medicamento-detalhes',
  standalone: false,
  templateUrl: './medicamento-detalhes.component.html',
  styleUrl: './medicamento-detalhes.component.scss'
})
export class MedicamentoDetalhesComponent implements OnInit {
  medicamento: MedicamentoResponseDTO | null = null;
  categoria = '';
  carregando = true;
  erro = '';
  secao: 'entrada' | 'baixa' | 'historico' | null = null;
  lote = '';
  validade = '';
  quantidade: number | null = null;
  salvando = false;
  carregandoHistorico = false;
  erroHistorico = '';
  historico: Movimentacao[] = [];

  constructor(
    private rota: ActivatedRoute,
    private medicamentos: MedicamentoService,
    private categorias: CategoriaService,
    private alertas: AlertaService,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  ngOnInit(): void {
    const id = this.rota.snapshot.paramMap.get('id')!;
    this.medicamentos.buscarPorId(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: medicamento => {
        this.medicamento = medicamento;
        this.carregando = false;
        this.cdr.detectChanges();
        this.categorias.listar().pipe(
          catchError(() => of([])),
          takeUntilDestroyed(this.destroyRef)
        ).subscribe(categorias => {
          this.categoria = categorias.find(c => c.id === medicamento.categoriaId)?.nome ?? '';
          this.cdr.detectChanges();
        });
      },
      error: (erro: Error) => {
        this.erro = erro.message;
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  abrir(secao: 'entrada' | 'baixa' | 'historico'): void {
    this.secao = secao;
    this.lote = '';
    this.validade = '';
    this.quantidade = null;
    if (secao !== 'historico' || !this.medicamento || this.carregandoHistorico) return;
    this.carregandoHistorico = true;
    this.erroHistorico = '';
    this.medicamentos.consultarHistorico(this.medicamento.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: historico => {
          this.historico = historico;
          this.carregandoHistorico = false;
          this.cdr.detectChanges();
        },
        error: (erro: Error) => {
          this.erroHistorico = erro.message;
          this.carregandoHistorico = false;
          this.cdr.detectChanges();
        }
      });
  }

  get movimentacaoValida(): boolean {
    return this.lote.trim().length > 0 && this.quantidade !== null &&
      Number.isInteger(this.quantidade) && this.quantidade > 0 &&
      (this.secao !== 'entrada' || !!this.validade);
  }

  registrar(): void {
    if (!this.medicamento || !this.movimentacaoValida || this.salvando ||
        this.quantidade === null || (this.secao !== 'entrada' && this.secao !== 'baixa')) return;

    const dto = { lote: this.lote.trim(), quantidade: this.quantidade };
    const operacao = this.secao === 'entrada'
      ? this.medicamentos.registrarEntrada(this.medicamento.id, { ...dto, validade: this.validade })
      : this.medicamentos.registrarBaixa(this.medicamento.id, dto);
    this.salvando = true;
    operacao.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: medicamento => {
        this.medicamento = medicamento;
        this.secao = null;
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
