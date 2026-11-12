import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';

export interface LoteVencimento {
  medicamentoId: string;
  medicamentoNome: string;
  lote: string;
  quantidade: number;
  validade: string;
}

@Injectable({ providedIn: 'root' })
export class LoteService {
  constructor(private api: ApiService) {}

  buscarVencidos(): Observable<LoteVencimento[]> {
    return this.api.get<LoteVencimento[]>('/lotes/vencidos');
  }

  buscarAVencer(dias: number = 30): Observable<LoteVencimento[]> {
    return this.api.get<LoteVencimento[]>(`/lotes/a-vencer?dias=${dias}`);
  }
}
