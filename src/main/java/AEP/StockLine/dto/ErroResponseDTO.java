package AEP.StockLine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo padrão devolvido quando a API responde com erro.
 *
 * Por que existe:
 * sem ele, o 404 voltaria com corpo vazio ou com o HTML/JSON padrão do Spring,
 * que expõe detalhes internos. Com um DTO próprio, todo erro da API tem o mesmo
 * formato, e o front consegue tratar sempre do mesmo jeito.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErroResponseDTO {

    private Integer status;
    private String mensagem;
}
