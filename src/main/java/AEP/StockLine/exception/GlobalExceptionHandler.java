package AEP.StockLine.exception;

import AEP.StockLine.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicamentoNotFoundException.class)
    public ResponseEntity<ErroResponseDTO> tratarMedicamentoNaoEncontrado(
            MedicamentoNotFoundException excecao) {

        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                excecao.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // Novo: trata o ajuste de quantidade inválido, devolvendo 400 em vez de 500
    @ExceptionHandler(QuantidadeInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> tratarQuantidadeInvalida(
            QuantidadeInvalidaException excecao) {

        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),   // 400
                excecao.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}