package AEP.StockLine.exception;

import AEP.StockLine.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicamentoNotFoundException.class)
    public ResponseEntity<ErroResponseDTO> tratarMedicamentoNaoEncontrado(
            MedicamentoNotFoundException excecao) {

        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                excecao.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErroResponseDTO> tratarCategoriaNaoEncontrada(CategoriaNotFoundException excecao) {
        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                excecao.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(EstoqueException.class)
    public ResponseEntity<ErroResponseDTO> tratarEstoque(EstoqueException excecao) {
        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(), 400, "Bad Request", excecao.getMessage());
        return ResponseEntity.badRequest().body(erro);
    }
    @ExceptionHandler(QuantidadeInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> tratarQuantidadeInvalida(QuantidadeInvalidaException excecao) {
        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(), 400, "Bad Request", excecao.getMessage());
        return ResponseEntity.badRequest().body(erro);
    }
}
