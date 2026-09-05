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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository repository;

    @InjectMocks
    private MedicamentoService service;

    @Test
    void deveListarTodosOsMedicamentos() {
        Medicamento paracetamol = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 3, 31))
                .lote("L001")
                .build();

        Medicamento dipirona = Medicamento.builder()
                .id("2")
                .nome("Dipirona")
                .descricao("Analgésico")
                .quantidade(50)
                .validade(LocalDate.of(2028, 8, 31))
                .lote("L002")
                .build();

        when(repository.findAll()).thenReturn(List.of(paracetamol, dipirona));

        List<MedicamentoResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado)
                .extracting(MedicamentoResponseDTO::getNome)
                .containsExactly("Paracetamol", "Dipirona");

        verify(repository).findAll();
    }

    @Test
    void deveConverterTodosOsCamposDoMedicamentoParaOResponseDTO() {
        Medicamento medicamento = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 3, 31))
                .lote("L001")
                .build();

        when(repository.findAll()).thenReturn(List.of(medicamento));

        MedicamentoResponseDTO dto = service.listarTodos().getFirst();

        assertThat(dto.getId()).isEqualTo("1");
        assertThat(dto.getNome()).isEqualTo("Paracetamol");
        assertThat(dto.getDescricao()).isEqualTo("Analgésico e antitérmico");
        assertThat(dto.getQuantidade()).isEqualTo(100);
        assertThat(dto.getValidade()).isEqualTo(LocalDate.of(2027, 3, 31));
        assertThat(dto.getLote()).isEqualTo("L001");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMedicamentos() {
        when(repository.findAll()).thenReturn(List.of());

        List<MedicamentoResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).isEmpty();
        verify(repository).findAll();
    }

    @Test
    void deveBuscarMedicamentoPorId() {
        Medicamento medicamento = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 3, 31))
                .lote("L001")
                .build();

        when(repository.findById("1")).thenReturn(Optional.of(medicamento));

        MedicamentoResponseDTO resultado = service.buscarPorId("1");

        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Paracetamol");
        assertThat(resultado.getDescricao()).isEqualTo("Analgésico e antitérmico");
        assertThat(resultado.getQuantidade()).isEqualTo(100);
        assertThat(resultado.getValidade()).isEqualTo(LocalDate.of(2027, 3, 31));
        assertThat(resultado.getLote()).isEqualTo("L001");

        verify(repository).findById("1");
    }

    @Test
    void deveLancarExcecaoQuandoMedicamentoNaoForEncontrado() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("999"))
                .isInstanceOf(MedicamentoNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository).findById("999");
    }
}
