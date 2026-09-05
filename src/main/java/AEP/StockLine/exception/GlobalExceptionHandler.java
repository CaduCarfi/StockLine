package AEP.StockLine.exception;

import AEP.StockLine.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Ponto único que transforma exceções da aplicação em respostas HTTP.
 *
 * Por que existe:
 * sem esta classe, uma MedicamentoNotFoundException escapando do controller
 * viraria 500 (Internal Server Error), que é errado — o servidor não falhou, o
 * recurso é que não existe. O certo é 404.
 *
 * @RestControllerAdvice faz o Spring aplicar isto a todos os controllers, então
 * o tratamento de erro fica em um lugar só, em vez de espalhar try/catch em
 * cada endpoint.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Diz ao Spring: sempre que esta exceção for lançada, chame este método.
    @ExceptionHandler(MedicamentoNotFoundException.class)
    public ResponseEntity<ErroResponseDTO> tratarMedicamentoNaoEncontrado(
            MedicamentoNotFoundException excecao) {

        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.NOT_FOUND.value(),   // 404
                excecao.getMessage()            // mensagem definida na exceção
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
