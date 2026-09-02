package AEP.StockLine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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
}
