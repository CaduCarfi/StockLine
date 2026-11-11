package AEP.StockLine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErroResponseDTO {

    private LocalDateTime timestamp;
    private Integer status;
    private String erro;
    private String mensagem;
}
