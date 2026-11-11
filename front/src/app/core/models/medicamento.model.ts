import { Lote } from './lote.model';

export interface MedicamentoResponseDTO {
  id: string;
  nome: string;
  descricao: string;
  quantidade: number;
  validade: string;
  lote: string;
  categoriaId: string;
  estoqueMinimo: number | null;
  lotes: Lote[];
}
