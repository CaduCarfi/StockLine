package AEP.StockLine.controller;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.service.MedicamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoControllerTest {

    @Mock
    private MedicamentoService service;

    @InjectMocks
    private MedicamentoController controller;

    @Test
    void deveRetornarOMedicamentoQuandoOIdExistir() {
        MedicamentoResponseDTO paracetamol = new MedicamentoResponseDTO(
                "1", "Paracetamol", "Analgésico", 100, LocalDate.of(2027, 3, 31), "L001");

        when(service.buscarPorId("1")).thenReturn(paracetamol);

        ResponseEntity<MedicamentoResponseDTO> resposta = controller.buscarPorId("1");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().getNome()).isEqualTo("Paracetamol");
    }

    @Test
    void deveLancarExcecaoQuandoOIdNaoExistir() {
        when(service.buscarPorId("999"))
                .thenThrow(new MedicamentoNotFoundException("999"));

        assertThatThrownBy(() -> controller.buscarPorId("999"))
                .isInstanceOf(MedicamentoNotFoundException.class);
    }

    @Test
    void deveRetornarAListaDeMedicamentos() {
        MedicamentoResponseDTO paracetamol = new MedicamentoResponseDTO(
                "1", "Paracetamol", "Analgésico", 100, LocalDate.of(2027, 3, 31), "L001");

        MedicamentoResponseDTO dipirona = new MedicamentoResponseDTO(
                "2", "Dipirona", "Analgésico", 50, LocalDate.of(2028, 8, 31), "L002");

        when(service.listarTodos()).thenReturn(List.of(paracetamol, dipirona));

        ResponseEntity<List<MedicamentoResponseDTO>> resposta = controller.listar();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody())
                .extracting(MedicamentoResponseDTO::getNome)
                .containsExactly("Paracetamol", "Dipirona");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMedicamentos() {
        when(service.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<MedicamentoResponseDTO>> resposta = controller.listar();

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEmpty();
    }

    @Test
    void deveCadastrarMedicamentoComSucesso() {
        MedicamentoRequestDTO request = new MedicamentoRequestDTO(
                "Paracetamol",
                "Analgésico e antitérmico",
                100,
                LocalDate.of(2027, 5, 20),
                "LOT-2026-001"
        );

        MedicamentoResponseDTO responseEsperado = new MedicamentoResponseDTO(
                "abc123",
                "Paracetamol",
                "Analgésico e antitérmico",
                100,
                LocalDate.of(2027, 5, 20),
                "LOT-2026-001"
        );

        when(service.cadastrar(request)).thenReturn(responseEsperado);

        ResponseEntity<MedicamentoResponseDTO> resposta = controller.cadastrar(request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getId()).isEqualTo("abc123");
        assertThat(resposta.getBody().getNome()).isEqualTo("Paracetamol");
        assertThat(resposta.getBody().getQuantidade()).isEqualTo(100);
    }
}
