package AEP.StockLine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categorias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {
    @Id
    private String id;
    private String nome;
    private String descricao;
}
