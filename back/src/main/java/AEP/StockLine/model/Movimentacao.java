package AEP.StockLine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movimentacao {
    private String tipo;
    private String lote;
    private Integer quantidade;
    private LocalDateTime data;
}
