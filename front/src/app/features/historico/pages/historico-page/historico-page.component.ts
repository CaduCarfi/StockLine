import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, distinctUntilChanged, map, of, switchMap } from 'rxjs';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { MedicamentoService, Movimentacao } from '../../../medicamentos/services/medicamento.service';

@Component({
  selector: 'app-historico-page',
  standalone: false,
  templateUrl: './historico-page.component.html',
  styleUrl: './historico-page.component.scss'
})
export class HistoricoPageComponent implements OnInit {
  medicamentos: MedicamentoResponseDTO[] = [];
  medicamentoId = '';
  tipo = '';
  movimentacoes: Movimentacao[] = [];
  carregandoMedicamentos = true;
  carregandoHistorico = false;
  erroMedicamentos = '';
  erroHistorico = '';

  constructor(
    private medicamentosService: MedicamentoService,
    private rota: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private destroyRef: DestroyRef
  ) {}

  ngOnInit(): void {
    this.medicamentosService.listar().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: medicamentos => {
        this.medicamentos = [...medicamentos].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
        this.carregandoMedicamentos = false;
        this.cdr.detectChanges();
      },
      error: (erro: Error) => {
        this.erroMedicamentos = erro.message;
        this.carregandoMedicamentos = false;
        this.cdr.detectChanges();
      }
    });

    this.rota.queryParamMap.pipe(
      map(parametros => parametros.get('medicamento') ?? ''),
      distinctUntilChanged(),
      switchMap(id => {
        this.medicamentoId = id;
        this.tipo = '';
        this.carregandoHistorico = !!id;
        this.erroHistorico = '';
        this.movimentacoes = [];
        if (!id) return of([]);
        return this.medicamentosService.consultarHistorico(encodeURIComponent(id)).pipe(
          catchError((erro: Error) => {
            this.erroHistorico = erro.message;
            return of([]);
          })
        );
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(movimentacoes => {
      this.movimentacoes = [...movimentacoes].sort((a, b) => b.data.localeCompare(a.data));
      this.carregandoHistorico = false;
      this.cdr.detectChanges();
    });
  }

  selecionarMedicamento(id: string): void {
    void this.router.navigate([], {
      relativeTo: this.rota,
      queryParams: { medicamento: id || null }
    });
  }

  get filtradas(): Movimentacao[] {
    return this.tipo ? this.movimentacoes.filter(item => item.tipo === this.tipo) : this.movimentacoes;
  }
}
