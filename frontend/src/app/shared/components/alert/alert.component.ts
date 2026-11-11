import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Alerta, AlertaService } from '../../services/alerta.service';

@Component({
  selector: 'app-alert',
  standalone: false,
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.scss'
})
export class AlertComponent implements OnInit, OnDestroy {
  alerta: Alerta | null = null;

  private sub!: Subscription;
  private timer: ReturnType<typeof setTimeout> | null = null;

  constructor(private alertaService: AlertaService) {}

  ngOnInit(): void {
    this.sub = this.alertaService.alerta$.subscribe(alerta => {
      this.alerta = alerta;
      if (this.timer) clearTimeout(this.timer);
      if (alerta) {
        this.timer = setTimeout(() => this.fechar(), 4000);
      }
    });
  }

  fechar(): void {
    this.alerta = null;
    this.alertaService.limpar();
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
    if (this.timer) clearTimeout(this.timer);
  }
}
