import { ChangeDetectorRef, Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable } from 'rxjs';
import { MedicamentoResponseDTO } from '../../../../core/models/medicamento.model';
import { ApiService } from '../../../../core/services/api.service';

interface LoteAlerta {
  medicamentoId: string;
  medicamentoNome: string;
  lote: string;
  quantidade: number;
  validade: string;
}

interface Resultado<T> {
  itens: T[];
  carregando: boolean;
  erro: string;
}

@Component({
  selector: 'app-alertas-page',
  standalone: false,
  templateUrl: './alertas-page.component.html',
  styleUrl: './alertas-page.component.scss'
})
export class AlertasPageComponent implements OnInit {
  aba: 'estoque' | 'vencidos' | 'a-vencer' = 'estoque';
  dias: number | null = 30;
  diasConsultados = 30;
  estoque: Resultado<MedicamentoResponseDTO> = { itens: [], carregando: true, erro: '' };
  vencidos: Resultado<LoteAlerta> = { itens: [], carregando: true, erro: '' };
  aVencer: Resultado<LoteAlerta> = { itens: [], carregando: true, erro: '' };

  constructor(private api: ApiService, private cdr: ChangeDetectorRef, private destroyRef: DestroyRef) {}

  ngOnInit(): void {
    this.consultar(this.estoque, this.api.get<MedicamentoResponseDTO[]>('/medicamentos/estoque-baixo'));
    this.consultar(this.vencidos, this.api.get<LoteAlerta[]>('/lotes/vencidos'));
    this.consultar(this.aVencer, this.api.get<LoteAlerta[]>('/lotes/a-vencer?dias=30'));
  }

  get diasValidos(): boolean {
    return this.dias !== null && Number.isInteger(this.dias) && this.dias > 0 && this.dias <= 2147483647;
  }

  aplicarPeriodo(): void {
    if (!this.diasValidos || this.dias === null || this.aVencer.carregando) return;
    this.diasConsultados = this.dias;
    this.consultar(this.aVencer, this.api.get<LoteAlerta[]>(`/lotes/a-vencer?dias=${this.diasConsultados}`));
  }

  private consultar<T>(resultado: Resultado<T>, operacao: Observable<T[]>): void {
    resultado.carregando = true;
    resultado.erro = '';
    operacao.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: itens => {
        resultado.itens = itens;
        resultado.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro: Error) => {
        resultado.itens = [];
        resultado.erro = erro.message;
        resultado.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }
}
