import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { MedicamentoResponseDTO } from '../../../core/models/medicamento.model';

@Injectable({ providedIn: 'root' })
export class MedicamentoService {
  constructor(private api: ApiService) {}

  listar(): Observable<MedicamentoResponseDTO[]> {
    return this.api.get<MedicamentoResponseDTO[]>('/medicamentos');
  }

  excluir(id: string): Observable<void> {
    return this.api.delete<void>(`/medicamentos/${id}`);
  }
}
