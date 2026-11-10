package AEP.StockLine.service;

import AEP.StockLine.dto.BaixaRequestDTO;
import AEP.StockLine.exception.EstoqueException;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaixaServiceTest {
    @Mock private MedicamentoRepository repository;
    private EntradaService service;
    private Medicamento medicamento;
    private final LocalDate validade = LocalDate.of(2027, 8, 15);

    @BeforeEach
    void preparar() {
        service = new EntradaService(repository);
        medicamento = Medicamento.builder().id("m1").quantidade(30).build();
        medicamento.getLotes().add(new Lote("L1", validade, 10));
        medicamento.getLotes().add(new Lote("L2", validade.plusMonths(1), 20));
    }

    private void simularBanco() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        when(repository.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void deveBaixarSomenteOLoteEscolhidoERegistrarMovimentacao() {
        simularBanco();
        var resposta = service.registrarBaixa("m1", new BaixaRequestDTO("L1", 4));
        assertThat(resposta.getQuantidade()).isEqualTo(26);
        assertThat(resposta.getLotes()).containsExactly(
                new Lote("L1", validade, 6), new Lote("L2", validade.plusMonths(1), 20));
        assertThat(medicamento.getMovimentacoes()).hasSize(1);
        var baixa = medicamento.getMovimentacoes().getFirst();
        assertThat(baixa.getTipo()).isEqualTo("BAIXA");
        assertThat(baixa.getLote()).isEqualTo("L1");
        assertThat(baixa.getQuantidade()).isEqualTo(4);
        assertThat(baixa.getData()).isNotNull();
    }

    @Test
    void devePermitirZerarOLoteSemRemoveLo() {
        simularBanco();
        service.registrarBaixa("m1", new BaixaRequestDTO("L1", 10));
        assertThat(medicamento.getLotes()).hasSize(2);
        assertThat(medicamento.getLotes().getFirst().getSaldo()).isZero();
        assertThat(medicamento.getQuantidade()).isEqualTo(20);
    }

    @Test
    void naoDeveUsarSaldoDeOutroLote() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        assertThatThrownBy(() -> service.registrarBaixa("m1", new BaixaRequestDTO("L1", 11)))
                .isInstanceOf(EstoqueException.class);
        assertThat(medicamento.getQuantidade()).isEqualTo(30);
        assertThat(medicamento.getLotes().getFirst().getSaldo()).isEqualTo(10);
        assertThat(medicamento.getMovimentacoes()).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveAceitarLoteInexistente() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        assertThatThrownBy(() -> service.registrarBaixa("m1", new BaixaRequestDTO("X", 1)))
                .isInstanceOf(EstoqueException.class);
        assertThat(medicamento.getMovimentacoes()).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveAceitarQuantidadeInvalida() {
        for (Integer quantidade : Arrays.asList(null, 0, -1)) {
            assertThatThrownBy(() -> service.registrarBaixa("m1", new BaixaRequestDTO("L1", quantidade)))
                    .isInstanceOf(EstoqueException.class);
        }
        verifyNoInteractions(repository);
    }

    @Test
    void naoDeveAceitarLoteAusente() {
        for (String lote : Arrays.asList(null, "", " ")) {
            assertThatThrownBy(() -> service.registrarBaixa("m1", new BaixaRequestDTO(lote, 1)))
                    .isInstanceOf(EstoqueException.class);
        }
        verifyNoInteractions(repository);
    }

    @Test
    void naoDeveAceitarMedicamentoInexistente() {
        assertThatThrownBy(() -> service.registrarBaixa("x", new BaixaRequestDTO("L1", 1)))
                .isInstanceOf(MedicamentoNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveAproveitarSaldoDoCadastroAntigo() {
        simularBanco();
        medicamento.setQuantidade(10);
        medicamento.setLote("ANTIGO");
        medicamento.setValidade(validade);
        medicamento.setLotes(null);
        medicamento.setMovimentacoes(null);
        service.registrarBaixa("m1", new BaixaRequestDTO("ANTIGO", 3));
        assertThat(medicamento.getQuantidade()).isEqualTo(7);
        assertThat(medicamento.getLotes()).containsExactly(new Lote("ANTIGO", validade, 7));
        assertThat(medicamento.getMovimentacoes()).hasSize(1);
    }

    @Test
    void devePermitirBaixaDeLoteVencidoParaDescarte() {
        simularBanco();
        medicamento.getLotes().getFirst().setValidade(LocalDate.of(2020, 1, 1));
        service.registrarBaixa("m1", new BaixaRequestDTO("L1", 2));
        assertThat(medicamento.getLotes().getFirst().getSaldo()).isEqualTo(8);
    }
}
