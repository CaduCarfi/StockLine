package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository repository;

    @InjectMocks
    private MedicamentoService service;

    @Test
    void deveBuscarMedicamentoPorId() {
        Medicamento medicamento = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .quantidade(100)
                .build();

        when(repository.findById("1")).thenReturn(Optional.of(medicamento));

        MedicamentoResponseDTO resultado = service.buscarPorId("1");

        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Paracetamol");
    }

    @Test
    void deveLancarExcecaoQuandoMedicamentoNaoExistir() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("999"))
                .isInstanceOf(MedicamentoNotFoundException.class);
    }
}
