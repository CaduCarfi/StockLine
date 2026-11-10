package AEP.StockLine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import AEP.StockLine.model.Lote;
import java.util.List;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicamentoResponseDTO {

    private String id;
    private String nome;
    private String descricao;
    private Integer quantidade;
    private LocalDate validade;
    private String lote;
    private Integer estoqueMinimo;
    private String categoriaId;
    private List<Lote> lotes;

    public MedicamentoResponseDTO(String id, String nome, String descricao, Integer quantidade,
            LocalDate validade, String lote, Integer estoqueMinimo, String categoriaId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.validade = validade;
        this.lote = lote;
        this.estoqueMinimo = estoqueMinimo;
        this.categoriaId = categoriaId;
    }
}
