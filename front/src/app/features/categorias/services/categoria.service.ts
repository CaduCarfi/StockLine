import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { CategoriaResponseDTO } from '../../../core/models/categoria.model';

export interface CategoriaForm {
  nome: string;
  descricao: string;
}

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  constructor(private api: ApiService) {}

  listar(): Observable<CategoriaResponseDTO[]> {
    return this.api.get<CategoriaResponseDTO[]>('/categorias');
  }

  criar(dto: CategoriaForm): Observable<CategoriaResponseDTO> {
    return this.api.post<CategoriaResponseDTO>('/categorias', dto);
  }

  atualizar(id: string, dto: CategoriaForm): Observable<CategoriaResponseDTO> {
    return this.api.put<CategoriaResponseDTO>(`/categorias/${id}`, dto);
  }

  excluir(id: string): Observable<void> {
    return this.api.delete<void>(`/categorias/${id}`);
  }
}
