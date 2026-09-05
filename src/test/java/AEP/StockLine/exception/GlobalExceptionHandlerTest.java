package AEP.StockLine.exception;

import AEP.StockLine.dto.ErroResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void deveDevolverNotFound() {
        ResponseEntity<ErroResponseDTO> resposta = new GlobalExceptionHandler()
                .tratarMedicamentoNaoEncontrado(new MedicamentoNotFoundException("999"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
