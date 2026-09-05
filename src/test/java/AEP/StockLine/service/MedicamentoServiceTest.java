package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.mapper.MedicamentoMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private MedicamentoMapper medicamentoMapper;

    @InjectMocks
    private MedicamentoService medicamentoService;

    @Test
    void deveCadastrarMedicamentoComSucesso() {
        MedicamentoRequestDTO request = new MedicamentoRequestDTO(
                "Paracetamol",
                "Analgésico e antitérmico",
                100,
                LocalDate.of(2027, 5, 20),
                "LOT-2026-001"
        );

        Medicamento medicamentoParaSalvar = Medicamento.builder()
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 5, 20))
                .lote("LOT-2026-001")
                .build();

        Medicamento medicamentoSalvo = Medicamento.builder()
                .id("abc123")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 5, 20))
                .lote("LOT-2026-001")
                .build();

        MedicamentoResponseDTO responseEsperado = new MedicamentoResponseDTO(
                "abc123",
                "Paracetamol",
                "Analgésico e antitérmico",
                100,
                LocalDate.of(2027, 5, 20),
                "LOT-2026-001"
        );

        when(medicamentoMapper.toEntity(request)).thenReturn(medicamentoParaSalvar);
        when(medicamentoRepository.save(medicamentoParaSalvar)).thenReturn(medicamentoSalvo);
        when(medicamentoMapper.toResponseDTO(medicamentoSalvo)).thenReturn(responseEsperado);

        MedicamentoResponseDTO resultado = medicamentoService.cadastrar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo("abc123");
        assertThat(resultado.getNome()).isEqualTo("Paracetamol");
        assertThat(resultado.getQuantidade()).isEqualTo(100);

        verify(medicamentoMapper, times(1)).toEntity(request);
        verify(medicamentoRepository, times(1)).save(medicamentoParaSalvar);
        verify(medicamentoMapper, times(1)).toResponseDTO(medicamentoSalvo);
    }

    @Test
    void deveBuscarMedicamentoPorId() {
        Medicamento medicamento = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .quantidade(100)
                .build();

        when(medicamentoRepository.findById("1")).thenReturn(Optional.of(medicamento));

        MedicamentoResponseDTO resultado = medicamentoService.buscarPorId("1");

        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Paracetamol");
    }

    @Test
    void deveLancarExcecaoQuandoMedicamentoNaoExistir() {
        when(medicamentoRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicamentoService.buscarPorId("999"))
                .isInstanceOf(MedicamentoNotFoundException.class);
    }

    @Test
    void deveListarTodosOsMedicamentos() {
        Medicamento paracetamol = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .build();

        Medicamento dipirona = Medicamento.builder()
                .id("2")
                .nome("Dipirona")
                .build();

        when(medicamentoRepository.findAll()).thenReturn(List.of(paracetamol, dipirona));

        List<MedicamentoResponseDTO> resultado = medicamentoService.listarTodos();

        assertThat(resultado)
                .extracting(MedicamentoResponseDTO::getNome)
                .containsExactly("Paracetamol", "Dipirona");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMedicamentos() {
        when(medicamentoRepository.findAll()).thenReturn(List.of());

        assertThat(medicamentoService.listarTodos()).isEmpty();
    }
}