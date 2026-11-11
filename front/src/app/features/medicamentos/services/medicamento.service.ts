import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { MedicamentoResponseDTO } from '../../../core/models/medicamento.model';

export interface MedicamentoForm {
  nome: string;
  descricao: string;
  quantidade: number | null;
  validade: string;
  lote: string;
  categoriaId: string;
  estoqueMinimo: number | null;
}

@Injectable({ providedIn: 'root' })
export class MedicamentoService {
  constructor(private api: ApiService) {}

  listar(): Observable<MedicamentoResponseDTO[]> {
    return this.api.get<MedicamentoResponseDTO[]>('/medicamentos');
  }

  buscarPorId(id: string): Observable<MedicamentoResponseDTO> {
    return this.api.get<MedicamentoResponseDTO>(`/medicamentos/${id}`);
  }

  criar(dto: MedicamentoForm): Observable<MedicamentoResponseDTO> {
    return this.api.post<MedicamentoResponseDTO>('/medicamentos', dto);
  }

  atualizar(id: string, dto: MedicamentoForm): Observable<MedicamentoResponseDTO> {
    return this.api.put<MedicamentoResponseDTO>(`/medicamentos/${id}`, dto);
  }

  excluir(id: string): Observable<void> {
    return this.api.delete<void>(`/medicamentos/${id}`);
  }
}
