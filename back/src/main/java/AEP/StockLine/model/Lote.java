package AEP.StockLine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lote {
    private String codigo;
    private LocalDate validade;
    private Integer saldo;
}
