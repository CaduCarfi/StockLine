import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { LoteService, LoteVencimento } from '../../services/lote.service';
import { AlertaService } from '../../../../shared/services/alerta.service';

@Component({
  selector: 'app-alertas-page',
  standalone: false,
  templateUrl: './alertas-page.component.html',
  styleUrl: './alertas-page.component.scss'
})
export class AlertasPageComponent implements OnInit {
  vencidos: LoteVencimento[] = [];
  aVencer: LoteVencimento[] = [];
  carregando = true;
  dias = 30;

  constructor(
    private loteService: LoteService,
    private alertaService: AlertaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    forkJoin({
      vencidos: this.loteService.buscarVencidos(),
      aVencer: this.loteService.buscarAVencer(this.dias)
    }).subscribe({
      next: ({ vencidos, aVencer }) => {
        this.vencidos = vencidos;
        this.aVencer = aVencer;
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

  filtrarAVencer(): void {
    if (!this.dias || this.dias < 1) this.dias = 30;
    this.loteService.buscarAVencer(this.dias).subscribe({
      next: (lista) => {
        this.aVencer = lista;
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        this.alertaService.erro(err.message);
        this.cdr.detectChanges();
      }
    });
  }
}
