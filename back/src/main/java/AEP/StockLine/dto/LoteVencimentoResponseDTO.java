package AEP.StockLine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteVencimentoResponseDTO {
    private String medicamentoId;
    private String medicamentoNome;
    private String lote;
    private Integer quantidade;
    private LocalDate validade;
}
