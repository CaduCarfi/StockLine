import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { CategoriaResponseDTO } from '../../../core/models/categoria.model';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  constructor(private api: ApiService) {}

  listar(): Observable<CategoriaResponseDTO[]> {
    return this.api.get<CategoriaResponseDTO[]>('/categorias');
  }
}
