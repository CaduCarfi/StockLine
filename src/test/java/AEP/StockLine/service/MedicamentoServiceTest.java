package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.mapper.MedicamentoMapper;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
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
}