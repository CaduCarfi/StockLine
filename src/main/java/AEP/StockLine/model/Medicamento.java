package AEP.StockLine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "medicamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicamento {

    @Id
    private String id;

    private String nome;
    private String descricao;
    private Integer quantidade;
    private LocalDate validade;
    private String lote;
}
